package mnkgame.time.SmallBoard;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.time.IHeuristicCell;

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
public class HeuristicCell implements IHeuristicCell {
    public final int i;
    public final int j;
    public int index;  // l'index all'interno dell'array della board
    public Value allyValue;
    public Value enemyValue;
    public MNKCellState state;
    private int numAdiacent;

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
    
    public int getValueWithAdj() {
        return (allyValue.getValue() + enemyValue.getValue()) + numAdiacent * 5;
    }

    public void addAdiacent(int v) {
        numAdiacent += v;
    }

    public int compareTo(IHeuristicCell other){
        return other.getValue() - this.getValue();
    }

    public MNKCell toMNKCell() {
        return new MNKCell(i, j, state);
    }

    @Override
    public int getI() {
        return i;
    }

    @Override
    public int getJ() {
        return j;
    }
}