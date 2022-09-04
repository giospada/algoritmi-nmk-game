package mnkgame.time;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

public class MicsPlayer implements mnkgame.MNKPlayer {
    private Board B;
    private MNKGameState myWin;
    private MNKCellState myState;
    private MNKCellState yourState;
    private MNKGameState yourWin;

    public MicsPlayer() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        myState = first ? MNKCellState.P1 : MNKCellState.P2;
        yourState = first ? MNKCellState.P2 : MNKCellState.P1;
        B = new Board(M, N, K, myState);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
    }

    // time should never run out right? it's the first step!
    // @returns a winning cell if there is one
    private MNKCell findWinCell(MNKCell[] freeCells) {
        for (MNKCell d : freeCells) {
            if (B.markCell(d.i, d.j) == myWin) {
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
                
                // vado a marcare io la cella con cui vincerebbe l'avversario
                B.setPlayer(myState);;
                B.markCell(d.i, d.j);
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

    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        B.setPlayer(yourState);
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
            B.updateCellValue(c.i, c.j);
        }
        System.out.println("Playing");

        // Priority 1: Win
        B.setPlayer(myState);
        MNKCell winCell = findWinCell(freeCells);
        if (winCell != null) {
            System.out.println("Winning cell found");
            return winCell;
        }

        // Priority 2, prevent the opponent from winning
        B.setPlayer(yourState);
        MNKCell preventWinCell = findPreventWinCell(freeCells);
        if (preventWinCell != null) {
            System.out.println("Preventing win cell found");
            return preventWinCell;
        }

        // Priority 3, find the best cell to fork (two or more winning ways)
        B.setPlayer(myState);
        winCell = findMyDoublePlay(freeCells);
        if (winCell != null) {
            System.out.println("Double play cell found");
            return winCell;
        }

        // Priority 4, find the best cell to block the opponent's fork
        B.setPlayer(yourState);
        preventWinCell = findEnemyDoublePlay(freeCells);
        if (preventWinCell != null) {
            System.out.println("Preventing double play cell found");
            return preventWinCell;
        }

        // Priority 5, find the best cell to win in the most number of possible ways
        // TODO: Probably using minimax
        B.setPlayer(myState);

        System.out.println("No winning cell found, selecting a random one");
        MNKCell bestCell = freeCells[0];  // temporaneo
        B.markCell(bestCell.i, bestCell.j);
        B.updateCellValue(bestCell.i, bestCell.j);
        return bestCell;
    }

    public String playerName() {
        return "Mics Player v2";
    }
}
