package mnkgame.mics;

import mnkgame.MNKCell;

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
class HeuristicCell implements Comparable<HeuristicCell>{
    private final int i;
    private final int j; 
    public int heuristicValue;
    // le direzioni sono
    // 0 - sopra
    // 1 - sotto
    // 2 - destra
    // 3 - sinistra
    // 4 - alto e sinistra
    // 5 - basso e destra
    // 6 - alto e destra
    // 7 - basso e sinistra
    
    //distanza massima prima di trovare una cella dell'altro segno max K
    public int depthDirection[];
    //numero di celle segnare interne alla depthDirection
    public int crossedCell[];
    //celle vicinie segnate dallo stesso player
    public int adjCell[];

    

    public HeuristicCell(int i, int j) {
        this.i = i;
        this.j = j;
        depthDirection = new int[8];
        numberCrossedCell = new int[8];
        updateHeuristicValue();
    }

    void updateHeuristicValue(){
        for(int i = 0; i < 4; i++){
            //caso vince
            if(adjCell[i] + adjCell[i+1] >= K){
                heuristicValue = Integer.MAX_VALUE;
                return;
            }

        }
    }

    MNKCell toMNKCell() {
        return new MNKCell(i, j);
    }

}