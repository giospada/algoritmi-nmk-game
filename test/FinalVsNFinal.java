package test;

import mnkgame.MNKCellState;

@SuppressWarnings("unused")
public class FinalVsNFinal {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        int times = 100000000;

        long start = System.nanoTime();
        for (int i = 0; i < times; i++) {
            Cell c = new Cell(0, 0);
        }
        long end = System.nanoTime();
        System.out.println("Time for new in non final: " + (end - start));

        long start1 = System.nanoTime();
        for (int i = 0; i < times; i++) {
            FinalCell c = new FinalCell(0, 0);
        }
        long end1 = System.nanoTime();
        System.out.println("Time for new in final: " + (end1 - start1));

        // -Xint to run without optimizations
        // java -Xint test.FinalVsNFinal  
        // Time for new in non final: 5379304700
        // Time for new in final: 4710420100

        // 2 time
        // Time for new in non final: 6692986500
        // Time for new in final: 6502971000

        // 3 time
        // Time for new in non final: 4142450800
        // Time for new in final: 4219253800

        // non sembra che ci sia tanta differenza
    }
}

class Cell {
    public int i;
    public int j;
    public MNKCellState state;


    Cell(int i, int j) {
        this.i = i;
        this.j = j;
        state = MNKCellState.FREE;
    }
}

class FinalCell {
    public final int i;
    public final int j;
    public final MNKCellState state;

    FinalCell(int i, int j) {
        this.i = i;
        this.j = j;
        state = MNKCellState.FREE;
    }
}