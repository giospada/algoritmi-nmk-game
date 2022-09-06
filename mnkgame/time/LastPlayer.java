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
    private final int BRANCHING_FACTOR = 7;
    private final int DEPTH_LIMIT = 7;
    private final int KINF = 1000000000;  // 1 miliardo

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
    }

    public int minPlayer(int depth, int alpha, int beta) {
        if (gameState != MNKGameState.OPEN || depth == DEPTH_LIMIT) {  // TODO: check when the board is in end state (depth time, state)
            return B.getValue(yourState);  // todo get the heuristic value of this game state
        }
        B.setPlayer(yourState);

        int v = KINF;

        int len = Math.min(BRANCHING_FACTOR, B.freeCellsCount);

        for (int i = 0; i < len; i++) {
            gameState = B.markCell(B.getGreatKCell(i).i, B.getGreatKCell(i).j, true);
            int maxPlayerValue = maxPlayer(depth + 1, alpha, beta);
            B.unmarkCell(true);

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
        if (gameState != MNKGameState.OPEN || depth == DEPTH_LIMIT) {  // TODO: check when the board is in end state (depth time, state)
            return B.getValue(myState);
        }

        B.setPlayer(myState);
        int v = -KINF;

        int len = Math.min(BRANCHING_FACTOR, B.freeCellsCount);

        for (int i = 0; i < len; i++) {
            gameState = B.markCell(B.getGreatKCell(i).i, B.getGreatKCell(i).j, true);
            int minPlayerValue = minPlayer(depth + 1, alpha, beta);
            B.unmarkCell(true);

            if (minPlayerValue > v) {
                v = minPlayerValue;
                alpha = Math.max(alpha, v);
            }

            if (v >= beta)
                return v;
        }
        return v;
    }

    // time should never run out right? it's the first step!
    // @returns a winning cell if there is one
    private MNKCell findWinCell(MNKCell[] freeCells) {
        for (MNKCell d : freeCells) {
            if (B.markCell(d.i, d.j) == myWin) {
                B.unmarkCell();
                return d;
            } else {
                B.unmarkCell();
            }
        }
        return null;
    }

    private MNKCell findPreventWinCell(MNKCell[] freeCells) {
        for (MNKCell d : freeCells) {
            if (B.markCell(d.i, d.j) == yourWin) {
                B.unmarkCell();
                return d;
            }
            B.unmarkCell();
        }
        return null;
    }

    private MNKCell findMyDoublePlay(MNKCell[] freCells) {
        MNKCell winningCell = null;
        for (MNKCell d : freCells) {
            Value value = B.getCellValue(d.i, d.j, myState);
            if (value.isDoublePlay()) {
                winningCell = d;
            }

            if (winningCell != null) break;
        }
        return winningCell;
    }

    private MNKCell findEnemyDoublePlay(MNKCell[] freCells) {
        MNKCell winningCell = null;
        for (MNKCell d : freCells) {
            Value value = B.getCellValue(d.i, d.j, yourState);
            if (value.isDoublePlay()) {
                winningCell = d;
            }

            if (winningCell != null) break;
        }
        return winningCell;
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
        B.setPlayer(myState);

        // al primo livello valuto quasi tutto
        int len = Math.min(BRANCHING_FACTOR * 3, B.freeCellsCount);
        for (int i = 0; i < len; i++) {
            gameState = B.markCell(B.getGreatKCell(i).i, B.getGreatKCell(i).j, true);
            int minPlayerValue = minPlayer(1, alpha, beta);
            B.unmarkCell(true);

            if (minPlayerValue > v) {
                v = minPlayerValue;
                cell = B.getGreatKCell(i);
                alpha = Math.max(alpha, v);
            }

            if (v >= beta)
                break;
        }
        return cell;
    }

    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        B.setPlayer(yourState);
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j, true); // Save the last move in the local MNKBoard
            firstMove = false;
        }

        B.print();

        if (firstMove) {
            MNKCell bestCell = new MNKCell((M - 1) / 2, (N - 1) / 2);
            B.setPlayer(myState);
            B.markCell(bestCell.i, bestCell.j, true);
            firstMove = false;
            return bestCell;
        }

        System.out.println("Playing");

        // Priority 1: Win
        B.setPlayer(myState);
        MNKCell winCell = findWinCell(freeCells);
        if (winCell != null) {
            System.out.println("Winning cell found");
            B.markCell(winCell.i, winCell.j, true);

            return winCell;
        }

        // Priority 2, prevent the opponent from winning
        B.setPlayer(yourState);
        MNKCell preventWinCell = findPreventWinCell(freeCells);
        if (preventWinCell != null) {
            System.out.println("Preventing win cell found");
            B.setPlayer(myState);
            B.markCell(preventWinCell.i, preventWinCell.j, true);

            return preventWinCell;
        }

        // Priority 3, find the best cell to fork (two or more winning ways)
        winCell = findMyDoublePlay(freeCells);
        if (winCell != null) {
            System.out.println("Double play cell found");
            B.setPlayer(myState);
            B.markCell(winCell.i, winCell.j, true);

            return winCell;
        }

        // Priority 4, find the best cell to block the opponent's fork
        B.setPlayer(yourState);
        preventWinCell = findEnemyDoublePlay(freeCells);
        if (preventWinCell != null) {
            System.out.println("Preventing double play cell found");
            B.setPlayer(myState);
            B.markCell(preventWinCell.i, preventWinCell.j, true);

            return preventWinCell;
        }

        // Priority 5, find the best cell to win in the most number of possible ways
        // TODO: Probably using minimax
        System.out.println("No winning cell found, selecting with mini-max ab");
        MNKCell bestCell = findBestMove();
        B.setPlayer(myState);
        B.markCell(bestCell.i, bestCell.j, true);

        return bestCell;
    }

    public String playerName() {
        return "Mics Player v2";
    }
}
