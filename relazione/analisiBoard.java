package relazione;

import mnkgame.CBoard;
import java.util.ArrayList;
import java.util.Collections;
// questo file si prefissa di fare l'analisi sperimentale alle due board
// CBoard per valutarne l'efficienza effettiva

public class analisiBoard {
    private static int numeroGiochi = 1000;
    private static MNKBoard mnkBoard;
    private static CBoard cBoard;
    private static boolean hasToRemove = false;
    private static final int K = 100;  // possederà un valore alto, in modo che il gioco non finisca mai
    private static final int[][] giochi = {
        {3, 3},
        {4, 3},
        {4, 4},
        {5, 4},
        {5, 5},
        {6, 4},
        {6, 5},
        {6, 6},
        {7, 4},
        {7, 5},
        {7, 6},
        {7, 7},
        {8, 8},
        {10, 10},
        {50, 50},
        {70, 70}
    };

    public static ArrayList<int[]> getAllCells(int m, int n) {
        ArrayList<int[]> cells = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                cells.add(new int[] {i, j});
            }
        }
        return cells;
    }

    public static void markCellMNKBoard(ArrayList<int[]> actions) {
        for (int[] action : actions) {
            mnkBoard.markCell(action[0], action[1]);
        }

        if (hasToRemove) {
            for (int i = 0; i < actions.size(); i++) {
                mnkBoard.unmarkCell();
            }
        }
    }

    public static void markCellCBoard(ArrayList<int[]> actions) {
        for (int[] action : actions) {
            cBoard.markCell(action[0], action[1]);
        }

        if (hasToRemove) {
            for (int i = 0; i < actions.size(); i++) {
                cBoard.unmarkCell();
            }
        }
    }

    public static void analize() {
        for (int[] giochio : giochi) {
            int m = giochio[0];
            int n = giochio[1];
            System.out.println("testing m = " + m + ", n = " + n);

            long timeMNK = 0;
            long timeC = 0;

            for (int i = 0; i < numeroGiochi; i++) {
                mnkBoard = new MNKBoard(m, n, K);
                cBoard = new CBoard(m, n, K);
                ArrayList<int[]> actions = getAllCells(m, n);
                Collections.shuffle(actions);
                long startMNK = System.nanoTime();
                markCellMNKBoard(actions);
                timeMNK += System.nanoTime() - startMNK;
                long startC = System.nanoTime();
                markCellCBoard(actions);
                timeC += System.nanoTime() - startC;
            }
            System.out.println("timeMNK = " + timeMNK / 1000000 + " ms");
            System.out.println("timeC = " + timeC  / 1000000 + " ms");
            System.out.println("timeMNK / timeC = " + (double)timeMNK / timeC + " ns");
        }
    }

    public static void main(String[] args) {
        hasToRemove = true;
        analize();
    }
}
