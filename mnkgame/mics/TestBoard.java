package mnkgame.mics;

import mnkgame.MNKCellState;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class TestBoard {

    @Test
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

    // 
    @Test
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
