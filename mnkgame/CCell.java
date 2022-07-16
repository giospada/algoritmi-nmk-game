package mnkgame;

import java.nio.file.DirectoryIteratorException;

class CCell{
    private UFNode ufNodes[];
    private MNKCellState state;
    private Position position;

    CCell(int x,int y){
        ufNodes=new UFNode[4];
        state=MNKCellState.FREE;
        position=new Position(x,y);
        initUFNode();
    }

    private void initUFNode(){
        for(int i=0;i<4;i++){
            ufNodes[i]=new UFNode();
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