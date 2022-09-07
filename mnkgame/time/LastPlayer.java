package mnkgame.time;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

// X X E -> 1 + 2 (per le due X) | -> KINF - 1

public class LastPlayer implements mnkgame.MNKPlayer {
    private Board B;
    private MNKGameState myWin;
    private MNKCellState myState;
    private MNKCellState yourState;
    private MNKGameState yourWin;
    private MNKGameState gameState;
    private final int BRANCHING_FACTOR = 9;
    private final int DEPTH_LIMIT = 10;
    private final int KINF = Integer.MAX_VALUE;

    private int M, N;
    // l'algoritmo si comporta in modo molto strano alle prime mosse
    private boolean firstMove;
    public LastPlayer() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        myState = first ? MNKCellState.P1 : MNKCellState.P2;
        yourState = first ? MNKCellState.P2 : MNKCellState.P1;
        B = new Board(M, N, K, myState);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;

        firstMove = true;
        this.M = M;
        this.N = N;

        // TODO: calcola la profondità massima esplorabile, con numero nodi esplorabili dal 
        // computer del prof e così calcola quanto deep può andare
    }

    public int minPlayer(int depth, int alpha, int beta) {
        if (depth == DEPTH_LIMIT) {  // TODO: check when the board is in end state (depth time, state)
            return B.getValue(yourState);  // todo get the heuristic value of this game state
        }else if(gameState == myWin ){
            return KINF - 1;
        } else if (gameState == yourWin) {
            return -KINF + 1;
        }else if ( gameState  == MNKGameState.DRAW){
            return 0;
        }
        
        
        int v = KINF;
        
        int len = Math.min(BRANCHING_FACTOR, B.freeCellsCount);
        
        for (int i = 0; i < len; i++) {
            gameState = B.markCell(B.getGreatKCell(i));
            int maxPlayerValue = maxPlayer(depth + 1, alpha, beta);
            B.unmarkCell();

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

    private int maxPlayer(int depth, int alpha, int beta) {
        if (depth == DEPTH_LIMIT) {  // TODO: check when the board is in end state (depth time, state)
            return B.getValue(myState);
        } else if(gameState == myWin){
            return KINF - 1;            
        } else if (gameState == yourWin) {
            return -KINF + 1;
        } else if (gameState == MNKGameState.DRAW) {
            return 0;
        }

        int v = -KINF;
        
        int len = Math.min(BRANCHING_FACTOR, B.freeCellsCount);
        
        for (int i = 0; i < len; i++) {
            gameState = B.markCell(B.getGreatKCell(i).i, B.getGreatKCell(i).j);
            int minPlayerValue = minPlayer(depth + 1, alpha, beta);
            B.unmarkCell();

            if (minPlayerValue > v) {
                v = minPlayerValue;
                alpha = Math.max(alpha, v);
            }

            if (v >= beta)
                return v;
        }
        return v;
    }

  

    /**
     * trova mossa migliore con alfa beta pruning
     * @return
     */
    private MNKCell findBestMove() {
        int alpha = -KINF;
        int beta = KINF;
        int v = -KINF;

        MNKCell cell = null;

        // al primo livello valuto quasi tutto
        int len = Math.min(BRANCHING_FACTOR * 3, B.freeCellsCount);

        for (int i = 0; i < len; i++) {
            HeuristicCell currCell = B.getGreatKCell(i);
            gameState = B.markCell(currCell.i, currCell.j);

            int minPlayerValue = minPlayer(1, alpha, beta);

            if (minPlayerValue > v) {
                v = minPlayerValue;
                cell = currCell.toMNKCell();
                alpha = Math.max(alpha, v);
            }
        }
        return cell;
    }    

    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
            firstMove = false;
        }

        B.print();
        B.printHeuristics();

        // MNKCell priorityCell=checkEasyState();
        // if(priorityCell != null){
        //     B.markCell(priorityCell.i, priorityCell.j,true);
        //     return priorityCell;
        // }

        // Priority 5, find the best cell to win in the most number of possible ways
        // TODO: Probably using minimax
        System.out.println("No winning cell found, selecting with mini-max ab");
        MNKCell bestCell = findBestMove();
        B.markCell(bestCell.i, bestCell.j);

        return bestCell;
    }

    public String playerName() {
        return "Mics Player v2";
    }

    /**
     * @brief guarda se le mosse che portano sicuramente ad una vittoria / ad una perdita
     * @return la cella o null se non ha trovato nulla
     */
    private MNKCell checkEasyState() {
        if (firstMove) {
            MNKCell bestCell = new MNKCell((M - 1) / 2, (N - 1) / 2);
            firstMove = false;
            return bestCell;
        }

        System.out.println("Playing");
        // TODO: Potrebbe essere che la cella migliore abbia sempre i valore euristico più alto
        // per cui si potrebbe fare un check costante invece che lineare come qui.
        // In pratica prendi la tua cella migliore, quella del nemico guardi se sono in uno dei casi di priorità 1-4
        // in quel caso esegui quella che ha piu priorità

        // Priority 1: Win
        MNKCell winCell = findWinCell();
        if (winCell != null) {
            System.out.println("Winning cell found");
            return winCell;
        }

        // Priority 2, prevent the opponent from winning
        B.setPlayer(yourState);
        MNKCell preventWinCell = findPreventWinCell();
        if (preventWinCell != null) {
            System.out.println("Preventing win cell found");
            B.setPlayer(myState);
            return preventWinCell;
        }

        // Priority 3, find the best cell to fork (two or more winning ways)
        winCell = findMyDoublePlay();
        if (winCell != null) {
            System.out.println("Double play cell found");
            return winCell;
        }

        // Priority 4, find the best cell to block the opponent's fork
        preventWinCell = findEnemyDoublePlay();
        if (preventWinCell != null) {
            System.out.println("Preventing double play cell found");
            return preventWinCell;
        }
        return null;
    }
    
    // time should never run out right? it's the first step!
    // @returns a winning cell if there is one
    private MNKCell findWinCell() {
        for (int i = 0; i < B.freeCellsCount; i++) {
            HeuristicCell cell = B.getIthCell(i);
            if (B.markCell(cell.i, cell.j) == myWin) {
                B.unmarkCell();
                return cell.toMNKCell();
            } else {
                B.unmarkCell();
            }
        }
        return null;
    }

    private MNKCell findPreventWinCell() {
        for (int i = 0; i < B.freeCellsCount; i++) {
            HeuristicCell cell = B.getIthCell(i);
            if (B.markCell(cell.i, cell.j) == yourWin) {
                B.unmarkCell();
                return cell.toMNKCell();
            }
            B.unmarkCell();
        }
        return null;
    }

    private MNKCell findMyDoublePlay() {
        MNKCell winningCell = null;
        for (int i = 0; i < B.freeCellsCount; i++) {
            HeuristicCell cell = B.getIthCell(i);
            Value value = B.getCellValue(cell.i, cell.j, myState);
            if (value.isDoublePlay()) {
                winningCell = cell.toMNKCell();
            }

            if (winningCell != null) break;
        }
        return winningCell;
    }

    private MNKCell findEnemyDoublePlay() {
        MNKCell winningCell = null;
        for (int i = 0; i < B.freeCellsCount; i++) {
            HeuristicCell cell = B.getIthCell(i);
            Value value = B.getCellValue(cell.i, cell.j, yourState);
            if (value.isDoublePlay()) {
                winningCell = cell.toMNKCell();
            }

            if (winningCell != null) break;
        }

        return winningCell;
    }
}
