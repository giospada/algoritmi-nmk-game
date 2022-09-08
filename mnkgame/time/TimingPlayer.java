package mnkgame.time;

import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

/**
 * Questa classe è utilizzata per fare una stima della capacità di esecuzione del computer remoto
 * in modo che dinamicamente calcoli profondità e branching factor migliore
 */
class TimingPlayer{

    private long timeStart;
    private final long TIMEOUT;
    private Board B;
    private int moves;

    private int BRANCHING_FACTOR = 9;
    private int DEPTH_LIMIT = 10;
    
    TimingPlayer(long timeStart, int timeout_in_secs, int M, int N, int K) {
        this(timeStart, timeout_in_secs, new Board(M, N, K, MNKCellState.P1));
    }  
    
    TimingPlayer(long timeStart, int timeout_in_secs,Board board) {
        this.timeStart = timeStart;
        this.TIMEOUT = timeout_in_secs;
        this.B = board;        
        this.moves = 0;
    }

    boolean hasEnded(int depth) {
        return depth == DEPTH_LIMIT || hasTimeRunOut() || B.gameState() != MNKGameState.OPEN;
    }


     /**
     * trova mossa migliore con alfa beta pruning
     * @return
     */
    public void findBestTime() {
        int len = Math.min(BRANCHING_FACTOR * 3, B.freeCellsCount);
        for (int i = 0; i < len; i++) {
            if (hasTimeRunOut()) return;
            
            HeuristicCell currCell = B.getGreatKCell(i);
            B.markCell(currCell.i, currCell.j);
            minPlayer(1);
            B.unmarkCell();
            moves++;
        }
    }    

    public void minPlayer(int depth) {
        if (hasEnded(depth)) { 
            return;
        }
        int len = Math.min(BRANCHING_FACTOR, B.freeCellsCount);
        for (int i = 0; i < len; i++) {
            B.markCell(B.getGreatKCell(i));
            maxPlayer(depth + 1);
            B.unmarkCell();
            moves++;
        }
    }

    private void maxPlayer(int depth) {
        if(hasEnded(depth))
            return;

        int len = Math.min(BRANCHING_FACTOR, B.freeCellsCount);
        
        for (int i = 0; i < len; i++) {
            if(hasTimeRunOut()) return; 

            B.markCell(B.getGreatKCell(i));
            minPlayer(depth + 1);
            B.unmarkCell();
            moves++;
        }
    }


    private boolean hasTimeRunOut() {
        return (System.currentTimeMillis() - timeStart) / 1000.0 > TIMEOUT * (90.0 / 100.0);
    }

    public int getMoves() {
        return this.moves;
    }
    
    public int getBranchingFactor() {   
        //TODO: calcolarlo
        return 0;  // stub
    }
    public int getDephtLimit(){
        //TODO: calcolarlo
        return 0;  // stub
    }

}