package mnkgame.bigheuristics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class TestCell {
    @Test
    @DisplayName("Correctly equals two cells")
    public void testEquals() {
        Cell cell1 = new Cell(1, 1);
        Cell cell2 = new Cell(1, 1);
        assert cell1.equals(cell2);
    }

    @Test
    @DisplayName("The hash code of the same cell is the same")
    public void testHashCode() {
        Cell cell1 = new Cell(1, 1);
        Cell cell2 = new Cell(1, 1);
        assert cell1.hashCode() == cell2.hashCode();
    }

    @Test
    @DisplayName("Correctly returns the MNKCell for the cell")
    public void testGetCell() {
        Cell cell = new Cell(1, 1);
        mnkgame.MNKCell mnkCell = cell.getCell();
        assert mnkCell.i == 1;
        assert mnkCell.j == 1;
    }

    @Test
    @DisplayName("To string returns the two cells separated by a comma")
    public void testToString() {
        Cell cell = new Cell(1, 1);
        assert cell.toString().equals("1,1");
    }
}
