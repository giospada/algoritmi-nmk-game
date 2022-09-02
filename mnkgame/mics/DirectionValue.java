package mnkgame.mics;

public class DirectionValue {
    public int left;
    public int right;
    public int center;

    // if center is invalid these two's values are invalid
    public int centerLeft;  // offset of the best-center-value to the left
    public int centerRight;  // offset of the best-center-value to the right
    
    DirectionValue() {
        this(0);
    }

    DirectionValue(int same) {
        left = same;
        right = same;
        center = Integer.MAX_VALUE;
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
}
