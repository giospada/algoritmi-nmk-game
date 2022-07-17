package mnkgame;



import javax.naming.InitialContext;

class CBoard {
    // *alert*:"bisognerà riscrivere la classe linked list per supportarte il remove in O(1)"



    private final CCell[][] Board;
    protected CLinkedList<CNode<CCell>> MarkedCell;
    protected final CLinkedList<CCell> FreeCell; 
    private CLinkedList<CLinkedList<UnionHistoryRecord>> UnionHistory;
    protected final int M, N, K;
	private final MNKCellState[] Player = {MNKCellState.P1,MNKCellState.P2};
    private int currentPlayer;
    private Position direction[]={new Position(0,1),new Position(1,0),new Position(1,1),new Position(1,-1)};

    CBoard(int M, int N, int K) {
        this.M = M;
        this.N = N;
        this.K = K;
        
        MarkedCell = new CLinkedList<>();
        FreeCell = new CLinkedList<>();
        UnionHistory= new CLinkedList<>();
        Board= new CCell[M][N];
        init();
    }

    public void init(){
        currentPlayer=0;
        for(int i=0;i<M;i++){
            for(int j=0;j<N;j++){
                Board[i][j]=new CCell(i,j);
                FreeCell.push(Board[i][j]);
            }
        }
    }

    public boolean isInside(int i,int j){
        return i>=0 && i<M && j>=0 && j<N;
    }

	public MNKCellState cellState(int i, int j) throws IndexOutOfBoundsException {
	    if(!isInside(i,j))
			throw new IndexOutOfBoundsException("Indexes " + i + "," + j + " are out of matrix bounds");
		else
			return Board[i][j].getState();
	}

    public MNKGameState markCell(int i,int j){
        CNode<CCell> head=FreeCell.getHead();
        while(head!=null){
            Position pos=head.getData().getPosition();
            if(pos.getX()==i && pos.getY()==j){
                return markCell(head);
            }
            head=head.next;
        }
        return MNKGameState.OPEN;
    }
    public MNKGameState markCell(CNode<CCell> nodeCell){
        FreeCell.remove(nodeCell);
        MarkedCell.push(nodeCell);

        CCell cell=nodeCell.getData();

        cell.setState(Player[currentPlayer]);
        Position pos=cell.getPosition();
        MNKGameState gameState=MNKGameState.OPEN;         
        CLinkedList<UnionHistoryRecord> cellUnited=new CLinkedList<>();
        int scalar[]={1,-1};

        int directionIndex=0;
        for(Position position:direction){
            for(int i:scalar){
                int x=pos.getX()+position.getX()*i;
                int y=pos.getY()+position.getY()*i;
                if(isInside(x,y) && Board[x][y].getState()==cell.getState()){
                    UFNode node1=cell.getUfNodes()[directionIndex];
                    UFNode node2=Board[x][y].getUfNodes()[directionIndex];

                    UnionHistoryRecord ufhistory=UFNode.union(node1, node2);
                    cellUnited.push(ufhistory);

                    if(ufhistory.getNode1().getSize()>=K){
                        gameState=currentPlayer==0?MNKGameState.WINP1:MNKGameState.WINP2;
                   }
                } 
            }
            directionIndex++;
        }

        UnionHistory.push(cellUnited);
        currentPlayer=1-currentPlayer;
        return gameState;
    }




    // questo dovrebbe eseguire in O(1) per la uf rollback e l'insert e il remove
    // delle linked List dovrebbero essere sempre in O(1)

    public void unmarkCell(){
        CNode<CCell> nodeCell=MarkedCell.pop();
        FreeCell.reinsert(nodeCell);
        CCell cell=nodeCell.getData();
        cell.setState(MNKCellState.FREE);

        CLinkedList<UnionHistoryRecord> cellUnited=UnionHistory.pop();
        while(cellUnited.isEmpty()==false){
            UnionHistoryRecord record=cellUnited.pop();
            UFNode.rollback(record);
        }
        
        currentPlayer=1-currentPlayer;
    } 


    public CLinkedList<CCell> getFreeCell() {
        return FreeCell;
    }
}