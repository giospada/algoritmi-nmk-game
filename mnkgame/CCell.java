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

    public MNKCell toMNKCell(){
        return new MNKCell(position.getX(),position.getY(),state);
    }

    // getter and setter

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
    
    // java methods override

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CCell){
            CCell cell=(CCell)obj;
            return cell.getPosition().equals(position) && cell.getState()==state;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return position.getX()+70*position.getY();
    }
    @Override
    public String toString() {
        return "CCell{ position:"+position.toString()+" state:"+state+" }";
    }
   
};