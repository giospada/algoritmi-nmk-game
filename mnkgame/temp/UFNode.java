class UFNode{
    private int rank;
    private int size;
    private CustomCell customcell;
    
    UFNode(CustomCell customcell){
        this.customcell=customcell;
        rank=0;
        size=1;
    }

    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    
    public CustomCell getCustomcell() {
        return customcell;
    }
    public void setCustomcell(CustomCell customcell) {
        this.customcell = customcell;
    }
}