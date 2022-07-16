package mnkgame;

class UnionHistoryRecord{
    private UFNode node1;
    private UFNode node2;

    UnionHistoryRecord( UFNode cell1,UFNode cell2) {
        this.node1 = cell1;
        this.node2 = cell2;
    }

    public UFNode getNode1() {
        return node1;
    }
    public UFNode getNode2() {
        return node2;
    }
}