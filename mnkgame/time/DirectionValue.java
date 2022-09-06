package mnkgame.time;

public class DirectionValue {
    public int left;  // numero minimo di celle per vincere andando solo a sinistra
    public int right;  // numero minimo di celle per vincere andando solo a destra
    public int center;  // numero minimo di celle per vincere per l'intera direzione
    public int numSliding;  // numero di sliding windows validi
    public int numtwos;  // numero di sliding windows a cui mancano 2 per finire (uno sicuramente è al centro)

    // usando il mics, il numero di sliding windows buone, + numero di celle amiche
    private int value;
    
    DirectionValue() {
        this(0);
    }

    DirectionValue(int same) {
        resetTo(same);
    }

    /**
     * Computes the value of the direction using MICS (Minimum Incomplete Cell Set)
     * heuristics.
     * @param K
     */
    public void computeValue(int K) {
        value = numSliding;
        if (left >= 0) 
            value += K - left;

        if (right >= 0)
            value += K - right;
    }

    public int getValue() {
        return value;
    }

    public void resetTo(int same) {
        left = same;
        right = same;
        center = Integer.MAX_VALUE;
        numSliding = 0;
        numtwos = 0;
    }

    public void setInvalidDirectionValue() {
        this.left = -1;
        this.right = -1;
        this.numSliding = 0;
        this.numtwos = 0;
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

    /**
     * @return if it's possible to have a trivial double play win
     * This is true if at least two sliding windows have need two moves to win
     */
    public boolean isInLineDoublePlay() {
        return numtwos >= 2;
    }
    
    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", left, center, right);
    }
}
