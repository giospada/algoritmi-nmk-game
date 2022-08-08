package mnkgame;

public class IterativeDeepening implements MNKPlayer {
	private CBoard Board;
    private MNKGameState currGameState;
	private MNKGameState myWin;
	private MNKGameState yourWin;
	private int TIMEOUT;
    private long timeStart;
    private final int kinf = 10000000;
    private final int kMyWinValue = 1000000;
    private final int kYourWinValue = -1000000;
    private final int kDrawValue = 0;

	public IterativeDeepening() {}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		Board   = new CBoard(M,N,K);
		myWin   = first ? MNKGameState.WINP1 : MNKGameState.WINP2; 
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
		TIMEOUT = timeout_in_secs;	
	}

    public int evaluateBoard() {
        // TODO create euristics value for case gameState Open
        int returnValue = 0;

        if (currGameState == myWin) {
            returnValue = kMyWinValue;
        } else if (currGameState == yourWin) {
            returnValue = kYourWinValue;
        } else if (currGameState == MNKGameState.DRAW) {
            returnValue = kDrawValue;
        }

        currGameState = MNKGameState.OPEN;
        return returnValue;
    }

    private boolean hasTimeRunOut() {
        return (System.currentTimeMillis() - timeStart) / 1000.0 > TIMEOUT * (99.0 / 100.0);
    }
    
    private boolean hasIterationEnded(int depth, int maxDepth) {
        return depth == maxDepth || currGameState != MNKGameState.OPEN || hasTimeRunOut();
    }

    private int iterativeDeepening(int depth, int maxDepth, boolean is_minimizing) {
        if (hasIterationEnded(depth, maxDepth)) {
            return evaluateBoard();
        }

        int v; 
        if (is_minimizing) {
            v = kinf;
            for (CCell cell : Board.getFreeCell()) {
                currGameState = Board.markCell(cell.getPosition());
                v = Math.min(v, iterativeDeepening(depth + 1, maxDepth, !is_minimizing));
                Board.unmarkCell();
            }
        } else {
            v = -kinf;
            for (CCell cell : Board.getFreeCell()) {
                currGameState = Board.markCell(cell.getPosition());
                v = Math.max(v, iterativeDeepening(depth + 1, maxDepth, !is_minimizing));
                Board.unmarkCell();
            }
        }
        return v;
    }

    // utilizziamo la board globale per aggiungere e togliere e ci fermiamo quando uno vince
	public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        timeStart = System.currentTimeMillis();

        if (movedCells.length > 0) {
			MNKCell c = movedCells[movedCells.length-1]; // Recover the last move from MC
			Board.markCell(c.i,c.j);         // Save the last move in the local MNKBoard
		}

        int v = -kinf;
        MNKCell bestCell = freeCells[0];
        int maxdepth = 1;
        while (!hasTimeRunOut() && maxdepth <= freeCells.length) {
            for (int i = 0; i < freeCells.length; i++) {
                currGameState = Board.markCell(freeCells[i].i, freeCells[i].j);
                int currV = iterativeDeepening(0, maxdepth, true);
                if (currV > v) {
                    v = currV;
                    bestCell = freeCells[i]; 
                }
                Board.unmarkCell();
            }
            maxdepth++;
        }

        return bestCell;
	}

	public String playerName() {
		return "Iterative deepening";
	}
}

