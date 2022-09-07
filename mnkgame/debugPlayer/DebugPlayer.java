package mnkgame.debugPlayer;

import mnkgame.MNKCell;

public class DebugPlayer implements mnkgame.MNKPlayer {

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        // New random seed for each game
    }

    /**
     * Selects a random cell in <code>FC</code>
     */
    MNKCell moves[] = {
            new MNKCell(2, 0), 
            new MNKCell(2, 2), 
            new MNKCell(0, 2), 
            new MNKCell(2, 1),
    };

    int i = 0;


    public MNKCell next() {
        return moves[i++];
    }

    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        if (i >= moves.length) {
            return freeCells[(int) Math.random() * freeCells.length];
        }
        return next();
    }

    public String playerName() {
        return "Debug Player";
    }

}