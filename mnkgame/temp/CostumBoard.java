import java.util.LinkedList;

import javax.naming.InitialContext;

class CustomBoard {
    // *alert*:"bisognerà riscrivere la classe linked list per supportarte il remove in O(1)"

    private final CustomCell[][] Board;
    protected final CustomLinkedList<CustomCell> MarkedCell;
    protected final CustomLinkedList<CustomCell> FreeCell; 
    private CustomLinkedList<CustomLinkedList<Pair<CustomCell, CustomCell>>> UniteCell;
    protected final int M, N, K;
	private final MNKCellState[] Player = {MNKCellState.P1,MNKCellState.P2};
    private int currentPlayer;
    private Position direction[]={new Position(0,1),new Position(1,0),new Position(1,1),new Position(1,-1)};

    CustomBoard(int M, int N, int K) {
        this.M = M;
        this.N = N;
        this.K = K;
        
        MarkedCell = new CustomLinkedList<>();
        FreeCell = new CustomLinkedList<>();
        UniteCell= new CustomLinkedList<>();
        Board= new CustomCell[M][N];
        init();
    }

    public void init(){
        current player=0;
        for(int i=0;i<M;i++){
            for(int j=0;j<N;j++){
                Board[i][j]=new CustomCell(i,j);
                FreeCell.push(Board[i][j]);
            }
        }
    }
    public isInside(int i,int j){
        return i>=0 && i<M && j>=0 && j<N;
    }
	public MNKCellState cellState(int i, int j) throws IndexOutOfBoundsException {
	    if(!isInside(i,j))
			throw new IndexOutOfBoundsException("Indexes " + i + "," + j + " are out of matrix bounds");
		else
			return Board[i][j].getState();
	}

    //questo metodo dovrebbe dare il risulato in O(log (k)) per la union find
    //l'insert e il remove delle linked list dovrebbe essere in O(1)
    // *il remove non lo è ancora*
    public MNKGameState markCell(CustomLinkedListNode<CustomCell> cell){
        FreeCell.remove(cell);
        MarkedCell.push(cell.getData());
        cell.getData().setState(Player[currentPlayer]);
        Position pos=cell.getData().getPosition();
        MNKGameState GameState=MNKGameState.OPEN;         
        CustomLinkedList<Pair<CustomCell, CustomCell>> uniteCell=new CustomLinkedList<>();
        for(Position position:direction){
            for(int i=1;i<=-1;i-=2){
                int x=pos.getX()+position.getX()*i;
                int y=pos.getY()+position.getY()*i;
                if(isInside(x,y) && Board[x][y].getState()==cell.getData().getState()){
                    if(union(cell.getData(), Board[x][y], direction,uniteCell)>=k){
                        GameState=currentPlayer==0?MNKGameState.WINP1:MNKGameState.WINP2;
                   }
                } 
            }
        }
        UniteCell.push(uniteCell);
        currentPlayer=1-currentPlayer;
        return GameState;
    }
    private int union(CustomCell cell1,CustomCell cell2,int direction,CustomLinkedList<Pair<CustomCell, CustomCell>> uniteCell){
        // è impossibile che siano nella stessa union perchè una cell1 ha appena modificato il suo stato
        cell1=findHead(cell1, direction);
        cell2=findHead(cell2, direction);
        UFNode node1=cell1.getUfNodes()[direction];
        UFNode node2=cell2.getUfNodes()[direction];
        if(node1.getRank()<node2.getRank()){
            swap(node1,node2);
        }
        node2.setParent(node1);
        node1.setRank(max(node1.getRank(),node2.getRank()+1));
        node1.setSize(node1.getSize()+node2.getSize());
        uniteCell.push(new Pair<>(node1,ccell2));
        return node1.getSize();
    }

    private CustomCell findHead(CustomCell cell,int direction){
        while(cell.getDirections()[direction].getHead()!=cell){
            cell=cell.getDirections()[direction].getHead();
        }
        return cell;
    }

    private void undoUnion(CustomCell cell1,CustomCell cell2,int direction){

    }

    // questo dovrebbe eseguire in O(1) per la uf rollback e l'insert e il remove
    // delle linked List dovrebbero essere sempre in O(1)

    public void unmarkCell(){
        CustomCell cell=MarkedCell.getHead();
        MarkedCell.pop();
        FreeCell.push(cell);

        cell.setState(MNKCellState.FREE);
        currentPlayer=1-currentPlayer;
    } 
    public CustomLinkedList<CustomCell> getMarkedCell() {
        return MarkedCell;
    }
    public CustomLinkedList<CustomCell> getFreeCell() {
        return FreeCell;
    }
}