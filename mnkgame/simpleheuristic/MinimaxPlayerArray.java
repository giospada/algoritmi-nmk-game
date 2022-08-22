package mnkgame.simpleheuristic;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


// questo è praticamente uguale all'altro heuristic minimax, solo che invece di queue, uso gli array
public class MinimaxPlayerArray implements mnkgame.MNKPlayer {
    private Random rand;
    private Board B;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private MNKGameState gameState;
    private long TIMEOUT;
    private final int kinf = 1000000000;  // un miliardo

    private long startTime;
    public MinimaxPlayerArray() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        rand = new Random(System.currentTimeMillis());
        MNKCellState myState = first ? MNKCellState.P1 : MNKCellState.P2;
        B = new Board(M, N, K, myState);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        TIMEOUT = timeout_in_secs;
        gameState = MNKGameState.OPEN;
    }

    private int minPlayer(MNKCell[] actions, int alpha, int beta) {
        if (gameState != MNKGameState.OPEN || (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0)) {
            return B.getHeuristicValue();
        }

        int best = kinf;
        
        for (int i = 0; i < actions.length; i++) {
            gameState = B.markCell(actions[i].i, actions[i].j);
            MNKCell newActions[] = getFreeCellsAfterAction(actions, i);

            int minPlayerValue = maxPlayer(newActions, alpha, beta);
            if (minPlayerValue < best) {
                best = minPlayerValue;
                alpha = Math.min(alpha, best);
            }
            B.unmarkCell();

            if (best <= alpha) {
                return best;
            }
        }

        return best;
    }

    private int maxPlayer(MNKCell[] actions, int alpha, int beta) {
        if (gameState != MNKGameState.OPEN || (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0)) {
            return B.getHeuristicValue();
        }
        int best = -kinf;
        
        for (int i = 0; i < actions.length; i++) {
            gameState = B.markCell(actions[i].i, actions[i].j);
            MNKCell newActions[] = getFreeCellsAfterAction(actions, i);

            int minPlayerValue = minPlayer(newActions, alpha, beta);
            if (minPlayerValue > best) {
                best = minPlayerValue;
                alpha = Math.max(alpha, best);
            }
            B.unmarkCell();

            if (best >= beta) {
                return best;
            }
        }

        return best;
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
        B.togglePlayer();  // turno dell'avversario
        for (MNKCell d : freeCells) {
            if (B.markCell(d.i, d.j) == yourWin) {
                B.unmarkCell();
                
                // vado a marcare io la cella con cui vincerebbe l'avversario
                B.togglePlayer();
                B.markCell(d.i, d.j);
                return d;
            } else {
                B.unmarkCell();
            }
        }
        B.togglePlayer();  // turno mio di nuovo
        return null;
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

    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        startTime = System.currentTimeMillis();
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
        }

        MNKCell winCell = findWinCell(freeCells);
        if (winCell != null) return winCell;

        MNKCell preventWinCell = findPreventWinCell(freeCells);
        if (preventWinCell != null) return preventWinCell;

        int best = -kinf;
        int alpha = -kinf;
        MNKCell bestCell = freeCells[rand.nextInt(freeCells.length)];
        
        for (int i = 0; i < freeCells.length; i++) {
            gameState = B.markCell(freeCells[i].i, freeCells[i].j);

            MNKCell newActions[] = getFreeCellsAfterAction(freeCells, i);

            int minPlayerValue = minPlayer(newActions, alpha, kinf);
            if (minPlayerValue > best) {
                best = minPlayerValue;
                bestCell = freeCells[i];
                alpha = Math.max(alpha, best);
            }

            B.unmarkCell();
        }
        B.markCell(bestCell.i, bestCell.j);
        return bestCell;
    }

    public String playerName() {
        return "Heuristic Minimax Static Array";
    }
}
