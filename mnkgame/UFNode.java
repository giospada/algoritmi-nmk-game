package mnkgame;

class UFNode {
    private int rank;
    private int size;
    private UFNode ufNode;

    UFNode() {
        this.ufNode = null;
        rank = 0;
        size = 1;
    }
    // O(log k)
    public UFNode findHead() {
        UFNode node = this;
        while (node.getUfNode() != null) {
            node = node.getUfNode();
        }
        return node;
    }
    // tempo costante
    static public UnionHistoryRecord union(UFNode node1, UFNode node2) {
        node1 = node1.findHead();
        node2 = node2.findHead();
        if (node1.getRank() < node2.getRank()) {
            UFNode temp = node1;
            node1 = node2;
            node2 = temp;
        }
        node2.setUfNode(node1);
        if (node1.getRank() == node2.getRank()) {
            node1.setRank(node1.getRank() + 1);
        }
        node1.setSize(node1.getSize() + node2.getSize());
        return (new UnionHistoryRecord(node1, node2));
    }
    // tempo costatnte
    static public void rollback(UnionHistoryRecord unionHistory) {
        UFNode node1 = unionHistory.getNode1();
        UFNode node2 = unionHistory.getNode2();
        node1.setSize(node1.getSize() - node2.getSize());
        node2.setUfNode(null);
        if (node1.getRank() == node2.getRank() + 1) {
            node1.setRank(node1.getRank() - 1);
        }
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
    public UFNode getUfNode() {
        return ufNode;
    }
    public void setUfNode(UFNode ufNode) {
        this.ufNode = ufNode;
        if (ufNode == this) {
            throw new IllegalArgumentException("ufNode cannot be itself");
        }
    }
}