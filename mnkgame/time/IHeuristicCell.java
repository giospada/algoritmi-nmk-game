package mnkgame.time;

import mnkgame.MNKCell;

public interface IHeuristicCell extends Comparable<IHeuristicCell> {
    public int getI();

    public int getJ();

    public int getValue();
    
    public MNKCell toMNKCell();
}