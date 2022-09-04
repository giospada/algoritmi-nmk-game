package mnkgame.time;

/**
 * Questa classe è un modo per rappresentare il valore di una cella
 * in una board di gioco.
 * 
 * I valori principali sono i seguenti:
 * Per ogni direzione salvo:
 * - il numero di celle da riempire prima di vincere a destra, a sinistra, e al centro
 */
public class Value {
    /**
     * 0 = horiz
     * 1 = vert
     * 2 = diagM
     * 3 = diagm
     */
    public DirectionValue[] directions;

    Value() {
        directions = new DirectionValue[4];
        for (int i = 0; i < 4; i++) {
            directions[i] = new DirectionValue();
        }
    }

    /**
     * Questa funzione ritorna se è possibile fare un doppio gioco mettendo una cella su questa
     * Presuppone che la cella sia ancora libera (altrimenti non so esattamente cosa fa a calcolarsi)
     * 
     * I doppi giochi 'primitivi' sono di due tipi
     * 1. ho due celle libere all'inizio e in fondo
     * 2. Ho una combo in due direzioni differenti.
     */
    public boolean isDoublePlay() {
        for (int i = 0; i < 4; i++) {
            if (directions[i].isInLineDoublePlay()) return true;
            if (directions[i].bestWin() != 2) continue;
            for (int j = i + 1; j < 4; j++) {
                if (directions[j].bestWin() == 2) return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < 4; i++) {
            s += directions[i].toString() + " ";
        }
        return s;
    }
}
