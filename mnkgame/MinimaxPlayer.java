package mnkgame;

public class MinimaxPlayer implements MNKPlayer {
    private MNKBoard Board;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private MNKGameState gameState;
    private int TIMEOUT;
    private long startTime;
    private boolean has_timeout;
    private final int kinf = 2;

    public MinimaxPlayer() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        Board = new MNKBoard(M, N, K);
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
    public int minPlayer(MNKCell[] actions, int alpha, int beta) {
        has_timeout = (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0);
        if (gameState != MNKGameState.OPEN || has_timeout) {
            return getValue();
        }

        int v = kinf;
        for (int i = 0; i < actions.length && !has_timeout; i++) {
            gameState = Board.markCell(actions[i].i, actions[i].j);
            MNKCell newActions[] = getFreeCellsAfterAction(actions, i);
            int maxPlayerValue = maxPlayer(newActions, alpha, beta);
            Board.unmarkCell();

            if (maxPlayerValue < v) {
                v = maxPlayerValue;
                beta = Math.min(beta, v);
            }

            // TODO: sarebbe buono provare a fare una ordering, sul principio della late move reduction.
            if (v <= alpha)
                return v;
        }
        return v;
    }

    private int maxPlayer(MNKCell[] actions, int alpha, int beta) {
        has_timeout = (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0);
        if (gameState != MNKGameState.OPEN || has_timeout) {
            return getValue();
        }

        int v = -kinf;
        for (int i = 0; i < actions.length && !has_timeout; i++) {
            gameState = Board.markCell(actions[i].i, actions[i].j);
            MNKCell newActions[] = getFreeCellsAfterAction(actions, i);
            int minPlayerValue = minPlayer(newActions, alpha, beta);
            Board.unmarkCell();

            if (minPlayerValue > v) {
                v = minPlayerValue;
                alpha = Math.max(alpha, v);
            }

            if (v >= beta)
                return v;
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
            MNKCell newActions[] = getFreeCellsAfterAction(freeCells, i);
            int minPlayerValue = minPlayer(newActions, alpha, kinf);
            Board.unmarkCell();

            if (minPlayerValue > v) {
                v = minPlayerValue;
                bestCell = freeCells[i];
                alpha = Math.max(alpha, v);
            }
            // non faccio check sull'alpha beta al primo livello, perché tanto è impossibile
            // che sia verificato, beta è sempre kinf
        }
        Board.markCell(bestCell.i, bestCell.j);
        has_timeout = false;
        return bestCell;
    }

    public String playerName() {
        return "MiniMaxPlayer";
    }

    private MNKCell[] getFreeCellsAfterAction(MNKCell[] oldFreeCells, int actionIdx) {
        MNKCell[] freeCells = new MNKCell[oldFreeCells.length - 1];
        int i = 0;
        for (int j = 0; j < oldFreeCells.length; j++) {
            if (actionIdx == j)
                continue;
            freeCells[i] = oldFreeCells[j];
            i++;
        }
        return freeCells;
    }
}
