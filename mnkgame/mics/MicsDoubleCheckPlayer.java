package mnkgame.mics;

import java.util.ArrayList;
import java.util.Collections;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

public class MicsDoubleCheckPlayer implements mnkgame.MNKPlayer {
    private Board B;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private MNKCellState myState;
    private MNKCellState yourState;
    private ArrayList<CellPair> moves;
    private int K;
    public MicsDoubleCheckPlayer() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        myState = first ? MNKCellState.P1 : MNKCellState.P2;
        yourState = first ? MNKCellState.P2 : MNKCellState.P1;
        B = new Board(M, N, K, myState);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        moves = new ArrayList<CellPair>();
        this.K = K;
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
        B.setPlayer(yourState);
        for (MNKCell d : freeCells) {
            if (B.markCell(d.i, d.j) == yourWin) {
                B.unmarkCell();
                
                // vado a marcare io la cella con cui vincerebbe l'avversario
                B.setPlayer(myState);;
                B.markCell(d.i, d.j);
                return d;
            } else {
                B.unmarkCell();
            }
        }
        B.setPlayer(myState);
        return null;
    }

    private MNKCell findDoubleWinCell(MNKCell[] freeCells) {
        MNKCell winnerCell = null;
        for (int i = 0; i < freeCells.length; i++) {
            B.setCellState(freeCells[i].i, freeCells[i].j, myState);
            for (int j = i + 1; j < freeCells.length; j++) {
                if (B.markCell(freeCells[j].i, freeCells[j].j) == myWin) {
                    if (getWholeHeuristics(freeCells[i]) > getWholeHeuristics(freeCells[j])) {
                        winnerCell = freeCells[i];
                    } else {
                        winnerCell = freeCells[j];
                    }
                } 
                B.unmarkCell();
                if (winnerCell != null) break;
            }
            B.setCellState(freeCells[i].i, freeCells[i].j, MNKCellState.FREE);

            if (winnerCell != null) {
                return winnerCell;
            }
        }
        return null;
    }

    private MNKCell findPreventDoubleWinCell(MNKCell[] freeCells) {
        MNKCell winnerCell = null;
        for (int i = 0; i < freeCells.length; i++) {
            B.setCellState(freeCells[i].i, freeCells[i].j, yourState);
            for (int j = i + 1; j < freeCells.length; j++) {
                if (B.markCell(freeCells[j].i, freeCells[j].j) == yourWin) {
                    // DEBUG, remember to delete me later
                    System.out.print(freeCells[i] + " " + freeCells[j] + " ");
                    int first = getWholeHeuristics(freeCells[i]);
                    System.out.println();
                    int second = getWholeHeuristics(freeCells[j]);
                    System.out.print(first + " " + second + " ");
                    if (first > second) {
                        winnerCell = freeCells[i];
                    } else {
                        winnerCell = freeCells[j];
                    }
                } 
                B.unmarkCell();
                if (winnerCell != null) break;
            }
            B.setCellState(freeCells[i].i, freeCells[i].j, MNKCellState.FREE);

            if (winnerCell != null) {
                return winnerCell;
            }
        }
        return null;
    }
    
    private int getWholeHeuristics(MNKCell freeCell) {
        return B.getHeuristic(freeCell.i, freeCell.j) + B.getSwappedHeuristics(freeCell.i, freeCell.j);
    }
    
    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        B.setPlayer(yourState);
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
        }
        
        B.setPlayer(myState);
        MNKCell winCell = findWinCell(freeCells);
        if (winCell != null) return winCell;
        
        B.setPlayer(yourState);
        MNKCell preventWinCell = findPreventWinCell(freeCells);
        if (preventWinCell != null) return preventWinCell;
        
        if (K >= 5) {
            B.setPlayer(myState);
            MNKCell doubleWinCell = findDoubleWinCell(freeCells);
            if (doubleWinCell != null) {
                B.markCell(doubleWinCell.i, doubleWinCell.j);
                return doubleWinCell;
            }
    
            B.setPlayer(yourState);
            MNKCell preventDoubleWin = findPreventDoubleWinCell(freeCells);
            if (preventDoubleWin != null) {
                B.setPlayer(myState);
                B.markCell(preventDoubleWin.i, preventDoubleWin.j);
                return preventDoubleWin;
            }
        }

        for (int i = 0; i < freeCells.length; i++)
            moves.add(new CellPair(getWholeHeuristics(freeCells[i]), freeCells[i]));

        Collections.sort(moves);
        MNKCell bestCell = moves.get(0).cell;
        B.setPlayer(myState);
        B.markCell(bestCell.i, bestCell.j);
        moves.clear();
        return bestCell;
    }

    public String playerName() {
        return "Mics DoubleCheck Player";
    }
}
