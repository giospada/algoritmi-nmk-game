package mnkgame.simpleheuristic;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MinimaxPlayer implements mnkgame.MNKPlayer {
    private Random rand;
    private Board B;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private MNKGameState gameState;
    private long TIMEOUT;
    private final int kinf = 1000000000;  // un miliardo

    private long startTime;
    public MinimaxPlayer() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        rand = new Random(System.currentTimeMillis());
        MNKCellState myState = first ? MNKCellState.P1 : MNKCellState.P2;
        B = new Board(M, N, K, myState);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        TIMEOUT = timeout_in_secs;
        gameState = MNKGameState.OPEN;
    }

    private int minPlayer(Queue<MNKCell> actions, int alpha, int beta) {
        // B.print();
        if (gameState != MNKGameState.OPEN || (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0)) {
            return B.getHeuristicValue();
        }

        // begin the minimax search in deeper levels
        MNKCell firstCell = actions.poll();  // used too check if i cicled through all the Cells
        gameState = B.markCell(firstCell.i, firstCell.j);
        int best = maxPlayer(actions, alpha, beta);
        B.unmarkCell();
        actions.add(firstCell);
        if (best <= alpha) {
            return best;
        }
        beta = Math.min(beta, best);

        while (actions.peek() != firstCell) {
            MNKCell d = actions.poll();
            gameState = B.markCell(d.i, d.j);

            int val = maxPlayer(actions, alpha, beta);
            B.unmarkCell();
            actions.add(d);
            if (val < best) {
                best = val;
                beta = Math.min(beta, best);
            }
            if (best <= alpha) {
                return best;
            }

        }
        return best;
    }

    private int maxPlayer(Queue<MNKCell> actions, int alpha, int beta) {
        // B.print();
        if (gameState != MNKGameState.OPEN || (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0)) {
            return B.getHeuristicValue();
        }
        // if (actions.peek() == new MNKCell(1, 1)) {
        //     return B.getHeuristicValue();
        // }
        // for(MNKCell s : actions) { 
        //     System.out.print(s + " "); 
        // }
        // System.out.println();
        // B.print();

        // begin the minimax search in deeper levels
        MNKCell firstCell = actions.poll();  // used too check if i cicled through all the Cells

        gameState = B.markCell(firstCell.i, firstCell.j);
        int best = minPlayer(actions, alpha, beta);
        B.unmarkCell();
        actions.add(firstCell);
        if (best >= beta) {
            return best;
        }
        alpha = Math.max(alpha, best);

        while (actions.peek() != firstCell) {
            MNKCell d = actions.poll();
            // System.out.format("(%d %d) ", d.i, d.j);

            gameState = B.markCell(d.i, d.j);
            int val = minPlayer(actions, alpha, beta);
            B.unmarkCell();

            // rimetto in fondo così è disponibile per il prossimo!, così la creazione
            // del prossimo stadio è fatto in tempo costante, invece che lineare.
            actions.add(d);  
            if (val > best) {
                best = val;
                alpha = Math.max(alpha, best);
            }

            if (best >= beta) {
                return best;
            }
        }
        // System.out.println("finished");

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

        Queue<MNKCell> freeCellQueue = new LinkedList<>(Arrays.asList(freeCells));

        // begin the minimax search in deeper levels
        MNKCell firstCell = freeCellQueue.poll();  // used too check if i cicled through all the Cells
        MNKCell bestCell = firstCell;
        B.markCell(firstCell.i, firstCell.j);
        int best = minPlayer(freeCellQueue, -kinf, kinf);
        freeCellQueue.add(firstCell);
        B.unmarkCell();
        
        while (freeCellQueue.peek() != firstCell) {
            MNKCell d = freeCellQueue.poll();
            // System.out.format("current cell is %d %d\n", d.i, d.j);
            gameState = B.markCell(d.i, d.j);
            int val = minPlayer(freeCellQueue, best, kinf);
            if (val > best) {
                best = val;
                bestCell = d;
            }
            B.unmarkCell();
            freeCellQueue.add(d);  // rimetto in fondo così è disponibile per il prossimo!, così la creazione
            // del prossimo stadio è fatto in tempo costante, invece che lineare.
        }
        B.markCell(bestCell.i, bestCell.j);
        return bestCell;
    }

    public String playerName() {
        return "Heuristic Minimax";
    }
}
