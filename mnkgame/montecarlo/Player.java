package mnkgame.montecarlo;

import javax.management.RuntimeErrorException;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import mnkgame.MNKPlayer;

public class Player implements MNKPlayer {
    private Board B; // TODO make board
    private long startTime;
    private int TIMEOUT;
    int possibleMovesLenght;
    TreeNode root;


    public Player() {}

    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        MNKCellState myState = first ? MNKCellState.P1 : MNKCellState.P2;
        // MNKCellState yourState = first ? MNKCellState.P2 : MNKCellState.P1;
        TIMEOUT = timeout_in_secs;
        B = new Board(M, N, K, myState);

        // ROOT inizializzata con tutte le mosse
        root = new TreeNode();
        root.createChilds(B.getFreeCells());
    }

    private boolean hasTimeRunOut() {
        return (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (70.0 / 100.0);
    }

    public MNKGameState myWin;
    public MNKGameState yourWin;
    TreeNode select(TreeNode curNode) {
        while (!curNode.isLeaf && !curNode.isFinished) {
            curNode = curNode.children.peek();
            B.markCell(curNode.currMove.i, curNode.currMove.j);
        }

        return curNode;
    }

    /**
     * @brief suppongo che curNode sia una foglia
     * @param curNode
     * @return
     */
    TreeNode expand(TreeNode curNode) {
        // non abbiamo mosse da farci unmark con la root
        if (curNode != root && (curNode.tries == 0 || curNode.isFinished)) {
            // rollout with current node
            B.unmarkCell();  // non vogliamo mandare in simulate un nodo finisced
            return curNode;
        }

        // sono sicuro ora di essere in una foglia che si può espandere
        curNode.createChilds(B.getFreeCells());

        // prende il miglior child da ritornare
        TreeNode bestNode = curNode.children.peek();
        return bestNode;
    }

    MNKGameState simulate(TreeNode curNode) {
        MNKCell nextCell = curNode.currMove;
        int numMoves = 1;
        MNKGameState lastState = B.markCell(nextCell);

        if (lastState != MNKGameState.OPEN) {  // marca come cella finale non espandibile
            curNode.isFinished = true;
        }

        while(lastState == MNKGameState.OPEN){
            numMoves++;

            // QUESTO FA SCELTA RANDOMICHE
            MNKCell[] freeCells = B.getFreeCells();
            MNKCell best = freeCells[(int) Math.random() * freeCells.length];

            // QUESTO USA IL MICS.
            // int bestScore = -1;
            // for(MNKCell cell : B.getFreeCells()){
            //     int heuristics = B.getHeuristic(cell.i, cell.j) + B.getSwappedHeuristics(cell.i, cell.j);  // O(K)
            //     if (heuristics > bestScore) {
            //         bestScore = heuristics;
            //         best = cell;
            //     }
            // }

            lastState = B.markCell(best);
        }

        // si può ottimizzare??? da guardare
        for(int i = 1; i < numMoves; i++){
            B.unmarkCell();
        }

        return lastState;
    }

    void backpropagate(MNKGameState state, TreeNode curNode) {
        int addingValue = 0;
        if (state == myWin) {
            addingValue = 2;
        } else if (state == yourWin) {
            addingValue = 1;
        }
        // se valutiamo 0 la vittoria nemica, allora non è in grado di bloccare.


        while (curNode.parent != null) {
            curNode.goodTries += addingValue;

            curNode.tries +=2;
            curNode.parent.children.remove(curNode);
            curNode.parent.children.add(curNode);  // riaggiorna la posizione del nodo nella priority

            B.unmarkCell();

            curNode = curNode.parent;
        }

        // aggiorna valori della ROOT
        curNode.goodTries += addingValue;
        curNode.tries += 2;
    }

    private TreeNode searchChildren(TreeNode curNode, MNKCell cell) {
        if (cell == null || curNode == null || curNode.isFinished) return null;  // invalid input
        if (curNode.isLeaf) {
            return new TreeNode();
        }

        for (TreeNode child : curNode.children) {
            if (child.currMove.i == cell.i && child.currMove.j == cell.j) {
                return child;
            }
        }
        throw new RuntimeErrorException(null, "updateRoot: cell not found");
    }

    private void updateRoot(TreeNode cell) {
        if (cell == null) {
            throw new NullPointerException("the cell i want to update the root with is null");
        }

        root = cell;
        root.parent = null;  // scollego la root dal padre
        B.markCell(cell.currMove);
    }

    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        startTime = System.currentTimeMillis();
        MNKCell c = MC.length > 0 ? MC[MC.length - 1] : null;
        if (c != null)
            updateRoot(searchChildren(root, c));

        while (!hasTimeRunOut()) {
            TreeNode leaf = select(root);
            TreeNode child = expand(leaf);
            MNKGameState result = simulate(child);  // rollout
            backpropagate(result, child);
        }

        // O(n) bestNode with higher number of playouts
        TreeNode bestNode = null;
        int nTries = 0;
        for (TreeNode child : root.children) {
            if (child.tries > nTries) {
                nTries = child.tries;
                bestNode = child;
            }
        }

        // if (bestNode.isFinished) {
        //     System.out.println("bestNode.isFinished");
        //     System.out.print(root);
        // }

        updateRoot(bestNode);
        return bestNode.currMove;
    }

    @Override
    public String playerName() {
        return "Montecarlo Player";
    }
}
