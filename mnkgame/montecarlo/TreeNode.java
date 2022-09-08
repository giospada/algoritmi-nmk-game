package mnkgame.montecarlo;

import java.util.PriorityQueue;

import mnkgame.MNKCell;

public class TreeNode implements Comparable<TreeNode>{
    public TreeNode parent;
    public int tries;
    public int goodTries;
    public HeuristicCell currMove;
    public boolean isFinished;
    public boolean isLeaf;
    public PriorityQueue<TreeNode> children;
    
    public TreeNode() {
        this(null);
    }

    public TreeNode(TreeNode parent) {
       this(parent, new HeuristicCell(-1,-1,-1));
    }

    public TreeNode(TreeNode parent, HeuristicCell heuristicCell) {
        this.parent = parent;
        this.tries = 0;
        this.goodTries = 0;
        this.currMove = heuristicCell;
        this.isFinished = false;
        this.isLeaf = true;
        this.children = new PriorityQueue<TreeNode>();
    }

    public TreeNode createNextChild(Board board){
        if (children.size() == board.freeCellsCount) return null;
        TreeNode child = new TreeNode(this, board.getGreatKCell(children.size()));
        children.add(child);
        return child;
    }

    double upperConfidenceBound() {
        if (tries == 0)
            return Double.MAX_VALUE;
        double value = goodTries * 1.0 / tries;
        if (parent == null)
            return value;
        else
            return value + 1.414 * Math.sqrt(Math.log(parent.tries) / tries);
    }

    @Override
    public int compareTo(TreeNode o) {
        return upperConfidenceBound() > o.upperConfidenceBound() ? -1 : 1;  // descending order
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{currMove: '" + currMove.toString() + "', tries: " + tries + ", goodTries: " + goodTries + ", UCB: " + upperConfidenceBound() + ", isFinished: " + isFinished + ", isLeaf: " + isLeaf + ", children: [ ");
        TreeNode[] childrenArray = children.toArray(new TreeNode[children.size()]);
        for (int i = 0; i < childrenArray.length; i++) {
            if (i != childrenArray.length - 1)
                sb.append(childrenArray[i].toString() + ", ");
            else
                sb.append(childrenArray[i].toString());
        }
        sb.append("]}");
        return sb.toString();
    }
}
