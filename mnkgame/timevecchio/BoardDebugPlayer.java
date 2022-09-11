package mnkgame.timevecchio;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

public class BoardDebugPlayer implements mnkgame.MNKPlayer {
    private IBoard B;
    private MNKCellState myState;

    private final boolean DEBUG = false;

    private int M, N;

    // l'algoritmo si comporta in modo molto strano alle prime mosse
    private boolean firstMove;
    
    public BoardDebugPlayer() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        long timeStart = System.currentTimeMillis();
        this.myState = first ? MNKCellState.P1 : MNKCellState.P2;

        this.B = new mnkgame.timevecchio.BigBoard.Board(M, N, K, myState);

        firstMove = true;
        this.M = M;
        this.N = N;

        B.markCell(9, 21);
B.markCell(8, 20);
B.markCell(9, 19);
B.markCell(9, 20);
B.markCell(8, 21);
B.markCell(7, 20);
B.markCell(6, 20);
B.markCell(10, 20);
B.markCell(13, 21);
B.markCell(12, 20);
B.markCell(7, 21);
B.markCell(13, 20);
B.markCell(15, 20);
B.markCell(14, 21);
B.markCell(21, 28);
B.markCell(20, 27);
B.markCell(15, 11);
B.markCell(15, 22);
B.markCell(9, 16);
B.markCell(12, 19);
B.markCell(18, 25);
B.markCell(12, 21);
B.markCell(12, 12);
B.markCell(12, 22);
B.markCell(12, 25);
B.markCell(12, 18);
B.markCell(14, 12);
B.markCell(12, 17);
B.markCell(13, 12);
B.markCell(12, 24);
B.markCell(17, 12);
B.markCell(12, 23);
B.markCell(16, 12);
B.markCell(18, 12);
B.markCell(12, 15);
B.markCell(17, 13);
B.markCell(7, 12);
B.markCell(16, 14);
B.markCell(14, 16);
B.markCell(19, 11);
B.markCell(13, 13);
B.markCell(20, 10);
B.markCell(20, 19);
B.markCell(15, 15);
B.markCell(11, 12);
B.markCell(21, 9);
B.markCell(11, 15);
B.markCell(22, 8);
B.markCell(11, 25);
B.markCell(10, 25);
B.markCell(10, 21);
B.markCell(11, 24);
B.markCell(11, 21);
B.markCell(9, 26);
B.markCell(9, 17);
B.markCell(8, 27);
B.markCell(25, 5);
B.markCell(7, 28);
B.markCell(23, 7);
B.markCell(6, 29);
B.markCell(5, 21);
B.markCell(8, 22);
B.markCell(12, 14);
B.markCell(11, 22);
B.markCell(5, 30);
B.markCell(10, 22);
B.markCell(17, 9);
B.markCell(13, 22);
B.markCell(18, 22);
B.markCell(17, 22);
B.markCell(6, 21);
B.markCell(14, 22);
B.markCell(18, 8);
B.markCell(9, 22);
B.markCell(16, 22);
B.markCell(5, 22);
B.markCell(10, 16);
B.markCell(16, 10);
B.markCell(7, 22);
B.markCell(6, 27);
B.markCell(4, 21);
B.markCell(9, 24);
B.markCell(9, 30);
B.markCell(14, 19);
B.markCell(14, 25);
B.markCell(15, 18);
B.markCell(7, 26);
B.markCell(16, 17);
B.markCell(15, 12);
B.markCell(17, 16);
B.markCell(10, 23);
B.markCell(18, 15);
B.markCell(8, 12);
B.markCell(15, 24);
B.markCell(21, 15);
B.markCell(18, 24);
B.markCell(23, 10);
B.markCell(22, 11);
B.markCell(13, 24);
B.markCell(16, 25);
B.markCell(11, 16);
B.markCell(11, 20);
B.markCell(8, 17);
B.markCell(9, 18);
B.markCell(17, 21);
B.markCell(14, 23);
B.markCell(21, 12);
B.markCell(10, 19);
B.markCell(19, 28);
B.markCell(18, 27);
B.markCell(17, 26);
B.markCell(8, 18);
B.markCell(15, 27);
B.markCell(7, 18);
B.markCell(16, 11);
B.markCell(11, 18);
B.markCell(10, 18);
B.markCell(14, 18);
B.markCell(17, 10);
B.markCell(13, 18);
B.markCell(19, 19);
B.markCell(20, 18);
B.markCell(21, 18);
B.markCell(16, 18);
B.markCell(13, 14);
B.markCell(17, 18);
B.markCell(17, 11);
B.markCell(19, 14);
B.markCell(20, 13);
B.markCell(15, 16);
B.markCell(19, 8);
B.markCell(18, 13);
B.markCell(18, 18);
B.markCell(19, 12);
B.markCell(13, 15);
B.markCell(14, 17);
B.markCell(22, 9);
B.markCell(20, 11);
B.markCell(18, 9);
B.markCell(14, 13);
B.markCell(9, 11);
B.markCell(21, 10);
B.markCell(16, 15);
B.markCell(11, 17);
B.markCell(13, 11);
B.markCell(13, 17);
B.markCell(12, 11);
B.markCell(15, 17);
B.markCell(19, 17);
B.markCell(18, 11);
B.markCell(12, 16);
B.markCell(21, 11);
B.markCell(23, 11);
B.markCell(16, 16);
B.markCell(21, 16);
B.markCell(20, 12);
B.markCell(17, 8);
B.markCell(19, 13);
B.markCell(23, 9);
B.markCell(18, 14);
B.markCell(21, 24);
B.markCell(22, 10);
B.markCell(12, 8);
B.markCell(20, 7);
B.markCell(21, 23);
B.markCell(20, 8);
B.markCell(21, 22);
B.markCell(20, 6);
B.markCell(11, 9);
B.markCell(20, 9);
B.markCell(12, 9);
B.markCell(20, 5);
B.markCell(13, 9);
B.markCell(17, 15);
B.markCell(13, 19);
B.markCell(21, 14);
B.markCell(21, 19);
B.markCell(22, 14);
B.markCell(9, 9);
B.markCell(23, 14);
B.markCell(20, 4);
B.markCell(17, 14);
B.markCell(21, 21);
B.markCell(14, 14);
B.markCell(21, 27);
B.markCell(20, 14);
B.markCell(21, 20);
B.printHeuristics(true);
    }


    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
            firstMove = false;
        }
        return freeCells[0];
    }

    public String playerName() {
        return "Board Debug";
    }
}
