package mnkgame.montecarlo;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;

// IDEA che deve essere valutata:
// se invece di contare quanti pezzi hai,
// ti tieni qualcosa per vedere il numero minimo di mosse per vincere in una direzione?
// Questo serve?
// eg 
// E E X
// E O E
// X E O
// la cella (0, 0) ha distanza di vittoria 2 pezzi, e ha due allineamenti possibili.
// si può fare questa cosa per ogni verso delle 4 direzioni possibili e credo sia semplice
// se mmai ci metti anche un caso in cui serve per vincere uno in mezzo, e lo consideri separato
// non va?
class HeuristicCell implements Comparable<HeuristicCell> {
    public final int i;
    public final int j;
    public int index;  // l'index all'interno dell'array della board
    public Value allyValue;
    public Value enemyValue;
    public MNKCellState state;

    public HeuristicCell(int i, int j, int index) {
        this.i = i;
        this.j = j;
        this.index = index;
        allyValue = new Value();
        enemyValue = new Value();
        state = MNKCellState.FREE;
    }
    
    public int getValue() {
        return allyValue.getValue() + enemyValue.getValue();
    }

    public int compareTo(HeuristicCell other){
        return other.getValue() - this.getValue();
        // TODO!
        // if (allyValue.equals(other.allyValue)) {
        //     return allyValue.compareTo(other.allyValue);
        // } else {
        //     return enemyValue.compareTo(other.enemyValue);
        // }
        // return heuristicValue - other.heuristicValue;  // ordine crescente
    }

    public MNKCell toMNKCell() {
        return new MNKCell(i, j, state);
    }
}