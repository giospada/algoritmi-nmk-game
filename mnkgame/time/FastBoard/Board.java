package mnkgame.time.FastBoard;

import java.lang.IllegalStateException;
import java.lang.IndexOutOfBoundsException;
import java.util.Collections;
import java.util.PriorityQueue;


import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import mnkgame.time.IBoard;
import mnkgame.time.IHeuristicCell;
import mnkgame.time.IValue;

public class Board implements IBoard {
    public final int M;
    public final int N;
    public final int K;

    /**
     * Rappresentazione della board in 2D 
     */
    private final HeuristicCell[][] B;

    /**
     * tutte le celle in allCells minori di freeCellsCount
     * devono soddisfare l'invariante che puntino al proprio index all'interno dell'array
     * mentre tutte le celle mosse (quelle maggiori o uguali) devonon puntare all'index
     * in cui devono tornare per ristabilire l'ordine
     */
    public final HeuristicCell[] allCells;
    public int freeCellsCount;

    /**
     * Rappresenta una sottoparte sortata secondo la compareTo di
     * Heuristic cells di tutte le celle
     */
    public final HeuristicCell[] sortedAllCells;
    

    private final MNKCellState[] Player = {MNKCellState.P1, MNKCellState.P2};
    private int currentPlayer; // currentPlayer plays next move
    private MNKGameState gameState; // game state

    private MNKCellState allyPlayer;  // alleato di sé stesso
    private MNKCellState enemyPlayer;
    
    private int sumEnemyHeuristic;  // utilizzata per dare un valore alla configurazione della board
    private int sumAllyHeuristic;

    private int branchingFactor = 40;

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
        this(M, N, K, playerCode, 50);
     }
     public Board(int M, int N, int K, MNKCellState playerCode,int maxBranchingFactor) throws IllegalArgumentException {
        if (M <= 0)
            throw new IllegalArgumentException("M cannot be smaller than 1");
        if (N <= 0)
            throw new IllegalArgumentException("N cannot be smaller than 1");
        if (K <= 0)
            throw new IllegalArgumentException("K cannot be smaller than 1");

        this.M = M;
        this.N = N;
        this.K = K;

        sumAllyHeuristic = 0;
        sumEnemyHeuristic = 0;

        B = new HeuristicCell[M][N];
        allCells = new HeuristicCell[M * N];
        sortedAllCells = new HeuristicCell[maxBranchingFactor];
        freeCellsCount = M * N;
        

        allyPlayer = playerCode;
        enemyPlayer = playerCode == MNKCellState.P1 ? MNKCellState.P2 : MNKCellState.P1;
        currentPlayer = 0;
        for(int i = 0; i < M; i++) {
            for(int j = 0; j < N; j++) {
                B[i][j] = new HeuristicCell(i, j, i * N + j);
                allCells[i*N + j] = B[i][j];
            }
        }
        
        // deve essere in for separato perché vuole prima avere una board inizializzata
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                initCellValue(i, j);
            }
        }
        updateCellDataStruct();  // sort sortedall cells

        gameState = MNKGameState.OPEN;
    }

    public void setBranchingFactor(int branchingFactor) {
        this.branchingFactor = branchingFactor;
    }

    // TODO: Migliorarlo 
    /**
     * Crea una copia delle celle disponibili e le sorta
     */
    public void updateCellDataStruct() {
        PriorityQueue<HeuristicCell> pq = new PriorityQueue<HeuristicCell>(Collections.reverseOrder()); 
        int len = Math.min(freeCellsCount, branchingFactor);
        for (int i = 0; i < freeCellsCount; i++) {
            if(pq.size()<len) {
                pq.add(allCells[i]);
            }else if(pq.peek().compareTo(allCells[i]) > 0) {
                pq.poll();
                pq.add(allCells[i]);
            }
        }
        int i = len - 1;
        while(!pq.isEmpty()){
            sortedAllCells[i] = pq.poll();
            i--;
        }
    }

    public IHeuristicCell getGreatKCell(int k) {
        if (k < 0 || k >= branchingFactor)
            return null;
        // TODO: migliorare questo perché non vorremmo che creasse semppre un nuovo oggetto
        // e lo distruggesse sul momento.
        return sortedAllCells[k];
    }

    public MNKGameState markCell(IHeuristicCell cell){
        return markCell(cell.getI(), cell.getJ());
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
        // checks non importanti ai fini algoritmici
        if (gameState != MNKGameState.OPEN) { throw new IllegalStateException("TimeBoard: Game ended!"); } else if (i < 0 || i >= M || j < 0 || j >= N) { throw new IndexOutOfBoundsException("TimeBoard:Indexes " + i + "," + j + " out of matrix bounds"); } else if (B[i][j].state != MNKCellState.FREE) { throw new IllegalStateException("TimeBoard:Cell " + i + "," + j + " is not free"); } B[i][j].state = Player[currentPlayer];


        // setta in modo che l'indice di quello in fondo da spostare sia coerente con l'invariante
        allCells[freeCellsCount - 1].index = B[i][j].index;  

        swapAllCellsByIndex(B[i][j].index, freeCellsCount - 1);
        freeCellsCount--;

        // TODO: invece di descrivere lo stato draw, come celle libere non rimanenti
        // si potrebbero usare il numero di sliding window che hanno ancora possibilità di vincere
        // così permette di stabilire il draw molte mosse prima.
        // Update game state
        if (Player[currentPlayer] == allyPlayer && B[i][j].allyValue.hasOneLeft()) {
            gameState = allyPlayer == MNKCellState.P1 ? MNKGameState.WINP1 : MNKGameState.WINP2;
        } else if (Player[currentPlayer] == enemyPlayer && B[i][j].enemyValue.hasOneLeft()) {
            gameState = allyPlayer == MNKCellState.P1 ? MNKGameState.WINP2 : MNKGameState.WINP1;
        } else if (freeCellsCount == 0) { 
            gameState = MNKGameState.DRAW;
        }

        B[i][j].state = Player[currentPlayer];
        // bisogna anche settare il valore invalido???
        // lo spostiamo tutto in cell Value update
        updateCellValue(i, j);
        updateCellDataStruct();
        // addAdjiacentCells(i, j, 1);

        // Arrays.sort()
        // TODO: decidere come sortare le celle in modo da riprenderle in modo effettivo
        
        currentPlayer = 1 - currentPlayer;

        return gameState;
    }

    public void addAdjiacentCells(int i, int j, int value) {
        for (int di = i - 1; di < i + 1; di++) {
            for (int dj = j - 1; dj < j + 1; dj++) {
                if (di >= 0 && di < M && dj >= 0 && dj < N && B[di][dj].state == MNKCellState.FREE) {
                    B[di][dj].addAdiacent(value);
                }
            }
        }
    }

    /**
     * se entrambi sono zero, significa che qualunque mossa faccia è sempre una mossa che porta a draw
     */
    public boolean isForcedDraw() {
        return sumAllyHeuristic + sumEnemyHeuristic == 0;
    }

    /**
     * Undoes last move
     *
     * @throws IllegalStateException If there is no move to undo
     */
    public void unmarkCell() throws IllegalStateException {
        if (freeCellsCount == M * N)
            throw new IllegalStateException("No move to undo");

        // freeCellsCount punta all'ultimo elemento moved
        HeuristicCell cell = allCells[freeCellsCount];
        
        // rollback della cella markata
        allCells[freeCellsCount].state = MNKCellState.FREE;
        int oldIndex = allCells[freeCellsCount].index;
        swapAllCellsByIndex(oldIndex, freeCellsCount);
        allCells[freeCellsCount].index = freeCellsCount; // punta ancora a oldindex
        freeCellsCount++;
        
        // rollback della board
        gameState = MNKGameState.OPEN;
        updateCellValue(cell.i, cell.j);
        updateCellDataStruct();
        // addAdjiacentCells(cell.i, cell.j, -1);
        currentPlayer = 1 - currentPlayer;
    }


    /**
     * This should be O(K)
     * 
     * @param lineCode 1 = horizontal, 2 = vertical, 3 = diagonal, 4 = anti-diagonal
     * @param state,   lo stato per cercare il valore (NON HA SENDO AVERE LO STATE
     *                 FREE)
     * @return INPLACE: the heuristics value for the current board for that cell.
     */
    public void computeCellDirectionValue(int i, int j, int lineCode, MNKCellState state) {
        DirectionValue dirValue = state == allyPlayer ? B[i][j].allyValue.directions[lineCode]
                : B[i][j].enemyValue.directions[lineCode];
        MNKCellState opponentState = state == allyPlayer ? enemyPlayer : allyPlayer;

        // calcola solo su celle vuote
        
        if (B[i][j].state == opponentState || B[i][j].state == state) {
            dirValue.setInvalidDirectionValue();
            dirValue.updateDirectionValue();
            return;
        }

        int jAdd = getHorizontalAdder(lineCode);
        int iAdd = getVerticalAdder(lineCode);
        dirValue.reset();

        int right = 1, left = 1;
        int numberOfOwnCells = 0;

        // ### Raggiungi la massima cella raggiungibile a destra, contando le celle
        // amiche.
        while (right < K) {
            int rightIidx = i + right * iAdd;
            int rightJidx = j + right * jAdd;
            if (!isValidCell(rightIidx, rightJidx) || B[rightIidx][rightJidx].state == opponentState) {
                break;
            }

            if (B[rightIidx][rightJidx].state == state) {
                numberOfOwnCells++;
                dirValue.numMyCells++;
            }
            right++;
        }
        if (right == K) { // raggiunta la grandezza per la prima sliding window
            dirValue.numSliding = 1;
            dirValue.numMaximumSliding = 1;

            // set first possible value for the center
            dirValue.center = K - numberOfOwnCells;
        }
        right--;  // torna al primo valido
        

        // ### scorrimento a sinistra con la sliding window
        while (left < K) {
            int leftIidx = i - left * iAdd;
            int leftJidx = j - left * jAdd;
            if (!isValidCell(leftIidx, leftJidx) || B[leftIidx][leftJidx].state == opponentState) {
                break;
            }

            if (B[leftIidx][leftJidx].state == state) {
                numberOfOwnCells++;
                dirValue.numMyCells++;
            }

            // Se ho già raggiunto la grandezza giusta per la window, mantienila.
            if (right + left == K) {
                if (B[i + right * iAdd][j + right * jAdd].state == state) {
                    numberOfOwnCells--;
                }
                right--;
            }

            // calcola il centro solo se la slinding window ha lunghezza già adeguata (K)
            if (right + left == K - 1) {
                int centerToFill = K - numberOfOwnCells;
                if (centerToFill < dirValue.center) {
                    dirValue.center = centerToFill;
                    dirValue.numMaximumSliding = 1;  // nuova sliding window con valore minore
                } else if (centerToFill == dirValue.center) {
                    dirValue.numMaximumSliding++;
                }
                
                dirValue.numSliding++;
            }
            left++;
        }

        if (dirValue.center == Integer.MAX_VALUE)
            dirValue.setInvalidDirectionValue();
            
        dirValue.updateDirectionValue();
    }

    public IHeuristicCell getIthCell(int i) {
        return allCells[i];
    }

    public int getValue(MNKCellState state) {
        // return sumAllyHeuristic + sumEnemyHeuristic;
        // proviamo così e vedo cosa succede

        if (state == allyPlayer) {
            return sumAllyHeuristic - sumEnemyHeuristic;
        } else {
            return sumEnemyHeuristic - sumAllyHeuristic;
        }
    }
    
    /**
     * Chiamato solo dal costruttore come primi valori
     */
    private void initCellValue(int i, int j) {
        for (int k = 0; k < 4; k++) {
            computeCellDirectionValue(i, j, k, allyPlayer);
            computeCellDirectionValue(i, j, k, enemyPlayer);
        }
        B[i][j].allyValue.updateValue();
        B[i][j].enemyValue.updateValue();
        sumAllyHeuristic += B[i][j].allyValue.getValue();
        sumEnemyHeuristic += B[i][j].enemyValue.getValue();
    }

    public IValue getCellValue(int i, int j, MNKCellState state) {
        if (state == allyPlayer) {
            return B[i][j].allyValue;
        } else {
            return B[i][j].enemyValue;
        }
    }

    /**
     * Updated the value of all the cells touched by a positioning of a cell
     * <code> state </code>
     * 
     * @param i,    j the cell index
     * @param state
     *              Runs in O(K^2)
     */
    public void updateCellValue(int i, int j) {
        sumAllyHeuristic -= B[i][j].allyValue.getValue();
        sumEnemyHeuristic -= B[i][j].enemyValue.getValue();
        for (int dir = 0; dir < 4; dir++){
            updateCellDirectionValue(i, j, dir);
        }

        B[i][j].allyValue.updateValue();
        B[i][j].enemyValue.updateValue();

        sumAllyHeuristic += B[i][j].allyValue.getValue();
        sumEnemyHeuristic += B[i][j].enemyValue.getValue();
    }

    private void updateCellDirectionValue(int i, int j, int dirCode) {
        int jAdd = getHorizontalAdder(dirCode);
        int iAdd = getVerticalAdder(dirCode);

        int slidingWindos[] = {0, 0};  // ally, enemy
        int numberOfOwnCells[] = {0, 0};  // ally, enemy

        int cI = i - iAdd *( K * 2 - 2) ,cJ = j - jAdd *( K * 2 - 2) ; 
        /// ci contiamo gli step
        // TODO: si può controllare più velocemente questo step con una formula matematica
        // semplicemente si fa il check a che punto la direzione si interseca con i bordi
        int step = 0 ;
        int stepTotali = K *4 -3 ; 
        int celleEsplorate = 0;
        for(; !isValidCell(cI, cJ); cJ += jAdd, cI += iAdd,step += 1) {}

        /**
         * Stora il numero di celle, che appartengono a me se sono positive e all'avversario se sono negative
         * se ho 0 non ho celle per nessuno dei due, se ho max ho invalido
         */
        QueueFixedInt numeroCelleSlidingWindow = new QueueFixedInt(K + 1);
        int numSlidingWindow[] = {0, 0};  // ally, enemy
        /*
            Devono tenersi il numero di celle (negative per il nemico, positive per l'alleato)
            deve tenersi l'ultimo index di queste celle
        */
        QueueFixedInt unltimoIndexDelleCelleContinue = new QueueFixedInt(K+1);
        QueueFixedInt numeroDiCelleContinue = new QueueFixedInt(K+1);

        QueueMaxFixed maxSlidingWindow [] = { new QueueMaxFixed(K + 1), new QueueMaxFixed(K + 1) };
        for(;!isValidCell(cI, cJ) && step < stepTotali; cJ += jAdd, cI += iAdd,step += 1, celleEsplorate++){
            // eliminazione della cella più vecchia
            if(celleEsplorate>K){
                int tI = cI - iAdd * K, tJ = cJ - jAdd * K;
                if(B[tI][tJ].state == allyPlayer){
                    numberOfOwnCells[0]--;
                }else if(B[tI][tJ].state == enemyPlayer){
                    numberOfOwnCells[1]--;
                }
            }
            //TODO aggiornare non è K ma è più grande
            if(numeroCelleSlidingWindow.getSize() == K){

                int first = numeroCelleSlidingWindow.getFirstEl();
                numeroCelleSlidingWindow.popFront();
                if(first == 0){ // valido per entrambi
                    if(maxSlidingWindow[0].getMax() == 0){
                        maxSlidingWindow[0].pop();
                    }
                    if(maxSlidingWindow[1].getMax() == 0){
                        maxSlidingWindow[1].pop();
                    }
                    numSlidingWindow[0]--;
                    numSlidingWindow[1]--;
                }else if(first == Integer.MAX_VALUE){  // invalido per entrambi
                    //scream      
                }else if(first < 0){  // ally
                    if(maxSlidingWindow[0].getMax() == first){
                        maxSlidingWindow[0].pop();
                    }
                    numSlidingWindow[0]--;
                }else if(first > 0){  // nemico
                    if(maxSlidingWindow[1].getMax() == -first){
                        maxSlidingWindow[1].pop();
                    }
                    numSlidingWindow[1]--;
                }
            }
            // fine eliminazione cella vecchia
            
            // aggiorno la cella attuale
            if(B[cI][cJ].state == allyPlayer){
                numberOfOwnCells[0]++;
            }else if(B[cI][cJ].state == enemyPlayer){
                numberOfOwnCells[1]++;
            }



            // se esiste una sliding windows
            if(celleEsplorate >= K){
                // guardiamo se la sliding window è valida per entrambi, uno dei due o per nessuno
                // a quel punto aggiungiamo il valore alla sliding window
                // se è zero va bene per entrambi
                // un numero positivo per il secondo player
                // un numero negativo per il primo player
                // intMax per nessuno dei due
                if(numberOfOwnCells[0] == 0 && numberOfOwnCells[1] == 0){
                    numeroCelleSlidingWindow.addBack(0);
                    maxSlidingWindow[0].add(0);
                    maxSlidingWindow[1].add(0);
                    numSlidingWindow[0]++;
                    numSlidingWindow[1]++;
                }else  if(numberOfOwnCells[0] == 0){
                    numeroCelleSlidingWindow.addBack(numberOfOwnCells[1]);
                    maxSlidingWindow[1].add(numberOfOwnCells[1]);
                    numSlidingWindow[1]++;
                }else  if(numberOfOwnCells[1] == 0){
                    numeroCelleSlidingWindow.addBack(-numberOfOwnCells[0]);
                    maxSlidingWindow[0].add(numberOfOwnCells[0]);
                    numSlidingWindow[0]++;
                }else{
                    numeroCelleSlidingWindow.addBack(Integer.MAX_VALUE);
                }
            }
            // ora dobbiamo aggiornare la cella al centro della sliding wind


            if(step>K){
                int tI = cI - iAdd * (K - 1), tJ = cJ - jAdd * (K - 1);
                if(B[tI][tJ].state == MNKCellState.FREE){
                    B[tI][tJ].allyValue.directions[dirCode].center = maxSlidingWindow[0].getMax();
                    B[tI][tJ].allyValue.directions[dirCode].numMaximumSliding = maxSlidingWindow[0].getMaxCount();
                    B[tI][tJ].allyValue.directions[dirCode].numSliding = numSlidingWindow[0];
                    B[tI][tJ].allyValue.directions[dirCode].numMyCells =
                    
                    
                    B[tI][tJ].enemyValue.directions[dirCode].center = maxSlidingWindow[1].getMax();
                    B[tI][tJ].enemyValue.directions[dirCode].numMaximumSliding = maxSlidingWindow[1].getMaxCount();
                    B[tI][tJ].enemyValue.directions[dirCode].numSliding = numSlidingWindow[1];
                }else{
                    B[tI][tJ].allyValue.directions[dirCode].setInvalidDirectionValue(); 
                    B[tI][tJ].enemyValue.directions[dirCode].setInvalidDirectionValue(); 
                }

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


    public void setCellState(int i, int j, MNKCellState state) {
        B[i][j].state = state;
    }

    public void setPlayer(MNKCellState player) {
        currentPlayer = player == MNKCellState.P1 ? 0 : 1;
    }

    private boolean isValidCell(int i, int j) {
        return i >= 0 && i < M && j >= 0 && j < N;
    }

    private void swapAllCellsByIndex(int i, int j) {
        HeuristicCell cell = allCells[i];
        allCells[i] = allCells[j];
        allCells[j] = cell;
    }

    /**
     * Old mics function
     */
    public int getHeuristic(int i, int j) {
        return B[i][j].allyValue.getValue();
    }

    /**
     * Returns the id of the player allowed to play next move.
     *
     * @return 0 (first player) or 1 (second player)
     */
    public int currentPlayer() {
        return currentPlayer;
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

    public void printHeuristics(boolean ally) {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                if (ally)
                    System.out.print(B[i][j].allyValue.getValue() + " \t");
                else
                    System.out.print(B[i][j].enemyValue.getValue() + " \t");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public int getFreeCellsCount() {
        return this.freeCellsCount;
    }
    public int getK() {
        return this.K;
    }

    public int getM() {
        return this.M;
    }
    
    public int getN() {
        return this.N;
    }
}
