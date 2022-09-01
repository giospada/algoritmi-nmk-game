package mnkgame.montecarlo;

import java.util.PriorityQueue;

import javax.management.RuntimeErrorException;

import mnkgame.MNKBoard;
import mnkgame.MNKCell;
import mnkgame.MNKGameState;
import mnkgame.MNKPlayer;

public class Player implements MNKPlayer {
    private MNKBoard B; // TODO make board
    private long startTime;
    private int TIMEOUT;
    private TreeNode[] possibleMoves;
    int possibleMovesLenght;
    TreeNode root;


    public Player() {}

    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        TIMEOUT = timeout_in_secs;
        B = new MNKBoard(M, N, K);
        root = null;  // TODO spostamento della root a seconda delle mosse.
    }

    private boolean hasTimeRunOut() {
        return (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (50.0 / 100.0);
    }

    void applyCurrNodeMoves(TreeNode currNode) {
        if (currNode.parent == null) {
            return;
        }
        applyCurrNodeMoves(currNode.parent);
        B.markCell(currNode.currMove.i, currNode.currMove.j);  // TODO fallo tempo costante
    }

    public MNKGameState myWin;
    public MNKGameState yourWin;
    TreeNode select(TreeNode curNode) {
        // if (curNode == root) {
        //     curNode = curNode.children.peek();
        // }
        while (!curNode.isLeaf && !curNode.isFinished) {
            curNode = curNode.children.peek();
            B.markCell(curNode.currMove);
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
            MNKCell[] freeCells = B.getFreeCells();
            numMoves++;
            nextCell = freeCells[(int) (Math.random() * freeCells.length)];
            lastState = B.markCell(nextCell);
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
        } else if (state == MNKGameState.DRAW) {
            addingValue = 1;
        }

        while (curNode.parent != null) {
            curNode.goodTries += addingValue;

            curNode.tries += 2;
            curNode.parent.children.remove(curNode);
            curNode.parent.children.add(curNode);  // riaggiorna la posizione del nodo nella priority

            B.unmarkCell();

            curNode = curNode.parent;
        }

        // aggiorna valori della ROOT
        curNode.goodTries += addingValue;
        curNode.tries += 2;
    }
    private TreeNode searchChildren(TreeNode curNode,MNKCell cell) {
        if (cell == null) return null;
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
        if (cell == null) {  // 
            root = new TreeNode();
            return;
        }
        root = cell;
        root.parent = null;  // scollego la root dal padre
        B.markCell(cell.currMove);
    }

    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        startTime = System.currentTimeMillis();
        MNKCell c = MC.length > 0 ? MC[MC.length - 1] : null;
        updateRoot(searchChildren(root, c));

        // queue = new PriorityQueue<TreeNode>();

        // queue.add(root);

        while (!hasTimeRunOut()) {
            // this is pseudo code
            // TreeNode leaf = queue.peek();  // with the board state, or initTree
            // if (leaf.parent == root) {
            //     possibleMoves[possibleMovesLenght] = leaf;
            //     possibleMovesLenght++;
            // }
            TreeNode leaf = select(root);
            TreeNode child = expand(leaf);
            MNKGameState result = simulate(child);  // rollout
            backpropagate(result, child);
            // System.out.println(root);
            // if (!child.isFinished) {
            //     queue.add(child);
            // }
        }

        // TODO: return the best cell, highest number of playouts.
        TreeNode bestNode = root.children.peek();
        if(FC.length > 7)
            System.err.println(root);
        updateRoot(bestNode);
        return bestNode.currMove;
    }

    @Override
    public String playerName() {
        return "Montecarlo Player";
    }
}
