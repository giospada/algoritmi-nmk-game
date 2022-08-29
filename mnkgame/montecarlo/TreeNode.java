package mnkgame.montecarlo;

import mnkgame.MNKCell;

public class TreeNode implements Comparable<TreeNode>{
    public TreeNode parent;
    public int tries;
    public int goodTries;
    public MNKCell currMove;
    
    public TreeNode() {
        this(null);
    }

    public TreeNode(TreeNode parent) {
        this(parent,new MNKCell(-1, -1))
    }
    public TreeNode(TreeNode parent, MNKCell currMove) {
        this.parent = parent;
        this.tries = 0;
        this.goodTries = 0;
        this.currMove=currMove;
    }

    double upperConfidenceBound() {
        double value = tries * 1.0 / goodTries;
        if (parent == null)
            return value;
        else 
            return value + 1.414 * Math.sqrt(Math.log(parent.tries) / tries);
    }

    @Override
    public int compareTo(TreeNode o) {
        return upperConfidenceBound() > o.upperConfidenceBound() ? 1 : -1;  // descending order
    }
}
