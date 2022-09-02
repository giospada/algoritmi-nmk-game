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

    private MNKCellState allyPlayer;  // alleato di sé stesso
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

        allyPlayer = playerCode;
        enemyPlayer = playerCode == MNKCellState.P1 ? MNKCellState.P2 : MNKCellState.P1;
        currentPlayer = playerCode == MNKCellState.P1 ? 0 : 1;
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                B[i][j] = new HeuristicCell(i, j, i * N + j);
                allCells[i*N + j] = B[i][j];
            }
        }

        // deve essere in for separato perché vuole prima avere una board inizializzata
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                B[i][j].allyValue = computeCellValue(i, j, allyPlayer);
                B[i][j].enemyValue = computeCellValue(i, j, enemyPlayer);
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
            if (B[i + end * y_multiplier][j + end * x_multiplier].state == allyPlayer) {
                myCells++;
            } else if (B[i + end * y_multiplier][j + end * x_multiplier].state == enemyPlayer) {
                break;
            }
            end++;
        }
        end--; // così rientra all'ultimo valido 
        while (isValidCell(i + start * y_multiplier, j + start * x_multiplier) && end - start < K) {
            if (B[i + start * y_multiplier][j + start * x_multiplier].state == allyPlayer) {
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
        if (isValidCell(i + start * y_multiplier, j + start * x_multiplier) && B[i + start * y_multiplier][j + start * x_multiplier].state == allyPlayer) {
            myCells++;
        }
        if (B[i + end * y_multiplier][j + end * x_multiplier].state == allyPlayer) {  // sempre valido finché start è valido, no check per contorno
            myCells--;
        }
        end--;

        while (start > -K && isValidCell(i + start * y_multiplier, j + start * x_multiplier)) {
            if (B[i + start * y_multiplier][j + start * x_multiplier].state == enemyPlayer) break;
            
            heuristic++;  // ossia ho un altro blocco da K valido

            start--;
            if (!isValidCell(i + start * y_multiplier, j + start * x_multiplier)) break;
            if (B[i + start * y_multiplier][j + start * x_multiplier].state == allyPlayer) {
                myCells++;
            }
            if (B[i + end * y_multiplier][j + end * x_multiplier].state == allyPlayer) {
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
        if (B[i][j].state == allyPlayer) {
            return 0;
        }

        MNKCellState tmp = allyPlayer;
        allyPlayer = enemyPlayer;
        enemyPlayer = tmp;

        int heuristic = 0;
        for (int k = 1; k <= 4; k++) heuristic += getLineHeuristics(i, j, k);

        tmp = allyPlayer;
        allyPlayer = enemyPlayer;
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

    private int getHorizontalAdder(int lineCode) {
        return lineCode == 1 ? 1 : lineCode == 2 ? 0 : lineCode == 3 ? 1 : 1;
    }

    private int getVerticalAdder(int lineCode) {
        return lineCode == 1 ? 0 : lineCode == 2 ? 1 : lineCode == 3 ? 1 : -1;
    }

    /**
     * This should be O(K)
     * @param lineCode 1 = horizontal, 2 = vertical, 3 = diagonal, 4 = anti-diagonal
     * @param state, lo stato per cercare il valore (NON HA SENDO AVERE LO STATE FREE)
     * @return the heuristics value for the current board for that cell.
     */
    public DirectionValue computeCellDirectionValue(int i, int j, int lineCode, MNKCellState state) {
        MNKCellState opponentState = state == MNKCellState.P1 ? MNKCellState.P2 : MNKCellState.P1;
        if (B[i][j].state == opponentState) {
            throw new IllegalArgumentException("Cell is occupied by opponent");
        }

        int xAdd = getHorizontalAdder(lineCode);
        int yAdd = getVerticalAdder(lineCode);
        DirectionValue dirValue = new DirectionValue(B[i][j].state == state ? 0 : 1);

        int right = 1, left = 1;
        int numberOfOwnCells = B[i][j].state == state ? 1 : 0;
        while (right < K) {
            int rightIidx = i + right * yAdd;
            int rightJidx = j + right * xAdd;
            if (!isValidCell(rightIidx, rightJidx) || B[rightIidx][rightJidx].state == opponentState) {
                dirValue.right = -1;
                break;
            }
    
            if (B[rightIidx][rightJidx].state == MNKCellState.FREE) {
                dirValue.right++;
            }  else {
                numberOfOwnCells++;
            }
            right++;
        }
        right--;

        // set the first possible value for the center
        if (dirValue.right != -1) {
            dirValue.center = dirValue.right;
            dirValue.centerRight = right;
            dirValue.centerLeft = 0;
        }

        // scorrimento a sinistra con la sliding window
        while (left < K) {
            int leftIidx = i - left * yAdd;
            int leftJidx = j - left * xAdd;
            if (!isValidCell(leftIidx, leftJidx) || B[leftIidx][leftJidx].state == opponentState) {
                dirValue.left = -1;
                break;
            }
            
            if (right + left == K) {
                if (B[i + right * yAdd][j + right * xAdd].state == state) {
                    numberOfOwnCells--;
                }
                right--;
            }

            if (B[leftIidx][leftJidx].state == MNKCellState.FREE) {
                dirValue.left++;
            }  else {
                numberOfOwnCells++;
            }

            // calcola il centro solo se la slinding window ha lunghezza già adeguata (K)
            if (right + left == K - 1) {
                int centerToFill = K - numberOfOwnCells;
                if (centerToFill < dirValue.center) {
                    dirValue.center = centerToFill;
                    dirValue.centerLeft = left;
                    dirValue.centerRight = right;
                }
            }
            left++;
        }

        if (dirValue.center == Integer.MAX_VALUE) dirValue.center = -1;

        return dirValue;
    }

    public Value computeCellValue(int i, int j, MNKCellState state) {
        Value cellValue = new Value();
        cellValue.horiz = computeCellDirectionValue(i, j, 1, state);
        cellValue.vert = computeCellDirectionValue(i, j, 2, state);
        cellValue.diag1 = computeCellDirectionValue(i, j, 3, state);
        cellValue.diag2 = computeCellDirectionValue(i, j, 4, state);
        return cellValue;
    }

    public Value getCellValue(int i, int j, MNKCellState state) {
        if (state == allyPlayer) {
            return allCells[i * N + j].allyValue;
        } else {
            return allCells[i * N + j].enemyValue;
        }
    }

    /**
     * Updated the value of all the cells touched by a positioning of a cell <code> state </code>
     * @param i, j the cell index
     * @param state
     */
    public void updateCellValue(int i, int j, MNKCellState state) {
        updateCellDirectionValue(i, j, 1, state);
        updateCellDirectionValue(i, j, 2, state);
        updateCellDirectionValue(i, j, 3, state);
        updateCellDirectionValue(i, j, 4, state);
    }

    private void updateCellDirectionValue(int i, int j, int lineCode, MNKCellState state) {
        int xAdd = getHorizontalAdder(lineCode);
        int yAdd = getVerticalAdder(lineCode);

        // TODO
        // le cose difficili da fare è gestire il nuovo valore migliore per il middle, che probabilmente
        // devi ancora andare a fare una scansione lineare
        // invece per tutte le altre celle di interesse basta cambiare un valore
        // esempio
        // metto una croce in 0,0 allora, prendiamo per esempio le direzioni orizzontali.
        // Mi basta togliere un 1 per l'orizzontale da una parte, mentre il centro lo devo aggiornare
        // Invece per il cerchio, ora non può più fare nessuna combo da questa parte, quindi dobbiamo settare un -1
        // nella direzione indicata e andare a ricalcolarci il middle.
        // Questa cosa credo abbia complessità K^2, perché devo farlo per O(K) nodi e nel peggiore dei casi vado
        // a fare una scansione lungo una direzione di K, ma se forse storiamo altri valori, probabilmente non avremo più bisogno di questo K in più
        // ma dovremo avere un array lungo K che stori il numero di cosi contenuti nella sliding window o simile... 
    }

    // private void updateFriendlyCellValue(int i, int j, int lineCode, MNKCellState friendCell) {
    //     allCells[i * N + j].allyValue = computeCellValue(i, j, state);
    // }
}
