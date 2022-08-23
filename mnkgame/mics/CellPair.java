package mnkgame.mics;

import mnkgame.MNKCell;

public class CellPair implements Comparable<CellPair> {
    public int key;
    public MNKCell cell;
    
    public CellPair(int key, MNKCell cell) {
        this.key = key;
        this.cell = cell;
    }

    @Override
    public int compareTo(CellPair o) {
        return o.key - key; // descending order
    }
}
