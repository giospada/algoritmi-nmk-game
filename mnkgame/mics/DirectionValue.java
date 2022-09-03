package mnkgame.mics;

import mnkgame.MNKCellState;

public class DirectionValue {
    public int left;
    public int right;
    public int center;

    public boolean leftIsFree;  // leftmost cell
    public boolean rightIsFree; // rightmost cell
    
    DirectionValue() {
        this(0);
    }

    DirectionValue(int same) {
        left = same;
        right = same;
        center = Integer.MAX_VALUE;
    }
    

    public void setInvalidDirectionValue() {
        this.left = -1;
        this.right = -1;
        this.center = -1;
    }
    /**
     * @return if it's possible to win (i.e. if there is at least one way to win in this direction)
     */
    public boolean isPossible() {
        return center >= 0;  // NOTA: se left o right >= 0, allora center >= 0
    }

    /**
     * @return the minimum number of steps to win in this direction, -1 if you can't win
     */
    public int bestWin() {
        if (left == -1 && right == -1) {
            return center;
        // da ora so che center >= 0;
        } else if (left == -1) {
            return Math.min(right, center);
        } else if (right == -1) {
            return Math.min(left, center);
        } else {
            return Math.min(Math.min(left, right), center);
        }
    }

    //
    public boolean isInLineDoublePlay() {
        if (left <= 2 && leftIsFree) return true;
        if (right <= 2 && rightIsFree) return true;

        return false;
    }
}
