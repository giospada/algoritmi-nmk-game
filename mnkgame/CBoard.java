package mnkgame;
class CBoard {

    private final CNode<CCell>[][]board;
    protected final CRemoveReinsertList<CCell> freeCell; 
    protected final CStack<CNode<CCell>> markedCell;
    private final CStack<CStack<UnionHistoryRecord>> unionHistory;
    protected final int M, N, K;
	private final MNKCellState[] player = {MNKCellState.P1,MNKCellState.P2};
    private int currentPlayer;
    private Position direction[] = {
        new Position(0,1),
        new Position(1,0),
        new Position(1,1),
        new Position(1,-1)
    };

    CBoard(int M, int N, int K) {
        this.M = M;
        this.N = N;
        this.K = K;
        
        markedCell = new CStack<>();
        freeCell=new CRemoveReinsertList<>();
        unionHistory = new CStack<>();
        board = (CNode<CCell>[][]) new CNode[M][N];
        currentPlayer = 0;
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                freeCell.pushHead(new CCell(i, j));
                board[i][j] = freeCell.getHead();
            }
        }
    }

    public boolean isInside(int i, int j) {
        return i >= 0 && i < M && j >= 0 && j < N;
    }

    public MNKCellState cellState(int i, int j) throws IndexOutOfBoundsException {
        if (!isInside(i, j))
            throw new IndexOutOfBoundsException("Indexes " + i + "," + j + " are out of matrix bounds");
        else
            return board[i][j].getData().getState();
    }
    public MNKGameState markCell(Position pos) {
        return markCell(pos.getX(),pos.getY());
    }

    public MNKGameState markCell(int i, int j) {
        return markCell(board[i][j]);
    }

    public MNKGameState updateUnionFindAndGameState(CCell cell) {
        MNKGameState gameState = MNKGameState.OPEN;
        CStack<UnionHistoryRecord> cellUnited = new CStack<>();
        int scalar[] = { 1, -1 };

        Position pos = cell.getPosition();
        int directionIndex = 0;
        for (Position position : direction) {
            for (int i : scalar) {
                int x = pos.getX() + position.getX() * i;
                int y = pos.getY() + position.getY() * i;
                if (isInside(x, y) && board[x][y].getData().getState() == cell.getState()) {
                    UFNode node1 = cell.getUfNodes()[directionIndex];
                    UFNode node2 = board[x][y].getData().getUfNodes()[directionIndex];

                    UnionHistoryRecord ufhistory = UFNode.union(node1, node2);
                    cellUnited.push(ufhistory);

                    if (ufhistory.getNode1().getSize() >= K) {
                        gameState = currentPlayer == 0 ? MNKGameState.WINP1 : MNKGameState.WINP2;
                    }
                }
            }
            directionIndex++;
        }
        unionHistory.push(cellUnited);
        return gameState;
    }

    public MNKGameState markCell(CNode<CCell> nodeCell) {
        freeCell.remove(nodeCell);
        markedCell.push(nodeCell);

        CCell cell = nodeCell.getData();
        cell.setState(player[currentPlayer]);
        MNKGameState gameState = updateUnionFindAndGameState(cell);
        checkCorrectness();
        currentPlayer = 1 - currentPlayer;
        return gameState;
    }


    // questo dovrebbe eseguire in O(1) per la uf rollback e l'insert e il remove
    // delle linked List dovrebbero essere sempre in O(1)

    public void unmarkCell() {
        CNode<CCell> nodeCell = markedCell.pop();
        freeCell.reinsert(nodeCell);
        CCell cell = nodeCell.getData();
        cell.setState(MNKCellState.FREE);

        CStack<UnionHistoryRecord> cellUnited = unionHistory.pop();
        while (cellUnited.isEmpty() == false) {
            UnionHistoryRecord record = cellUnited.pop();
            UFNode.rollback(record);
        }

        currentPlayer = 1 - currentPlayer;
    }

    public CRemoveReinsertList<CCell> getFreeCell() {
        return freeCell;
    }

    private void  checkCorrectness(){
        boolean[][] visited = new boolean[M][N];
        for(int i=0;i<M;i++) for(int j=0;j<N;j++) visited[i][j]=false;
        for(CCell c:freeCell){
            visited[c.getPosition().getX()][c.getPosition().getY()]=true;
        }
        for(CNode<CCell> t:markedCell){
            CCell c=t.getData();
            visited[c.getPosition().getX()][c.getPosition().getY()]=true;
        }
        for(int i=0;i<M;i++) for(int j=0;j<N;j++) if(!visited[i][j]) throw new RuntimeException("Errore");
    }
}