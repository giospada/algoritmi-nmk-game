package mnkgame.mics;

/**
 * Questa classe è un modo per rappresentare il valore di una cella
 * in una board di gioco.
 * 
 * I valori principali sono i seguenti:
 * Per ogni direzione salvo:
 * - il numero di celle da riempire prima di vincere a destra, a sinistra, e al centro
 */
public class Value {
    public DirectionValue horiz;
    public DirectionValue vert;
    public DirectionValue diag1;
    public DirectionValue diag2;

    // TODO fare i checks per i doppi giochi
}
