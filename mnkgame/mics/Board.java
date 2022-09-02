package mnkgame.mics;

import java.lang.IllegalStateException;
import java.lang.IndexOutOfBoundsException;

import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

public class Board {
    public final int M;
    public final int N;
    public final int K;

    private final HeuristicCell[][] B;

    /**
     * tutte le celle in allCells minori di freeCellsCount
     * devono soddisfare l'invariante che puntino al proprio index all'interno dell'array
     * mentre tutte le celle mosse (quelle maggiori o uguali) devonon puntare all'index
     * in cui devono tornare per ristabilire l'ordine
     */
    public final HeuristicCell[] allCells;
    public int freeCellsCount;

    private final MNKCellState[] Player = {MNKCellState.P1, MNKCellState.P2};
    private int currentPlayer; // currentPlayer plays next move
    private MNKGameState gameState; // game state

    private MNKCellState ownerPlayer;
    private MNKCellState enemyPlayer;
    

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

        B = new HeuristicCell[M][N];
        allCells = new HeuristicCell[M * N];
        freeCellsCount = M * N;
        
        gameState = MNKGameState.OPEN;

        ownerPlayer = playerCode;
        enemyPlayer = playerCode == MNKCellState.P1 ? MNKCellState.P2 : MNKCellState.P1;
        currentPlayer = playerCode == MNKCellState.P1 ? 0 : 1;
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                B[i][j] = new HeuristicCell(i, j, i*N+j);
                allCells[i*N + j] = B[i][j];
            }
        }
    }

    /**
     * Returns the state of cell <code>i,j</code>
     *
     * @param i i-th row
     * @param j j-th column
     *
     * @return State of the <code>i,j</code> cell (FREE,P1,P2)
     */
    public MNKCellState getState(int i, int j) {
        return B[i][j].state;
    }

    public void print() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (B[i][j].state == MNKCellState.P1)
                    System.out.print("1 ");
                else if (B[i][j].state == MNKCellState.P2)
                    System.out.print("2 ");
                else
                    System.out.print("0 ");
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
        } else if (B[i][j].toMNKCell().state != MNKCellState.FREE) {
            throw new IllegalStateException("Cell " + i + "," + j + " is not free");
        }
        B[i][j].state = Player[currentPlayer];
        allCells[freeCellsCount - 1].index = B[i][j].index;  // setta in modo che l'indice di quello in fondo da spostare sia coerente con l'invariante
        swapAllCells(B[i][j].index, freeCellsCount - 1);
        freeCellsCount--;

        if (isWinningCell(i, j))
            gameState = B[i][j].state == MNKCellState.P1 ? MNKGameState.WINP1 : MNKGameState.WINP2;
        else if (freeCellsCount == 0)
            gameState = MNKGameState.DRAW;

        return gameState;
    }

    /**
     * Undoes last move
     *
     * @throws IllegalStateException If there is no move to undo
     */
    public void unmarkCell() throws IllegalStateException {
        if (freeCellsCount == M * N) {
            throw new IllegalStateException("No move to undo");
        } else {
            // freeCellsCount punta all'ultimo elemento moved
            allCells[freeCellsCount].state = MNKCellState.FREE;
            int oldIndex = allCells[freeCellsCount].index;
            swapAllCells(oldIndex, freeCellsCount);
            allCells[freeCellsCount].index = freeCellsCount;  // punta ancora a oldindex
            freeCellsCount++;
            gameState = MNKGameState.OPEN;
        }
    }

    boolean isValidCell(int i, int j) {
        return i >= 0 && i < M && j >= 0 && j < N;
    }

    public void setCellState(int i, int j, MNKCellState state) {
        B[i][j].state = state;
    }

    // questa funzione aggiorna l'euristica contando solamente una singola linea
    // lineCode: 1 -> verticale, 2 -> orizzontale, 3 -> diagonale, 4 -> antidiagonale
    private int getLineHeuristics(int i, int j, int lineCode) {
        int x_multiplier = lineCode == 1 ? 1 : lineCode == 2 ? 0 : lineCode == 3 ? 1 : 1;
        int y_multiplier = lineCode == 1 ? 0 : lineCode == 2 ? 1 : lineCode == 3 ? 1 : -1;

        int heuristic = 0;  // heuristic value to return
        int myCells = 0;  // number of myOwnCells in the window

        // creazione dello sliding windows
        int start = 0;
        int end = 1;
        while (end < K && isValidCell(i + end * y_multiplier, j + end * x_multiplier)) {
            if (B[i + end * y_multiplier][j + end * x_multiplier].state == ownerPlayer) {
                myCells++;
            } else if (B[i + end * y_multiplier][j + end * x_multiplier].state == enemyPlayer) {
                break;
            }
            end++;
        }
        end--; // così rientra all'ultimo valido 
        while (isValidCell(i + start * y_multiplier, j + start * x_multiplier) && end - start < K) {
            if (B[i + start * y_multiplier][j + start * x_multiplier].state == ownerPlayer) {
                myCells++;
            } else if (B[i + start * y_multiplier][j + start * x_multiplier].state == enemyPlayer) {
                break;
            }
            start--;
        }
        start++; // così rientra all'ultimo valido, stesso modo per end.

        // fine creazione sliding window
        if (end - start + 1 == K) {
            heuristic = myCells + 1;  // +1 perché è una sliding window valida
        } else {
            return 0; // non è possibile nemmeno creare un singolo sliding window in questa direzione
        }

        // go to next step
        start--;
        if (isValidCell(i + start * y_multiplier, j + start * x_multiplier) && B[i + start * y_multiplier][j + start * x_multiplier].state == ownerPlayer) {
            myCells++;
        }
        if (B[i + end * y_multiplier][j + end * x_multiplier].state == ownerPlayer) {  // sempre valido finché start è valido, no check per contorno
            myCells--;
        }
        end--;

        while (start > -K && isValidCell(i + start * y_multiplier, j + start * x_multiplier)) {
            if (B[i + start * y_multiplier][j + start * x_multiplier].state == enemyPlayer) break;
            
            heuristic++;  // ossia ho un altro blocco da K valido

            start--;
            if (!isValidCell(i + start * y_multiplier, j + start * x_multiplier)) break;
            if (B[i + start * y_multiplier][j + start * x_multiplier].state == ownerPlayer) {
                myCells++;
            }
            if (B[i + end * y_multiplier][j + end * x_multiplier].state == ownerPlayer) {
                myCells--;
            }
            end--;
            // ADD BONUS FOR NUMBER OF CELLS IN THE WINDOW
            // if (myCells >= K - 1) {
            //     System.out.print("adding bonus line code is: " + lineCode + "\n");
            //     heuristic += 4;  // BONUS PERICOLOSITÀ
            // }
        }

        return heuristic;
    }

    // checks if the cell has K - 2 samekind in a row
    // the concept is the same as markCell, so it could be implemented there,
    // but for clarity i make it his own function
    public int getAlmostKHeuristics(int i, int j) {
        if (B[i][j].state == MNKCellState.FREE) return 0;
        final int almostKGain = 8; // dovrebbe essere diverso a seconda della grandezza della board
        int heuristic = 0;
        MNKCellState state = B[i][j].state;
        // Horizontal check
        int n = 1;
        for (int k = 1; j - k >= 0 && B[i][j - k].state == state; k++) n++; // backward check
        if (n == K - 1) heuristic += almostKGain;
        n = 1;
        for (int k = 1; j + k < N && B[i][j + k].state == state; k++) n++; // forward check
        if (n == K - 1) heuristic += almostKGain;

        // Vertical check
        n = 1;
        for (int k = 1; i - k >= 0 && B[i - k][j].state == state; k++) n++; // backward check
        if (n == K - 1) heuristic += almostKGain;
        n = 1;
        for (int k = 1; i + k < M && B[i + k][j].state == state; k++) n++; // forward check
        if (n == K - 1) heuristic += almostKGain;
        n = 1;

        // Diagonal check
        n = 1;
        for (int k = 1; i - k >= 0 && j - k >= 0 && B[i - k][j - k].state == state; k++) n++; // backward check
        if (n == K - 1) heuristic += almostKGain;
        n = 1;
        for (int k = 1; i + k < M && j + k < N && B[i + k][j + k].state == state; k++) n++; // forward check
        if (n == K - 1) heuristic += almostKGain;

        // Anti-diagonal check
        n = 1;
        for (int k = 1; i + k < M && j - k >= 0 && B[i + k][j - k].state == state; k++) n++; // backward check
        if (n == K - 1) heuristic += almostKGain;
        n = 1;
        for (int k = 1; i - k >= 0 && j + k < N && B[i - k][j + k].state == state; k++) n++; // backward check
        if (n == K - 1) heuristic += almostKGain;
        
        return heuristic;
    }

    // questa funzione deve aggiornare le euristiche seguendo il metodo di
    // Nathaniel Hayes and Teig Loge nel paper 2016, contando le mosse disponibili.
    // questa implementazione ricacola sempre l'euristica ogni step, si può migliorare
    // facendo Dinamic programming, ma per quanto esposto poi dovrebbe funzioanre ugualmente
    public int getHeuristic(int i, int j) {
        if (B[i][j].state == enemyPlayer) {
            return 0;
        }
        
        int heuristic = 0;
        for (int k = 1; k <= 4; k++) heuristic += getLineHeuristics(i, j, k);
        return heuristic;
    }

    // ritorna i valori euristica per il nemico
    public int getSwappedHeuristics(int i, int j) {
        if (B[i][j].state == ownerPlayer) {
            return 0;
        }

        MNKCellState tmp = ownerPlayer;
        ownerPlayer = enemyPlayer;
        enemyPlayer = tmp;

        int heuristic = 0;
        for (int k = 1; k <= 4; k++) heuristic += getLineHeuristics(i, j, k);

        tmp = ownerPlayer;
        ownerPlayer = enemyPlayer;
        enemyPlayer = tmp;

        return heuristic;
    }

    public void setPlayer(MNKCellState player) {
        currentPlayer = player == MNKCellState.P1 ? 0 : 1;
    }

    private void swapAllCells(int i, int j) {
        HeuristicCell cell = allCells[i];
        allCells[i] = allCells[j];
        allCells[j] = cell;
    }

    // Check winning state from cell i, j
    private boolean isWinningCell(int i, int j) {
        MNKCellState state = B[i][j].state;
        int n;

        // Useless pedantic check
        if (state == MNKCellState.FREE)
            return false;

        // Horizontal check
        n = 1;
        for (int k = 1; j - k >= 0 && B[i][j - k].state == state; k++) n++; // backward check
        for (int k = 1; j + k < N && B[i][j + k].state == state; k++) n++; // forward check
        if (n >= K)
            return true;

        // Vertical check
        n = 1;
        for (int k = 1; i - k >= 0 && B[i - k][j].state == state; k++) n++; // backward check
        for (int k = 1; i + k < M && B[i + k][j].state == state; k++) n++; // forward check
        if (n >= K)
            return true;

        // Diagonal check
        n = 1;
        for (int k = 1; i - k >= 0 && j - k >= 0 && B[i - k][j - k].state == state; k++) n++; // backward check
        for (int k = 1; i + k < M && j + k < N && B[i + k][j + k].state == state; k++) n++; // forward check
        if (n >= K)
            return true;

        // Anti-diagonal check
        n = 1;
        for (int k = 1; i - k >= 0 && j + k < N && B[i - k][j + k].state == state; k++) n++; // backward check
        for (int k = 1; i + k < M && j - k >= 0 && B[i + k][j - k].state == state; k++) n++; // backward check
        if (n >= K)
            return true;

        return false;
    }
}
