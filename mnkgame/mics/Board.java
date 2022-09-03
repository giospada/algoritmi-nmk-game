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
                B[i][j].allyValue = new Value();
                B[i][j].enemyValue = new Value();
                computeCellValue(i, j );
            }
        }
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




    /**
     * This should be O(K)
     * @param lineCode 1 = horizontal, 2 = vertical, 3 = diagonal, 4 = anti-diagonal
     * @param state, lo stato per cercare il valore (NON HA SENDO AVERE LO STATE FREE)
     */
    public void computeCellDirectionValue(int i, int j, int lineCode) {
        DirectionValue dirValueAlly = B[i][j].allyValue.directions[lineCode];
        DirectionValue dirValueEnemy = B[i][j].enemyValue.directions[lineCode]

        int jAdd = getHorizontalAdder(lineCode);
        int iAdd = getVerticalAdder(lineCode);
        int iPos= i - iAdd * (K - 1);
        int jPos= j - jAdd * (K - 1);
        int enemyInWindow = 0;
        int allyInWindow = 0;
        for(int steps = 0; steps < K * 2 - 1; steps++){
            if(isValidCell(iPos, jPos)){
                if(B[iPos][jPos].state == enemyPlayer){
                    enemyInWindow++;
                }else if(B[iPos][jPos].state==allyPlayer){
                    allyInWindow++;
                }
                if(steps>=K){
                    //Gestisci la slidingWindows (quindi il fatto di andare a checkare K - 1 steps indietro per aggiornare
                    // quanto scorre )
                }
                // quando arrivi al centro cioè a K step aggiorna il left e 
                // quando arrivi a K*2 - 2  step  aggiorna il rightu

            }
            jPos+=jAdd;
            iPos+=iAdd;
        }


        
        // vai da destra a sinitra 
        // creare la sliding window verso una singola direzione (non ce ne frega se è invalida o meno)
        // iniziare a scorrerla aumentando di 1 e aggiornando i valori
        // if (cella destra è amico)
        //   decrementa conto amico
        //   decrementa conto nemico per nemico
        // else if cella destra è nemico
        // else non decremento niente
        // la sliding window è attiva quando al suo interno non ci sono celle dell'altro player, e quando i suoi 
        // estremi sono all'interno della board.

        // se la cella iniziale è già occupata da me
        // ho una cella in medo da dover riempire, quindi 0
        
        dirValue.center = Integer.MAX_VALUE;
        int [] output=computeNumberOfCellInADirection(i, j, lineCode, state);

        int right=output[0];
        int left= 1;
        dirValue.right+=output[1];
        int numberOfOwnCells=output[2];

        if (!isValidCell(i + right * yAdd, j + right * xAdd) || B[i + right * yAdd][j + right * xAdd].state == opponentState) {
            dirValue.rightIsFree = false;
        } else {
            dirValue.rightIsFree = true;
        }
        right--;

        // set the first possible value for the center
        if (dirValue.right != -1) dirValue.center = dirValue.right;

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
                if (centerToFill < dirValue.center) dirValue.center = centerToFill;
            }
            left++;
        }
        if (!isValidCell(i - left * yAdd, j - left * xAdd) || // fuori dalla board
            B[i - left * yAdd][j - left * xAdd].state == opponentState || 
            !isValidCell(i + yAdd, j + xAdd) || // fuori dalla board dall'altra parte
            B[i + yAdd][j + xAdd].state == opponentState) { // c'è un ostacolo anche dall'altra parte
            dirValue.leftIsFree = false;
        } else {
            dirValue.leftIsFree = true;
        }
        
        if (dirValue.center == Integer.MAX_VALUE) dirValue.center = -1;
    }
    

    public void computeCellValue(int i, int j) {
        for (int k = 0; k < 4; k++) 
            computeCellDirectionValue(i, j, k);
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
     * Runs in O(K^2)
     */
    public void updateCellValue(int i, int j) {
        for(int dir = 0; dir < 4; dir++)
            updateCellDirectionValue(i, j, dir);
    }

    private void updateCellDirectionValue(int i, int j, int dirCode) {
        int xAdd = getHorizontalAdder(dirCode);
        int yAdd = getVerticalAdder(dirCode);

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

        // attualmente è una versione lenta che aggiorna tutte le celle toccate
        int start = 0;
        while (start > -K && isValidCell(i + start * yAdd, j + start * xAdd)) {
            start--;
        }

        while (start < K && isValidCell(i + start * yAdd, j + start * xAdd)) {
            B[i][j].allyValue.directions[dirCode] = computeCellDirectionValue(i, j, dirCode, allyPlayer);
            B[i][j].enemyValue.directions[dirCode] = computeCellDirectionValue(i, j, dirCode, enemyPlayer);
            start++;
        }
    }

    // private void updateFriendlyCellValue(int i, int j, int lineCode, MNKCellState friendCell) {
    //     allCells[i * N + j].allyValue = computeCellValue(i, j, state);
    // }

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

    private int getHorizontalAdder(int dirCode) {
        return dirCode == 0 ? 1 : dirCode == 1 ? 0 : dirCode == 2 ? 1 : 1;
    }

    private int getVerticalAdder(int dirCode) {
        return dirCode == 0 ? 0 : dirCode == 1 ? 1 : dirCode == 2 ? 1 : -1;
    }

    /**
     * Returns the current state of the game.
     *
     * @return MNKGameState enumeration constant (OPEN,WINP1,WINP2,DRAW)
     */
    public MNKGameState gameState() {
        return gameState;
    }

    boolean isValidCell(int i, int j) {
        return i >= 0 && i < M && j >= 0 && j < N;
    }

    public void setCellState(int i, int j, MNKCellState state) {
        B[i][j].state = state;
    }

    public void setPlayer(MNKCellState player) {
        currentPlayer = player == MNKCellState.P1 ? 0 : 1;
    }

    private void swapAllCells(int i, int j) {
        HeuristicCell cell = allCells[i];
        allCells[i] = allCells[j];
        allCells[j] = cell;
    }


    /**
     * Returns the id of the player allowed to play next move.
     *
     * @return 0 (first player) or 1 (second player)
     */
    public int currentPlayer() {
        return currentPlayer;
    }
}
