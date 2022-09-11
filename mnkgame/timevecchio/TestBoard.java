package mnkgame.timevecchio;

import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import mnkgame.timevecchio.SmallBoard.Board;
import mnkgame.timevecchio.SmallBoard.HeuristicCell;
import mnkgame.timevecchio.SmallBoard.Value;

import org.junit.jupiter.api.*;

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
        B.setPlayer(MNKCellState.P1);
        B.markCell(1, 1);
        B.setPlayer(MNKCellState.P1);
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
        B.setPlayer(MNKCellState.P1);
        B.markCell(1, 1);
        B.setPlayer(MNKCellState.P1);
        MNKGameState state = B.markCell(2, 2);
        assert state == MNKGameState.WINP1;
        B.unmarkCell();
        assert B.gameState() == MNKGameState.OPEN;
    }

    @Nested
    @DisplayName("Tests about the value and direction value")
    class ValueAndDirectionValue {
        @Test
        @DisplayName("Should correctly evaluate the Value of the cell without moves")
        public void testCellValue() {
            Board B = new Board(3, 3, 3, MNKCellState.P1);
            Value value = B.getCellValue(0, 0, MNKCellState.P1);
            System.out.println(value.directions[0].minStepsToWin());
            System.out.flush();
            assert value.directions[0].minStepsToWin() == 3;
            assert value.directions[1].minStepsToWin() == 3;
            assert value.directions[2].minStepsToWin() == 3;
            assert value.directions[3].minStepsToWin() == -1;  // guardando l'angolo non posso vincere

            value = B.getCellValue(1, 1, MNKCellState.P2);
            for (int i = 0; i < 4; i++)
                assert value.directions[i].minStepsToWin() == 3;
        }
        
        @Test
        @DisplayName("minStepsToWin should update when player moves at center")
        public void testCellValueAfterMove() {
            Board B = new Board(3, 3, 3, MNKCellState.P1);
            B.markCell(1, 1);
            B.updateCellValue(1, 1);
            Value value = B.getCellValue(0, 0, MNKCellState.P1);
            assert value.directions[0].minStepsToWin() == 3;
            assert value.directions[1].minStepsToWin() == 3;
            assert value.directions[2].minStepsToWin() == 2;
            assert value.directions[3].minStepsToWin() == -1;
        }

        @Test
        @DisplayName("minStepsToWin should update when player moves in multiple places")
        public void testCellValueAfterMultipleMoves() {
            Board B = new Board(3, 3, 3, MNKCellState.P1);
            B.markCell(0, 0);
            B.updateCellValue(0, 0);
            Value value = B.getCellValue(1, 1, MNKCellState.P1);
            assert value.directions[0].minStepsToWin() == 3;
            assert value.directions[1].minStepsToWin() == 3;
            assert value.directions[2].minStepsToWin() == 2;
            assert value.directions[3].minStepsToWin() == 3;  // guardando l'angolo non posso vincere

            B.setPlayer(MNKCellState.P1);
            B.markCell(1, 1);
            B.updateCellValue(1, 1);
            value = B.getCellValue(2, 2, MNKCellState.P1);
            assert value.directions[0].minStepsToWin() == 3;
            assert value.directions[1].minStepsToWin() == 3;
            assert value.directions[2].minStepsToWin() == 1;
            assert value.directions[3].minStepsToWin() == -1;  // guardando l'angolo non posso vincere

            B.setPlayer(MNKCellState.P1);
            MNKGameState state = B.markCell(2, 2);
            assert state == MNKGameState.WINP1;
        }

        @Test
        @DisplayName("Correctly updates the center")
        public void testCellValueAfterCenterMoves() {
            Board B = new Board(3, 3, 3, MNKCellState.P1);
            B.markCell(2, 0);
            B.setPlayer(MNKCellState.P1);
            B.markCell(2, 2);
            Value value = B.getCellValue(2, 1, MNKCellState.P1);
            assert value.directions[0].minStepsToWin() == 1;
        }
    }

    @Nested
    @DisplayName("Tests about the double plays")
    class DoublePlays {

        @Test
        @DisplayName("Correctly recognizes a line double play at angle when it's present")
        public void testDoubleWin() {
            // E E E E E ...
            // E X E E e ...
            // E E X E e ...
            // E E E X e ...
            // E E E E T ...
            // ...
            Board B = new Board(10, 10, 5, MNKCellState.P1);
            B.markCell(1, 1);
            B.setPlayer(MNKCellState.P1);
            B.markCell(2, 2);
            B.setPlayer(MNKCellState.P1);
            B.markCell(3, 3);
            B.updateCellValue(4, 4);
            Value value = B.getCellValue(4, 4, MNKCellState.P1);
            assert value.directions[2].minStepsToWin() == 2;
            assert value.directions[2].isInLineDoublePlay();
            assert value.isDoublePlay();

            value = B.getCellValue(9, 9, MNKCellState.P1);
            assert value.directions[2].minStepsToWin() == 5;
            assert !value.isDoublePlay();
        }

        @Test 
        @DisplayName("Correctly recognizes a not double play in line when it's blocked")
        public void notInlineDoublePlay() {
            // O E E E E ...
            // E X E E e ...
            // E E X E e ...
            // E E E X e ...
            // E E E E T ...
            // ...
            Board B = new Board(10, 10, 5, MNKCellState.P1);
            B.markCell(1, 1);
            B.setPlayer(MNKCellState.P1);
            B.markCell(2, 2);
            B.setPlayer(MNKCellState.P1);
            B.markCell(3, 3);

            B.setPlayer(MNKCellState.P2);
            B.markCell(0, 0);
            B.updateCellValue(4, 4);
            Value value = B.getCellValue(4, 4, MNKCellState.P1);
            assert value.directions[2].minStepsToWin() == 2;
            assert !value.directions[2].isInLineDoublePlay();
            assert !value.isDoublePlay();
        }

        @Test
        public void randomTest() {
            Board B = new Board(4, 4, 3, MNKCellState.P1);
            Value value = B.getCellValue(1, 1, MNKCellState.P1);
            assert !value.isDoublePlay();
        }

        @Test
        @DisplayName("Correctly recognizes a line double play in middle when it's not present")
        public void testNoDoubleWin() {
            // E E E E E ...
            // E X E E e ...
            // E E X E e ...
            // E E E T e ...
            // E E E E X ...
            // ...
            Board B = new Board(10, 10, 5, MNKCellState.P1);
            B.markCell(1, 1);
            B.setPlayer(MNKCellState.P1);
            B.markCell(2, 2);
            B.setPlayer(MNKCellState.P1);
            B.markCell(4, 4);
            B.updateCellValue(3, 3);
            Value value = B.getCellValue(3, 3, MNKCellState.P1);
            assert value.directions[2].minStepsToWin() == 2;
            assert !value.directions[0].isInLineDoublePlay();
            assert value.directions[2].isInLineDoublePlay();
            assert value.isDoublePlay();  // buggato
        }

        @Test
        @DisplayName("Correctly recognizes a multiline double play when it's present")
        public void testMultiLineDoubleWin() {
            Board B = new Board(10, 10, 5, MNKCellState.P1);
            B.markCell(1, 1);
            B.setPlayer(MNKCellState.P1);
            B.markCell(2, 2);
            B.setPlayer(MNKCellState.P1);
            B.markCell(3, 3);
            B.setPlayer(MNKCellState.P1);
            B.markCell(4, 5);
            B.setPlayer(MNKCellState.P1);
            B.markCell(4, 6);
            B.setPlayer(MNKCellState.P1);
            B.markCell(4, 7);
            B.updateCellValue(4, 4);
            Value value = B.getCellValue(4, 4, MNKCellState.P1);
            assert value.isDoublePlay();
        }
    }

    @Nested
    @DisplayName("tests about the mics heuristics functions")
    class MicsHeuristicTest {
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
                System.out.println(angleValues[i]);
                System.out.flush();
                assert angleValues[i] == 3;
            }

            int middleValues[] = new int[4];
            middleValues[0] = board.getHeuristic(1, 0);
            middleValues[1] = board.getHeuristic(1, 2);
            middleValues[2] = board.getHeuristic(0, 1);
            middleValues[3] = board.getHeuristic(2, 1);
            for (int i = 0; i < middleValues.length; i++) {
                System.out.println(middleValues[i]);
                System.out.flush();
                assert middleValues[i] == 2;
            }

            int center = board.getHeuristic(1, 1);
            assert center == 4;
        }

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
            board.updateCellValue(2, 2);

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
            Board board = new Board(5, 5, 4, MNKCellState.P1);
            board.setPlayer(MNKCellState.P1);
            board.markCell(2, 2);
            assert board.getHeuristic(1, 1) == (6 + 1);  // + 1 per la cella amica

            board.setPlayer(MNKCellState.P2);
            board.markCell(4, 4);  
            assert board.getHeuristic(1, 1) == (6);


            // avvolgo tutta la cella 1 1
            board.setPlayer(MNKCellState.P2);
            board.markCell(0, 0);
            board.setPlayer(MNKCellState.P2);
            board.markCell(1, 0);
            board.setPlayer(MNKCellState.P2);
            board.markCell(2, 0);
            board.setPlayer(MNKCellState.P2);
            board.markCell(0, 1);
            board.setPlayer(MNKCellState.P2);
            board.markCell(1, 2);
            board.setPlayer(MNKCellState.P2);
            board.markCell(2, 1);
            board.setPlayer(MNKCellState.P2);
            board.markCell(0, 2);
            assert board.getHeuristic(1, 1) == 0; // check che non conta la cella amica

            board.setCellState(3, 3, MNKCellState.FREE);
            board.setCellState(4, 4, MNKCellState.FREE);

            board.updateCellValue(1, 1);
            int value = board.getHeuristic(1, 1);
            assert value == 1 + 1; // 1 per le celle libere + 1 per la cella amica in (2, 2)
        }
    }  // nested test class


    @Nested
    @DisplayName("tests for all cells and sorted cells")
    class AllCells {
        @Test
        @DisplayName("test if a used move is not present in sortedCells")
        public void TestMoveIsUsed() {
            Board B = new Board(3, 3, 3, MNKCellState.P1);
            B.markCell(0, 0);

            for (int i = 0; i < B.freeCellsCount; i++) {
                HeuristicCell cell = B.getIthCell(i);
                assert cell.i != 0 || cell.j != 0;
            }

            for (int i = 0; i < B.freeCellsCount; i++) {
                HeuristicCell cell = B.getGreatKCell(i);
                assert cell.i != 0 || cell.j != 0;
            }
        }

        @Test
        @DisplayName("unmarked move is present in sortedCells and allcells")
        public void TestMoveIsUnmarked() {
            Board B = new Board(3, 3, 3, MNKCellState.P1);
            B.markCell(0, 0);
            B.unmarkCell();

            boolean isPresentInAllCells = false;
            boolean isPresentInSortedCells = false;

            for (int i = 0; i < B.freeCellsCount; i++) {
                HeuristicCell cell = B.getIthCell(i);
                if (cell.i == 0 && cell.j == 0) {
                    isPresentInAllCells = true;
                }
            }

            for (int i = 0; i < B.freeCellsCount; i++) {
                HeuristicCell cell = B.getGreatKCell(i);
                if (cell.i == 0 && cell.j == 0) {
                    isPresentInSortedCells = true;
                }                
            }

            assert isPresentInAllCells;
            assert isPresentInSortedCells;
        }

        @Test
        @DisplayName("test if all cells are present in allcells, and sortedcells")
        public void allCellsPresentOnInit() {
            Board B = new Board(3, 3, 3, MNKCellState.P1);

            boolean board1[][] = new boolean[3][3];
            boolean board2[][] = new boolean[3][3];

            for (int i = 0; i < B.freeCellsCount; i++) {
                HeuristicCell cell = B.getIthCell(i);
                board1[cell.i][cell.j] = true;
            }

            for (int i = 0; i < B.freeCellsCount; i++) {
                HeuristicCell cell = B.getGreatKCell(i);
                board2[cell.i][cell.j] = true;
            }

            for (int i = 0; i < 3; i++) { 
                for (int j = 0; j < 3; j++) { 
                    assert board1[i][j] == true;
                    assert board2[i][j] == true;
                }
            }
        }
    }
}