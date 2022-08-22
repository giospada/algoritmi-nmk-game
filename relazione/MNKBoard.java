package relazione;
import mnkgame.*;

import java.lang.IllegalStateException;
import java.lang.IndexOutOfBoundsException;
import java.util.HashSet;
import java.util.LinkedList;

// versione più breve della board, senza alcune funzioni e checks usati per il testing
// sperimentale della velocità di esecuzione
public class MNKBoard {
    public final int M;
    public final int N;
    public final int K;

    protected final MNKCellState[][] B;
    protected final LinkedList<MNKCell> MC; // Marked Cells
    protected final HashSet<MNKCell> FC; // Free Cells

    private final MNKCellState[] Player = {MNKCellState.P1, MNKCellState.P2};

    protected int currentPlayer; // currentPlayer plays next move

    protected MNKGameState gameState; // game state

    public MNKBoard(int M, int N, int K) throws IllegalArgumentException {
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
        // Initial capacity large enough to assure load factor < 0.75
        FC = new HashSet<MNKCell>((int) Math.ceil((M * N) / 0.75));
        MC = new LinkedList<MNKCell>();
        
        currentPlayer = 0;
        gameState = MNKGameState.OPEN;
        initBoard();
        initFreeCellList();
        initMarkedCellList();
    }

    public MNKGameState markCell(int i, int j) throws IndexOutOfBoundsException, IllegalStateException {
        // ho tolto il check a open
        if (i < 0 || i >= M || j < 0 || j >= N) {
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

    public void unmarkCell() throws IllegalStateException {
        if (MC.size() == 0) {
            throw new IllegalStateException("No move to undo");
        } else {
            MNKCell oldc = MC.removeLast();
            MNKCell newc = new MNKCell(oldc.i, oldc.j, MNKCellState.FREE);

            B[oldc.i][oldc.j] = MNKCellState.FREE;

            FC.add(newc);
            currentPlayer = (currentPlayer + 1) % 2;
            gameState = MNKGameState.OPEN;
        }
    }

    // Sets to free all board cells
    private void initBoard() {
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                B[i][j] = MNKCellState.FREE;
    }

    // Rebuilds the free cells set
    private void initFreeCellList() {
        this.FC.clear();
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                this.FC.add(new MNKCell(i, j));
    }

    // Resets the marked cells list
    private void initMarkedCellList() {
        this.MC.clear();
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
        if (n >= K)
            return true;

        // Vertical check
        n = 1;
        for (int k = 1; i - k >= 0 && B[i - k][j] == s; k++) n++; // backward check
        for (int k = 1; i + k < M && B[i + k][j] == s; k++) n++; // forward check
        if (n >= K)
            return true;

        // Diagonal check
        n = 1;
        for (int k = 1; i - k >= 0 && j - k >= 0 && B[i - k][j - k] == s; k++) n++; // backward check
        for (int k = 1; i + k < M && j + k < N && B[i + k][j + k] == s; k++) n++; // forward check
        if (n >= K)
            return true;

        // Anti-diagonal check
        n = 1;
        for (int k = 1; i - k >= 0 && j + k < N && B[i - k][j + k] == s; k++) n++; // backward check
        for (int k = 1; i + k < M && j - k >= 0 && B[i + k][j - k] == s; k++) n++; // backward check
        if (n >= K)
            return true;

        return false;
    }
}
