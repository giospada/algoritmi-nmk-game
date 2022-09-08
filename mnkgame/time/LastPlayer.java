package mnkgame.time;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

public class LastPlayer implements mnkgame.MNKPlayer {
    private Board B;
    private MNKGameState myWin;
    private MNKCellState myState;
    private MNKCellState yourState;
    private MNKGameState yourWin;
    private MNKGameState gameState;
    private final int KINF = Integer.MAX_VALUE;

    private int BRANCHING_FACTOR = 7;
    private int DEPTH_LIMIT = 8;
    private int maxNumberOfMoves;
    
    // mosse massime per il tree attuale
    private int maxMovesCurrentTree;
    
    // mosse del tree attuale
    private int movesCurrentTree;


    private final boolean DEBUG = false;

    private int M, N;
    // l'algoritmo si comporta in modo molto strano alle prime mosse
    private boolean firstMove;
    public LastPlayer() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        long timeStart = System.currentTimeMillis();
        this.myState = first ? MNKCellState.P1 : MNKCellState.P2;
        this.yourState = first ? MNKCellState.P2 : MNKCellState.P1;
        this.B = new Board(M, N, K, myState);
        this.myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        this.yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;

        firstMove = true;
        this.M = M;
        this.N = N;

        TimingPlayer timing = new TimingPlayer(timeStart, timeout_in_secs , B);
        timing.findBestTime();
        // TimingPlayer timing = new TimingPlayer(timeStart, timeout_in_secs, M, N, K);
        //BRANCHING_FACTOR = timing.getBranchingFactor();
        //DEPTH_LIMIT = timing.getDephtLimit();
        this.maxNumberOfMoves = timing.getMoves();
        this.maxMovesCurrentTree = 0;
        this.movesCurrentTree = 0;
    }

    public int minPlayer(int depth, int alpha, int beta) {
        if (depth == DEPTH_LIMIT) {  // TODO: check when the board is in end state (depth time, state)
            return B.getValue(yourState);  // todo get the heuristic value of this game state
        }else if(gameState == myWin){
            return KINF - 1;
        } else if (gameState == yourWin) {
            return -KINF + 1;
        }else if ( gameState  == MNKGameState.DRAW || B.isForcedDraw()){
            return 0;
        }
        
        
        int v = KINF;
        
        int len = Math.min(BRANCHING_FACTOR, B.freeCellsCount);
        
        for (int i = 0; i < len; i++) {
            if (movesCurrentTree >= maxMovesCurrentTree) {
                break;
            }

            gameState = B.markCell(B.getGreatKCell(i));
            int maxPlayerValue = maxPlayer(depth + 1, alpha, beta);
            B.unmarkCell();
            movesCurrentTree++;
            
            if (maxPlayerValue < v) {
                v = maxPlayerValue;
                beta = Math.min(beta, v);
            }

            // TODO: sarebbe buono provare a fare una ordering, sul principio della late move reduction.
            if (v <= alpha)
                return v;
        }
        
        if(v == KINF){
            return B.getValue(myState);
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
        } else if (gameState == MNKGameState.DRAW || B.isForcedDraw()) {
            return 0;
        }

        int v = -KINF;
        
        int len = Math.min(BRANCHING_FACTOR, B.freeCellsCount);
        
        for (int i = 0; i < len; i++) {
            if (movesCurrentTree >= maxMovesCurrentTree) {
                break;
            }

            gameState = B.markCell(B.getGreatKCell(i));
            int minPlayerValue = minPlayer(depth + 1, alpha, beta);
            B.unmarkCell();
            
            movesCurrentTree++;
            

            if (minPlayerValue > v) {
                v = minPlayerValue;
                alpha = Math.max(alpha, v);
            }

            if (v >= beta)
                return v;
        }

        if(v == -KINF) {
            return B.getValue(myState);
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
        maxMovesCurrentTree = maxNumberOfMoves / 4;

        int toAddEachStep;
        if (len <= 1)
            toAddEachStep = maxNumberOfMoves;
        else
            toAddEachStep = (maxNumberOfMoves - maxMovesCurrentTree)/ (len - 1);

        if (DEBUG) {
            System.out.println("maxMovesCurrentTree: " + maxNumberOfMoves);
            System.out.println("toAddEachStep: " + toAddEachStep);
        }

        for (int i = 0; i < len; i++) {
            movesCurrentTree = 0;
            HeuristicCell currCell = B.getGreatKCell(i);
            gameState = B.markCell(currCell.i, currCell.j);
            int minPlayerValue = minPlayer(1, alpha, beta);
            B.unmarkCell();

            
            if (DEBUG){
                System.out.println("cella: " + currCell.i + " " + currCell.j + " valore: " + minPlayerValue);
                System.out.format("usate %d mosse su %d\n", movesCurrentTree, maxMovesCurrentTree);
            }

            if (minPlayerValue > v) {
                v = minPlayerValue;
                cell = currCell.toMNKCell();
                alpha = Math.max(alpha, v);
            }

            // quelli rimasti nell'iterazione precendente + numero da aggiungere ogni step
            
            maxMovesCurrentTree = (maxMovesCurrentTree - movesCurrentTree) + toAddEachStep;
        }
        return cell;
    }    

    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
            firstMove = false;
        }

        if (DEBUG) {
            // B.print();
            // B.printHeuristics(true);
            // B.printHeuristics(false);
        }

        // MNKCell priorityCell=checkEasyState();
        // if(priorityCell != null){
        //     B.markCell(priorityCell.i, priorityCell.j,true);
        //     return priorityCell;
        // }

        // Priority 5, find the best cell to win in the most number of possible ways
        // System.out.println("No winning cell found, selecting with mini-max ab");
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
