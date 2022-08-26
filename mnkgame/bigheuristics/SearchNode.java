package mnkgame.bigheuristics;

public class SearchNode implements Comparable<SearchNode>  {
    // tutte le mosse fino ad arrivare al parent
    // potrei anche non fare questo array, ma credo sia più veloce rispetto a ripercorrere
    // la linked list fino al root per avere tutte le mosse.
    public Cell[] moves;  

    public int value;
    public boolean minimize;
    public SearchNode parent;
    public int alpha;
    public int beta;

    SearchNode(int i, int j, int value) {
        moves = new Cell[1];
        moves[0] = new Cell(i, j);
        this.value = value;
        minimize = false;
        parent = null;
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
    }   

    SearchNode(Cell[] moves, int value, boolean minimize, SearchNode parent, int alpha, int beta) {
        this.moves = moves;
        this.value = value;
        this.minimize = minimize;
        this.parent = parent;
        this.alpha = alpha;
        this.beta = beta;
    }

    SearchNode expand(int i, int j, int value) {
        Cell[] newMoves = new Cell[moves.length + 1];
        for (int k = 0; k < moves.length; k++) {
            newMoves[k] = new Cell(moves[k]);
        }
        return new SearchNode(newMoves, value, !minimize, this, alpha, beta);
    }

    // uses the node current value to backtrack
    void backtrack() {
        SearchNode node = this;
        while (node.parent != null) {
            // l'alpha beta non serve a niente???
            if (node.parent.minimize) {
                if (node.parent.value > node.value) {
                    node.parent.value = node.value;
                } else break;  // si suppone che il genitore sia già apposto
            } else {
                if (node.parent.value < node.value) {
                    node.parent.value = node.value;
                } else break;
            }
            node = node.parent;
        }
    }

    /** backtracks with a value */
    public void backtrack(int value) {
        if (minimize) {
            if (this.value > value) {
                this.value = value;
            } else return;
        } else {
            if (this.value < value) {
                this.value = value;
            } else return;
        }

        this.backtrack();
    }

    Cell getFirstMove() {
        if (moves.length == 0) throw new IllegalStateException("No moves present, cant get any");
        return moves[0];
    }

    public int compareTo(SearchNode other) {
        return other.value - this.value;  // descending order
     }

     @Override
     public String toString() {
        String s = "";
        for (int i = 0; i < moves.length; i++) {
            s += moves[i].toString() + ' ' + this.value + " |";
        }
        return s;
     }
}
