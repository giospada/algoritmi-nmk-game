package mnkgame.bigheuristics;

import java.util.HashMap;
import java.util.PriorityQueue;

import mnkgame.MNKCell;
import mnkgame.MNKGameState;
import mnkgame.MNKCellState;

public class IterativePlayerV2 implements mnkgame.MNKPlayer {
    private Board B;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private int TIMEOUT;
    private long timeStart;
    private MNKCellState myState;
    private MNKCellState yourState;

    private PriorityQueue<SearchNode> queue;
    private HashMap<SearchNode, SearchNode> registeredNodes;
    private SearchNode[] moves;  // contiene solamente i root nodes, fra cui poi andare a scegliere
    private int timeoutFrac;  // frazione di 100 per cui checkare il timeout
    int moves_counter;

    public IterativePlayerV2() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        myState = first ? MNKCellState.P1 : MNKCellState.P2;
        yourState = first ? MNKCellState.P2 : MNKCellState.P1;
        B = new Board(M, N, K, myState);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        TIMEOUT = timeout_in_secs;

        // supponendo che 1 <= TIMEOUT <= 10
        timeoutFrac = 89 + TIMEOUT;
    }

    private boolean hasTimeRunOut() {
        return (System.currentTimeMillis() - timeStart) / 1000.0 > TIMEOUT * (timeoutFrac / 100.0);
    }

    private MNKCell findWinCellAndCreateQueue(MNKCell[] freeCells) {
        // TODO: velocizzare questa parte con Dinamic programming, sto ricalcolando molte free cells credo???
        for (MNKCell d : freeCells) {
            SearchNode currnode = new SearchNode(d.i, d.j, B.getHeuristic(d.i, d.j) + B.getSwappedHeuristics(d.i, d.j));
            MNKGameState state = B.markCell(d.i, d.j);
            queue.add(currnode);  // O(log(n))
            moves[moves_counter++] = currnode;  // O(1)

            if (state == myWin) {
                queue = null;  // free with Garbage Collector
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
                queue.clear();
                return d;
            }
            B.unmarkCell();
        }
        return null;
    }

    // utilizziamo la board globale per aggiungere e togliere e ci fermiamo quando uno vince
    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        timeStart = System.currentTimeMillis();
        moves = new SearchNode[freeCells.length];
        queue = new PriorityQueue<SearchNode>();
        registeredNodes = new HashMap<SearchNode, SearchNode>(freeCells.length * 10);
        moves_counter = 0;

        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
        }
        MNKCell winCell = findWinCellAndCreateQueue(freeCells);
        if (winCell != null) return winCell;

        B.setPlayer(yourState);
        MNKCell preventWinCell = findPreventWinCell(freeCells);
        if (preventWinCell != null) return preventWinCell;
        B.setPlayer(myState);
        // nella queue voglio avere solamente nodi che sono stati calcolati e per cui
        // il gioco sia ancora aperto.
        while (!hasTimeRunOut() && !queue.isEmpty()) {
            SearchNode curr = queue.poll();  // log(size(queue));
            B.applyMove(curr);  // lineare sulla depth del nodo
            final MNKCell[] FC = B.getFreeCells();

            for (MNKCell cell : FC) {
                if (hasTimeRunOut()) break;
                MNKGameState state = B.markCell(cell.i, cell.j);
                int value = B.getHeuristic(cell.i, cell.j) + B.getSwappedHeuristics(cell.i, cell.j);
                if (state == myWin) {
                    value *= 1.7;
                    curr.backtrack(value);
                } else if (state == yourWin) {
                    value *= 1.5;
                    curr.backtrack(value);
                } else if (state == MNKGameState.DRAW) {
                    curr.backtrack(value);
                } else {
                    SearchNode child = curr.expand(cell.i, cell.j, value);
                    // essendo inserita nella queue, allora è necessariamente contenuto
                    if (registeredNodes.containsKey(child)) {  // O(1) check
                        SearchNode childNode = registeredNodes.get(child);  // O(1) retrieval
                        if (value > childNode.value) {
                            childNode.value = value;
                            childNode.backtrack(value);

                            // update the queue, O(log(size of queue))
                            queue.remove(childNode);
                            queue.add(childNode);
                        }  // else do nothing
                    } else {
                        registeredNodes.put(child, child);
                        queue.add(child);
                    }       
                }
                B.unmarkCell();
            }
            curr.backtrack();  // se lo ho fatto già dentro dovrebbe essere costo costante (non dovrebbe backtraccare)
            B.resetMove(curr);  // lineare sulla depth del nodo
        }

        // find best move in O(n), with n size of moves = size of freecells
        MNKCell best = null;
        int bestValue = Integer.MIN_VALUE;
        for (SearchNode m : moves) {
            if (m.value > bestValue) {
                best = new MNKCell(m.moves[0].i, m.moves[0].j);
                bestValue = m.value;
            }
        }

        B.markCell(best.i, best.j);
        return best;
    }

    @Override
    public String playerName() {
        return "IterativePlayer LTM v2";  // LTM = Late Move Reduction
    }
    
}
