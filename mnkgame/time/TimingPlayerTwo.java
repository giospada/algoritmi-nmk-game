package mnkgame.time;

import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import mnkgame.time.SmallBoard.Board;

/**
 * Questa classe è utilizzata per fare una stima della capacità di esecuzione del computer remoto
 * in modo che dinamicamente calcoli profondità e branching factor migliore
 */
class TimingPlayerTwo{

    private long timeStart;
    private final long TIMEOUT;
    private IBoard B;
    private int moves;

    private int BRANCHING_FACTOR = 7;
    private int DEPTH_LIMIT = 10;
    
    private int M;
    private int N;
    private int K;

    TimingPlayerTwo(long timeStart, int timeout_in_secs, int M, int N, int K) {
        this(timeStart, timeout_in_secs, new Board(M, N, K, MNKCellState.P1));
    }  
    
    TimingPlayerTwo(long timeStart, int timeout_in_secs, IBoard board) {
        this.timeStart = timeStart;
        this.TIMEOUT = Math.min(timeout_in_secs, 10);
        this.B = board;        
        this.moves = 0;

        if (board.getK() == 10) {  // per le board grosse
            DEPTH_LIMIT = 10;
            BRANCHING_FACTOR = 3;
        }

        this.M = board.getM();
        this.N = board.getN();
        this.K = board.getK();
    }

    boolean hasEnded(int depth) {
        return depth == DEPTH_LIMIT || hasTimeRunOut() || B.gameState() != MNKGameState.OPEN;
    }


     /**
     * trova mossa migliore con alfa beta pruning
     * @return
     */
    public void findBestTime() {
        int len = Math.min(BRANCHING_FACTOR * 3, B.getFreeCellsCount());
        // moves = 1000000;
        // return;
        for (int i = 0; i < len; i++) {
           if (hasTimeRunOut()) return;
           
           IHeuristicCell currCell = B.getGreatKCell(i);
           B.markCell(currCell);
           minPlayer(1);
           B.unmarkCell();
           moves++;
        }
    }    

    public void minPlayer(int depth) {
        if (hasEnded(depth)) { 
            return;
        }
        int len = Math.min(BRANCHING_FACTOR, B.getFreeCellsCount());
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

        int len = Math.min(BRANCHING_FACTOR, B.getFreeCellsCount());
        
        for (int i = 0; i < len; i++) {
            if(hasTimeRunOut()) return; 

            B.markCell(B.getGreatKCell(i));
            minPlayer(depth + 1);
            B.unmarkCell();
            moves++;
        }
    }


    private boolean hasTimeRunOut() {
        return (System.currentTimeMillis() - timeStart) / 1000.0 > TIMEOUT * (85.0 / 100.0);
    }

    public int getMoves() {
        return this.moves;
    }
    
    public int getBranchingFactor() {   
        return BRANCHING_FACTOR;
    }
    public int getDephtLimit(){
        if (M == 5 && N == 5 && K == 4) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 11;
            }
        } else if (M == 5 && N == 5 && K == 5) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 11;
            }
        } else if (M == 6 && N == 5 && K == 4) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 11;
            }
        } else if (M == 6 && N == 6 && K == 4) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 11;
            } else {
                return 14;
            }
        } else if (M == 6 && N == 6 && K == 5) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 9;
            }
        } else if (M == 6 && N == 6 && K == 6) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 10;
            }
        } else if (M == 7 && N == 5 && K == 4) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 15;
            }
        } else if (M == 7 && N == 5 && K == 5) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 13;
            } else {
                return 14;
            }
        } else if (M == 7 && N == 6 && K == 4) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 13;
            } else {
                return 15;
            }
        }else if (M == 7 && N == 6 && K == 5) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 9;
            }
        } else if (M == 7 && N == 7 && K == 5) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 9;
            }
        }else if (M == 7 && N == 7 && K == 6) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 9;
            }
        }else if (M == 7 && N == 7 && K == 7) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 9;
            }
        } else if (M == 8 && N == 8 && K == 4) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 9;
            }
        } else if (M == 10 && N == 10 && K == 5) {
            if (moves < 10000) {
                return 5;
            } else if (moves < 100000) {
                return 7;
            } else if (moves < 1000000) {
                return 9;
            } else {
                return 9;
            }
        } else if (M == 50 && M == 50 && K == 10) {
            if (moves < 10000) {
                return 9;
            } else if (moves < 100000) {
                return 12;
            } else if (moves < 1000000) {
                return 12;
            } else {
                return 12;
            }
        } else if (M == 70 && N == 70 && K == 10) {
            if (moves < 10000) {
                return 9;
            } else if (moves < 100000) {
                return 12;
            } else if (moves < 1000000) {
                return 12;
            } else {
                return 12;
            }
        }


        return DEPTH_LIMIT;
    }

}

// args.board=(5, 5, 4)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=11, numeroDiMosse=10000000
// args.board=(5, 5, 5)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=11, numeroDiMosse=10000000
// args.board=(6, 5, 4)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=15, numeroDiMosse=10000000
// args.board=(6, 6, 4)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=11, numeroDiMosse=1000000
// massimo=14, numeroDiMosse=10000000
// args.board=(6, 6, 5)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=9, numeroDiMosse=10000000
// args.board=(6, 6, 6)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=10, numeroDiMosse=10000000
// args.board=(7, 5, 4)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=15, numeroDiMosse=10000000
// args.board=(7, 6, 4)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=13, numeroDiMosse=1000000
// massimo=14, numeroDiMosse=10000000
// args.board=(7, 7, 4)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=13, numeroDiMosse=1000000
// massimo=15, numeroDiMosse=10000000
// args.board=(7, 5, 5)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=9, numeroDiMosse=10000000
// args.board=(7, 6, 5)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=9, numeroDiMosse=10000000
// args.board=(7, 7, 5)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=9, numeroDiMosse=10000000
// args.board=(7, 7, 6)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=9, numeroDiMosse=10000000
// args.board=(7, 7, 7)
// massimo=6, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=9, numeroDiMosse=10000000
// args.board=(8, 8, 4)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=9, numeroDiMosse=10000000
// args.board=(10, 10, 5)
// massimo=5, numeroDiMosse=10000
// massimo=7, numeroDiMosse=100000
// massimo=9, numeroDiMosse=1000000
// massimo=9, numeroDiMosse=10000000
// args.board=(50, 50, 10)
// massimo=9, numeroDiMosse=10000
// massimo=12, numeroDiMosse=100000
// massimo=12, numeroDiMosse=1000000
// massimo=12, numeroDiMosse=10000000
// args.board=(70, 70, 10)
// massimo=9, numeroDiMosse=10000
// massimo=12, numeroDiMosse=100000
// massimo=12, numeroDiMosse=1000000
// massimo=12, numeroDiMosse=10000000
