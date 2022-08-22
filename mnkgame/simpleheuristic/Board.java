

package mnkgame.simpleheuristic;

import java.lang.IllegalStateException;
import java.lang.IndexOutOfBoundsException;
import java.util.HashSet;
import java.util.LinkedList;

import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import mnkgame.MNKCell;

public class Board {
    public final int M;
    public final int N;
    public final int K;

    protected final MNKCellState[][] B;
    protected final LinkedList<MNKCell> MC; // Marked Cells
    protected final HashSet<MNKCell> FC; // Free Cells

    private final MNKCellState[] Player = {MNKCellState.P1, MNKCellState.P2};
    protected int currentPlayer; // currentPlayer plays next move
    protected MNKGameState gameState; // game state

    protected int globalHeuristicCount;
    protected final int[][] Heuristic;
    protected final MNKCellState owner;

    /**
     * Create a board of size MxN and initialize the game parameters
     *
     * @param M Board rows
     * @param N Board columns
     * @param K Number of symbols to be aligned (horizontally, vertically, diagonally) for a win
     *
     * @throws IllegalArgumentException If M,N,K are smaller than  1
     */
    public Board(int M, int N, int K, MNKCellState playerCode) throws IllegalArgumentException {
        if (M <= 0)
            throw new IllegalArgumentException("M cannot be smaller than 1");
        if (N <= 0)
            throw new IllegalArgumentException("N cannot be smaller than 1");
        if (K <= 0)
            throw new IllegalArgumentException("K cannot be smaller than 1");

        this.M = M;
        this.N = N;
        this.K = K;

        B = new MNKCellState[M][N];
        Heuristic = new int[M][N];
        owner = playerCode;
        globalHeuristicCount = 0;

        // large HashSet, so that it should never reallocate.
        FC = new HashSet<MNKCell>(2 * M * N);
        MC = new LinkedList<MNKCell>();

        reset();
    }

    /**
     * Resets the MNKBoard
     */
    public void reset() {
        currentPlayer = 0;
        gameState = MNKGameState.OPEN;
        initBoard();
        initFreeCellList();
        initMarkedCellList();
    }

    /**
     * Returns the state of cell <code>i,j</code>
     *
     * @param i i-th row
     * @param j j-th column
     *
     * @return State of the <code>i,j</code> cell (FREE,P1,P2)
     * @throws IndexOutOfBoundsException If <code>i,j</code> are out of matrix bounds
     */
    public MNKCellState cellState(int i, int j) throws IndexOutOfBoundsException {
        if (i < 0 || i >= M || j < 0 || j >= N)
            throw new IndexOutOfBoundsException("Indexes " + i + "," + j + " are out of matrix bounds");
        else
            return B[i][j];
    }

    public void print() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(B[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }
    /**
     * Returns the current state of the game.
     *
     * @return MNKGameState enumeration constant (OPEN,WINP1,WINP2,DRAW)
     */
    public MNKGameState gameState() {
        return gameState;
    }

    /**
     * Returns the id of the player allowed to play next move.
     *
     * @return 0 (first player) or 1 (second player)
     */
    public int currentPlayer() {
        return currentPlayer;
    }

    /**
     * Marks the selected cell for the current player
     *
     * @param i i-th row
     * @param j j-th column
     *
     * @return State of the game after the move
     *
     * @throws IndexOutOfBoundsException If <code>i,j</code> are out of matrix bounds
     * @throws IllegalStateException If the game already ended or if <code>i,j</code> is not a free cell
     */
    public MNKGameState markCell(int i, int j) throws IndexOutOfBoundsException, IllegalStateException {
        if (gameState != MNKGameState.OPEN) {
            throw new IllegalStateException("Game ended!");
        } else if (i < 0 || i >= M || j < 0 || j >= N) {
            throw new IndexOutOfBoundsException("Indexes " + i + "," + j + " out of matrix bounds");
        } else if (B[i][j] != MNKCellState.FREE) {
            throw new IllegalStateException("Cell " + i + "," + j + " is not free");
        } else {
            MNKCell oldc = new MNKCell(i, j, B[i][j]);
            MNKCell newc = new MNKCell(i, j, Player[currentPlayer]);

            B[i][j] = Player[currentPlayer];

            FC.remove(oldc);
            MC.add(newc);

            currentPlayer = (currentPlayer + 1) % 2;

            if (isWinningCell(i, j))
                gameState = B[i][j] == MNKCellState.P1 ? MNKGameState.WINP1 : MNKGameState.WINP2;
            else if (FC.isEmpty())
                gameState = MNKGameState.DRAW;

            return gameState;
        }
    }

    /**
     * Undoes last move
     *
     * @throws IllegalStateException If there is no move to undo
     */
    public void unmarkCell() throws IllegalStateException {
        if (MC.size() == 0) {
            throw new IllegalStateException("No move to undo");
        } else {
            MNKCell oldc = MC.removeLast();
            MNKCell newc = new MNKCell(oldc.i, oldc.j, MNKCellState.FREE);
            restoreHeuristic(oldc.i, oldc.j);
            B[oldc.i][oldc.j] = MNKCellState.FREE;

            FC.add(newc);
            currentPlayer = (currentPlayer + 1) % 2;
            gameState = MNKGameState.OPEN;
        }
    }

    public MNKCell[] getMarkedCells() {
        return MC.toArray(new MNKCell[MC.size()]);
    }

    public MNKCell[] getFreeCells() {
        return FC.toArray(new MNKCell[FC.size()]);
    }

    private void initBoard() {
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++) {
                B[i][j] = MNKCellState.FREE;
                Heuristic[i][j] = 0;
            }
    }

    private void initFreeCellList() {
        this.FC.clear();
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                this.FC.add(new MNKCell(i, j));
    }

    private void initMarkedCellList() {
        this.MC.clear();
    }

    // Euristica di Chua Hock Chuan
    // @considerazioni:
    // ma ha senso che 10 configurazioni quasi vincenti equivalgano a uno vincente???
    private void updateHeuristic(int i, int j, int n) {
        if (B[i][j] == owner) {
            if (n >= K) {
                Heuristic[i][j] += 100;
            } else if (n == K - 1) {
                Heuristic[i][j] += 10;
            } else if (n == K - 2) {
                Heuristic[i][j] += 1;
            }  // else add 0
        } else {
            if (n >= K) {
                Heuristic[i][j] += -100;
            } else if (n == K - 1) {
                Heuristic[i][j] += -10;
            } else if (n == K - 2) {
                Heuristic[i][j] += -1;
            }  // else add 0
        }
        globalHeuristicCount += Heuristic[i][j];
    }

    // this could be used for another heuristic evaluation?
    // ha senso che sia pubblico? per ora lo uso per fare un filtro iniziale
    // nel minimax player
    public void togglePlayer() {
        currentPlayer = (currentPlayer + 1) % 2;
    }

    private void restoreHeuristic(int i, int j) {
        globalHeuristicCount -= Heuristic[i][j];
        Heuristic[i][j] = 0;
    }

    public int getHeuristicValue() {
        return globalHeuristicCount;
    }

    // Check winning state from cell i, j
    private boolean isWinningCell(int i, int j) {
        MNKCellState s = B[i][j];
        int n;

        // Useless pedantic check
        if (s == MNKCellState.FREE)
            return false;

        // Horizontal check
        n = 1;
        for (int k = 1; j - k >= 0 && B[i][j - k] == s; k++) n++; // backward check
        for (int k = 1; j + k < N && B[i][j + k] == s; k++) n++; // forward check
        updateHeuristic(i, j, n);
        if (n >= K)
            return true;

        // Vertical check
        n = 1;
        for (int k = 1; i - k >= 0 && B[i - k][j] == s; k++) n++; // backward check
        for (int k = 1; i + k < M && B[i + k][j] == s; k++) n++; // forward check
        updateHeuristic(i, j, n);
        if (n >= K)
            return true;

        // Diagonal check
        n = 1;
        for (int k = 1; i - k >= 0 && j - k >= 0 && B[i - k][j - k] == s; k++) n++; // backward check
        for (int k = 1; i + k < M && j + k < N && B[i + k][j + k] == s; k++) n++; // forward check
        updateHeuristic(i, j, n);
        if (n >= K)
            return true;

        // Anti-diagonal check
        n = 1;
        for (int k = 1; i - k >= 0 && j + k < N && B[i - k][j + k] == s; k++) n++; // backward check
        for (int k = 1; i + k < M && j - k >= 0 && B[i + k][j - k] == s; k++) n++; // backward check
        updateHeuristic(i, j, n);
        if (n >= K)
            return true;

        return false;
    }
}
