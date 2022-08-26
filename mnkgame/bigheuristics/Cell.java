package mnkgame.bigheuristics;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;

public class Cell {
    public int i;
    public int j;

    Cell(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public Cell(Cell that) {
        this(that.i, that.j);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return "[" + this.i + "," + this.j + "]";
    }

    public MNKCell getCell() {
        return new MNKCell(this.i, this.j, MNKCellState.FREE);
    }
}
