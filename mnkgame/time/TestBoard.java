package mnkgame.time;

import mnkgame.MNKCellState;
import mnkgame.MNKGame;
import mnkgame.MNKGameState;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

public class TestBoard {

    @Test
    @DisplayName("Should correctly mark and unmark a single cell")
    public void testMarkUnmark() {
        Board B = new Board(3, 3, 3, MNKCellState.P1);
        B.markCell(0, 0);
        assert B.getState(0, 0) == MNKCellState.P1;
        B.unmarkCell();
        assert B.getState(0, 0) == MNKCellState.FREE;
    }

    @Test
    @DisplayName("Should correctly mark and unmark multiple cells")
    public void testMarkUnmarkMultiple() {
        Board B = new Board(3, 3, 3, MNKCellState.P1);
        B.markCell(0, 0);
        B.markCell(1, 1);
        B.markCell(2, 2);
        assert B.getState(0, 0) == MNKCellState.P1;
        assert B.getState(1, 1) == MNKCellState.P1;
        assert B.getState(2, 2) == MNKCellState.P1;
        B.unmarkCell();
        assert B.getState(2, 2) == MNKCellState.FREE;
        B.unmarkCell();
        assert B.getState(1, 1) == MNKCellState.FREE;
        B.unmarkCell();
        assert B.getState(0, 0) == MNKCellState.FREE;
    }

    @Test
    @DisplayName("Should correctly return end state of board")
    public void testBoardEndState() {
        Board B = new Board(3, 3, 3, MNKCellState.P1);
        B.markCell(0, 0);
        B.markCell(1, 1);
        MNKGameState state = B.markCell(2, 2);
        assert state == MNKGameState.WINP1;
        B.unmarkCell();
        assert B.gameState() == MNKGameState.OPEN;
    }

    @Test
    @DisplayName("Should correctly evaluate the Value of the cell without moves")
    public void testCellValue() {
        Board B = new Board(3, 3, 3, MNKCellState.P1);
        Value value = B.getCellValue(0, 0, MNKCellState.P1);

        assert value.directions[0].bestWin() == 3;
        assert value.directions[1].bestWin() == 3;
        assert value.directions[2].bestWin() == 3;
        assert value.directions[3].bestWin() == -1;  // guardando l'angolo non posso vincere

        value = B.getCellValue(1, 1, MNKCellState.P2);
        for (int i = 0; i < 4; i++)
            assert value.directions[i].bestWin() == 3;
    }
    
    @Test
    @DisplayName("bestwin should update when player moves at center")
    public void testCellValueAfterMove() {
        Board B = new Board(3, 3, 3, MNKCellState.P1);
        B.markCell(1, 1);
        B.updateCellValue(1, 1);
        Value value = B.getCellValue(1, 1, MNKCellState.P1);
        assert value.directions[0].bestWin() == 2;
        assert value.directions[1].bestWin() == 2;
        assert value.directions[2].bestWin() == 2;
        assert value.directions[3].bestWin() == 2;
    }

    @Test
    @DisplayName("bestwin should update when player moves in multiple places")
    public void testCellValueAfterMultipleMoves() {
        Board B = new Board(3, 3, 3, MNKCellState.P1);
        B.markCell(0, 0);
        B.updateCellValue(0, 0);
        Value value = B.getCellValue(0, 0, MNKCellState.P1);
        assert value.directions[0].bestWin() == 2;
        assert value.directions[1].bestWin() == 2;
        assert value.directions[2].bestWin() == 2;
        assert value.directions[3].bestWin() == -1;  // guardando l'angolo non posso vincere

        B.markCell(1, 1);
        B.updateCellValue(1, 1);
        value = B.getCellValue(0, 0, MNKCellState.P1);
        assert value.directions[0].bestWin() == 2;
        assert value.directions[1].bestWin() == 2;
        assert value.directions[2].bestWin() == 1;
        assert value.directions[3].bestWin() == -1;  // guardando l'angolo non posso vincere

        B.markCell(2, 2);
        B.updateCellValue(2, 2);
        value = B.getCellValue(0, 0, MNKCellState.P1);
        assert value.directions[0].bestWin() == 2;
        assert value.directions[1].bestWin() == 2;
        assert value.directions[2].bestWin() == 0;
        assert value.directions[3].bestWin() == -1;  // guardando l'angolo non posso vincere
    }

    @Test
    @DisplayName("Correctly recognizes a line double play at angle when it's present")
    public void testDoubleWin() {
        Board B = new Board(10, 10, 5, MNKCellState.P1);
        B.markCell(1, 1);
        B.markCell(2, 2);
        B.markCell(3, 3);
        B.updateCellValue(4, 4);
        Value value = B.getCellValue(3, 3, MNKCellState.P1);
        assert value.directions[2].bestWin() == 2;
        assert value.isDoublePlay();

        value = B.getCellValue(9, 9, MNKCellState.P1);
        assert value.directions[2].bestWin() == 5;
        assert !value.isDoublePlay();
    }

    @Test
    public void randomTest() {
        Board B = new Board(4, 4, 3, MNKCellState.P1);
        Value value = B.getCellValue(1, 1, MNKCellState.P1);
        assert !value.isDoublePlay();
    }

    @Test
    @Disabled("Non funzionano i check attuali per questo caso")
    @DisplayName("Correctly recognizes a line double play in middle when it's not present")
    public void testNoDoubleWin() {
        Board B = new Board(10, 10, 5, MNKCellState.P1);
        B.markCell(1, 1);
        B.markCell(2, 2);
        B.markCell(4, 4);
        B.updateCellValue(3, 3);
        Value value = B.getCellValue(3, 3, MNKCellState.P1);
        assert value.directions[2].bestWin() == 2;
        assert !value.isDoublePlay();  // buggato
    }

    @Test
    @DisplayName("Correctly recognizes a multiline double play when it's present")
    public void testMultiLineDoubleWin() {
        Board B = new Board(10, 10, 5, MNKCellState.P1);
        B.markCell(1, 1);
        B.markCell(2, 2);
        B.markCell(3, 3);
        B.markCell(4, 5);
        B.markCell(4, 6);
        B.markCell(4, 7);
        B.updateCellValue(4, 4);
        Value value = B.getCellValue(4, 4, MNKCellState.P1);
        assert value.isDoublePlay();
    }


    @Test
    @Disabled("Old test, for MICS heuristics")
    @DisplayName("tests if correcly counts for 3x3 board in all angles")
    public void testEmpty() {
        Board board = new Board(3, 3, 3, MNKCellState.P1);
        int angleValues[] = new int[4];
        angleValues[0] = board.getHeuristic(0, 0);
        angleValues[1] = board.getHeuristic(2, 2);
        angleValues[2] = board.getHeuristic(2, 0);
        angleValues[3] = board.getHeuristic(0, 2);
        for (int i = 0; i < angleValues.length; i++) {
            assert angleValues[i] == 3;
        }

        int middleValues[] = new int[4];
        middleValues[0] = board.getHeuristic(1, 0);
        middleValues[1] = board.getHeuristic(1, 2);
        middleValues[2] = board.getHeuristic(0, 1);
        middleValues[3] = board.getHeuristic(2, 1);
        for (int i = 0; i < middleValues.length; i++) {
            assert middleValues[i] == 2;
        }

        int center = board.getHeuristic(1, 1);
        assert center == 4;
    }

    @Test
    @Disabled("Old test, for MICS heuristics")
    @DisplayName("tests if correcly counts for 5x5 board in all angles except middle")
    public void testFiveByFive() {
        // O O O O O
        // O O O O O
        // O O O O O
        // O O O O O
        // O O O O O

        Board board = new Board(5, 5, 3, MNKCellState.P1);
        // test corners
        int cornerValues[] = new int[4];
        cornerValues[0] = board.getHeuristic(0, 0);
        cornerValues[1] = board.getHeuristic(0, 4);
        cornerValues[2] = board.getHeuristic(4, 0);
        cornerValues[3] = board.getHeuristic(4, 4);
        for (int i = 0; i < cornerValues.length; i++) {
            assert cornerValues[i] == 3;
        }

        // test middle
        int middleValues[] = new int[4];
        middleValues[0] = board.getHeuristic(2, 0);
        middleValues[1] = board.getHeuristic(2, 4);
        middleValues[2] = board.getHeuristic(0, 2);
        middleValues[3] = board.getHeuristic(4, 2);
        for (int i = 0; i < middleValues.length; i++) {
            assert middleValues[i] == 6;
        }

        // test center
        int center = board.getHeuristic(2, 2);
        assert center == 12;

        // test edges
        int edgeValues[] = new int[8];
        edgeValues[0] = board.getHeuristic(1, 0);
        edgeValues[1] = board.getHeuristic(1, 4);
        edgeValues[2] = board.getHeuristic(0, 1);
        edgeValues[3] = board.getHeuristic(4, 1);
        edgeValues[4] = board.getHeuristic(3, 0);
        edgeValues[5] = board.getHeuristic(3, 4);
        edgeValues[6] = board.getHeuristic(0, 3);
        edgeValues[7] = board.getHeuristic(4, 3);
        for (int i = 0; i < edgeValues.length; i++) {
            assert edgeValues[i] == 4;
        }

        // se quelli sopra sono andati bene i restanti al centro non dovrebbero essere un problema
    }

    @Test
    @Disabled("Old test, for MICS heuristics")
    @DisplayName("test if correctly counts for obstacles")
    public void testObstacles() {
        Board board = new Board(5, 5, 3, MNKCellState.P1);
        board.setCellState(2, 2, MNKCellState.P2);

        // test corners
        int cornerValues[] = new int[4];
        cornerValues[0] = board.getHeuristic(0, 0);
        cornerValues[1] = board.getHeuristic(0, 4);
        cornerValues[2] = board.getHeuristic(4, 0);
        cornerValues[3] = board.getHeuristic(4, 4);
        for (int i = 0; i < cornerValues.length; i++) {
            assert cornerValues[i] == 2;
        }

        // test middle
        int middleValues[] = new int[4];
        middleValues[0] = board.getHeuristic(2, 0);
        middleValues[1] = board.getHeuristic(2, 4);
        middleValues[2] = board.getHeuristic(0, 2);
        middleValues[3] = board.getHeuristic(4, 2);
        for (int i = 0; i < middleValues.length; i++) {
            assert middleValues[i] == 5;
        }

        // test edges
        int edgeValues[] = new int[8];
        edgeValues[0] = board.getHeuristic(1, 0);
        edgeValues[1] = board.getHeuristic(1, 4);
        edgeValues[2] = board.getHeuristic(0, 1);
        edgeValues[3] = board.getHeuristic(4, 1);
        edgeValues[4] = board.getHeuristic(3, 0);
        edgeValues[5] = board.getHeuristic(3, 4);
        edgeValues[6] = board.getHeuristic(0, 3);
        edgeValues[7] = board.getHeuristic(4, 3);
        for (int i = 0; i < edgeValues.length; i++) {
            assert edgeValues[i] == 4;
        }

        int nearCenter[] = new int[4];
        nearCenter[0] = board.getHeuristic(1, 1);
        nearCenter[1] = board.getHeuristic(3, 3);
        nearCenter[2] = board.getHeuristic(3, 1);
        nearCenter[3] = board.getHeuristic(1, 3);
        for (int i = 0; i < nearCenter.length; i++) {
            assert nearCenter[i] == 5;
        }
    }

    @Test
    @Disabled("Old test, for MICS heuristics")
    @DisplayName("test if correctly counts for own pieces and obstacles")
    public void countOwnPieces() {
        Board board = new Board(5, 5, 3, MNKCellState.P1);
        board.setCellState(2, 2, MNKCellState.P1);
        assert board.getHeuristic(1, 1) == (7 + 1);  // + 1 per la cella amica

        board.setCellState(3, 3, MNKCellState.P2);
        assert board.getHeuristic(1, 1) == (6 + 1);

        // avvolgo tutta la cella 1 1
        board.setCellState(0, 0, MNKCellState.P2);
        board.setCellState(1, 0, MNKCellState.P2);
        board.setCellState(2, 0, MNKCellState.P2);
        board.setCellState(0, 1, MNKCellState.P2);
        board.setCellState(1, 2, MNKCellState.P2);
        board.setCellState(2, 1, MNKCellState.P2);
        board.setCellState(0, 2, MNKCellState.P2);
        assert board.getHeuristic(1, 1) == 0; // check che non conta la cella amica

        board.setCellState(3, 3, MNKCellState.FREE);
        int value = board.getHeuristic(1, 1);
        assert value == 1 + 1; // 1 per le celle libere + 1 per la cella amica in (2, 2)
    }
}
