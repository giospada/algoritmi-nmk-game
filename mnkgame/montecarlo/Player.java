package mnkgame.montecarlo;

import java.util.PriorityQueue;

import mnkgame.MNKBoard;
import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGame;
import mnkgame.MNKGameState;
import mnkgame.MNKPlayer;

public class Player implements MNKPlayer {

    private MNKBoard B; // TODO make board
    private long startTime;

    private boolean hasTimeRunOut() {
        return (System.currentTimeMillis() - startTime) / 1000.0 > TIMEOUT * (99.0 / 100.0);
    }

    void applyCurrNodeMoves(TreeNode currNode) {
        if (currNode.parent == null) {
            return;
        }
        applyCurrNodeMoves(currNode.parent);
        B.markCell(currNode.currMove.i, currNode.currMove.j);
    }

    public MNKGameState myWin;
    public MNKGameState yourWin;

    TreeNode expand(TreeNode parentNode) {
        // TODO: Fai con l'eursitica per ora facciamo random
        MNKCell selectedCell = B.getFreeCells()[0];
        return new TreeNode(parentNode, selectedCell);
    }

    MNKGameState simulate(TreeNode curNode) {
        MNKCell nextCell = curNode.currMove;
        int numMoves=1;
        MNKGameState lastState;
        while((lastState = B.markCell(nextCell))==MNKGameState.OPEN){
            numMoves++;
            nextCell = B.getFreeCells()[0];
        }
        for(int i=0;i<numMoves;i++){
            B.unmarkCell();
        }
        return lastState;
    }
    void backpropagate(MNKGameState state, TreeNode curNode) {
        if (curNode.parent == null) {
            return;
        }
        backpropagate(state, curNode.parent);

        if (myWin == state) {
            curNode.goodTries += 2 ;
        }else if(state == MNKGameState.DRAW){
            curNode.goodTries += 1;
        }
        curNode.tries += 2;

    }

    @Override
    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
    }

    @Override
    public MNKCell selectCell(MNKCell[] FC, MNKCell[] MC) {
        startTime = System.currentTimeMillis();
        if (MC.length > 0) {
            MNKCell c = MC[MC.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
        }

        TreeNode root = new TreeNode();
        PriorityQueue<TreeNode> queue = new PriorityQueue<TreeNode>();
        queue.add(root);

        while (!hasTimeRunOut() && !queue.isEmpty()) {
            // this is pseudo code
            TreeNode leaf = queue.poll();  // with the board state, or initTree
            applyCurrNodeMoves(leaf);

            TreeNode child = expand(leaf);
            MNKGameState result = simulate(child);
            backpropagate(result, leaf);
        }

        // TODO: return the best cell, highest number of playouts.
        return null;
    }

    @Override
    public String playerName() {
        return "Montecarlo Player";
    }
}
