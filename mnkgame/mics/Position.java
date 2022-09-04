/**
 * Non bene perché ho fatto questa cosa, è abbastanz ainutile
 */

// package mnkgame.mics;

// public class Position {
//     private int i;
//     private int j;

//     private final int iAdd;
//     private final int jAdd;

//     private int getHorizontalAdder(int dirCode) {
//         return dirCode == 0 ? 1 : dirCode == 1 ? 0 : dirCode == 2 ? 1 : 1;
//     }

//     private int getVerticalAdder(int dirCode) {
//         return dirCode == 0 ? 0 : dirCode == 1 ? 1 : dirCode == 2 ? 1 : -1;
//     }

//     Position(int dirCode) {
//         i = 0;
//         j = 0;
//         iAdd = getVerticalAdder(dirCode);
//         jAdd = getHorizontalAdder(dirCode);
//     }

//     public void add(int steps) {
//         i += steps * iAdd;
//         j += steps * jAdd;
//     }

//     public HeuristicCell getCellAtOffSet(Board board, int offset) {
//         HeuristicCell cell = null;
//         if (board.isValidCell(i + offset * iAdd, j + offset * jAdd))
//             cell = board.getCell(i + offset * iAdd, j + offset * jAdd);
//         return cell;
//     }

//     @Override
//     public String toString() {
//         return "Position{ I:" + i + " J:" + j + " }";
//     }
// }
