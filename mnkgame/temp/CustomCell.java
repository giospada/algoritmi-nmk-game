import java.nio.file.DirectoryIteratorException;

class CustomCell{
    private UFNode ufNodes[];
    private MNKCellState state;
    private Position position;

    CustomCell(Position pos){
        ufNodes=new UFNode[4];
        state=MNKCellState.FREE;
        position=pos;
        initUFNode();
    }

    private void initUFNode(){
        for(int i=0;i<4;i++){
            ufNodes[i]=new UFNode(this);
        }
    }

    public UFNode[] getUfNodes() {
        return ufNodes;
    }
    
    public Position getPosition() {
        return position;
    }
    public MNKCellState getState() {
        return state;
    }
    public void setState(MNKCellState state) {
        this.state = state;
    }
    public void setPosition(Position position) {
        this.position = position;
    }

    public void setUfNodes(UFNode[] ufNodes) {
        this.ufNodes = ufNodes;
    }
};