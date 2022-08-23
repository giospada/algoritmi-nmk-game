package mnkgame;

public class BoardMinimaxPlayer implements MNKPlayer {
    private CBoard Board;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private MNKGameState gameState;
    private int TIMEOUT;
    private long startTime;
    private boolean has_timeout;
    private final int kinf = 2;

    public BoardMinimaxPlayer() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        Board = new CBoard(M, N, K);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        TIMEOUT = timeout_in_secs;
        has_timeout = false;
    }

    public int getValue() {
        if (gameState == myWin) {
            return 1;
        } else if (gameState == yourWin) {
            return -1;
        } else {
            return 0;
        }
    }

    // il giocatore minimo
    public int minPlayer(int alpha, int beta) {
        has_timeout = (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0);
        if (gameState != MNKGameState.OPEN || has_timeout) {
            return getValue();
        }

        int v = kinf;
        for (CCell cell : Board.getFreeCell()) {
            if (has_timeout) {
                break;
            }

            gameState = Board.markCell(cell.getPosition());
            v = Math.min(v, maxPlayer(alpha, beta));
            Board.unmarkCell();
            if (v <= alpha)
                return v;
            beta = Math.min(beta, v);
        }
        return v;
    }

    // nel minPlayer e maxPlayer sarebbe meglio sostituire MNKCELL[] action, con una linked list
    // poichè il costo di scorrerla è n ma tanto bisogna farlo, e l'inserimento e la rimozione è O(1)
    // che viene comodo quando la si passa da una funzione all'altra

    // il giocatore massimo
    private int maxPlayer(int alpha, int beta) {
        has_timeout = (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0);
        if (gameState != MNKGameState.OPEN || has_timeout) {
            return getValue();
        }

        int v = -kinf;
        for (CCell cell : Board.getFreeCell()) {
            if (has_timeout) {
                // in teoria se fa break prima di aver fatto una mossa, ritorna un risultato
                // invalido, il min player vedrebbe la mossa che chiamato questo come la mossa migliore
                // quindi sceglie per forza questo, ma la probabilità che succeda, dato il check iniziale
                // di sopra è molto bassa, credo impossibile, solo causato da grande sfortuna.
                break;  
            }

            gameState = Board.markCell(cell.getPosition());
            v = Math.max(v, minPlayer(alpha, beta));
            Board.unmarkCell();
            if (v >= beta)
                return v;
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    // utilizziamo la board globale per aggiungere e togliere e ci fermiamo quando uno vince
    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        startTime = System.currentTimeMillis();
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            Board.markCell(c.i, c.j); // Save the last move in the local MNKBoard
        }

        MNKCell bestCell = freeCells[0];
        int v = -kinf;
        int alpha = -kinf;

        // questo è come se fosse un max player, ma tiene in conto anche della cella
        for (int i = 0; i < freeCells.length && !has_timeout; i++) {
            gameState = Board.markCell(freeCells[i].i, freeCells[i].j);
            int minPlayerValue = minPlayer(alpha, kinf);
            Board.unmarkCell();
            if (minPlayerValue > v) {
                v = minPlayerValue;
                bestCell = freeCells[i];
            }

            alpha = Math.max(alpha, v);
            // non faccio check sull'alpha beta al primo livello, perché tanto è impossibile
            // che sia verificato, beta è sempre kinf
        }
        Board.markCell(bestCell.i, bestCell.j);
        has_timeout = false;
        return bestCell;
    }

    public String playerName() {
        return "CBoardMiniMaxPlayer";
    }
}
