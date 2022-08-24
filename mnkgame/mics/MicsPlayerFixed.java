package mnkgame.mics;

import java.util.ArrayList;
import java.util.Collections;

import mnkgame.MNKCell;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;

// Mics Player ma con delle mosse prefissate per poter rispondere 
// nelle configurazioni di vittoria sicura
public class MicsPlayerFixed implements mnkgame.MNKPlayer {
    private Board B;
    private MNKGameState myWin;
    private MNKGameState yourWin;
    private int steps;
    private char caseKey;
    private ArrayList<CellPair> moves;
    private int M, K;
    public MicsPlayerFixed() {}

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        MNKCellState myState = first ? MNKCellState.P1 : MNKCellState.P2;
        B = new Board(M, N, K, myState);
        myWin = first ? MNKGameState.WINP1 : MNKGameState.WINP2;
        yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
        moves = new ArrayList<CellPair>();
        steps = 0;
        this.M = M;
        this.K = K;
        caseKey = ' ';
    }

    // time should never run out right? it's the first step!
    // @returns a winning cell if there is one
    private MNKCell findWinCell(MNKCell[] freeCells) {
        for (MNKCell d : freeCells) {
            if (B.markCell(d.i, d.j) == myWin) {
                return d;
            } else {
                B.unmarkCell();
            }
        }
        return null;
    }

    private MNKCell findPreventWinCell(MNKCell[] freeCells) {
        B.togglePlayer();  // turno dell'avversario
        for (MNKCell d : freeCells) {
            if (B.markCell(d.i, d.j) == yourWin) {
                B.unmarkCell();
                
                // vado a marcare io la cella con cui vincerebbe l'avversario
                B.togglePlayer();
                B.markCell(d.i, d.j);
                return d;
            } else {
                B.unmarkCell();
            }
        }
        B.togglePlayer();  // turno mio di nuovo
        return null;
    }

    private MNKCell selectCell4_3() {
        if (steps == 0) {
            // primo step
            // marco la cella in mezzo buona
            return new MNKCell(1, 1);
        } else if (steps == 1) {
            if (B.isFree(2, 1)) {
                return new MNKCell(2, 1);
            } else {
                return new MNKCell(2, 2);
            }
        } else if (steps == 2) {
            if (B.isMine(2, 1)) {
                if (B.isFree(0, 1)) {
                    return new MNKCell(0, 1); // vinto
                } else if (B.isFree(3, 1)) {
                    return new MNKCell(3, 1); // vinto
                } else {
                    return new MNKCell(1, 2); // creato un doppio gioco con (1, 1), (2, 1) e (1, 2)
                }
            } else { // ossia (2 2) è mio.
                if (B.isFree(0, 0)) {
                    return new MNKCell(0, 0); // vinto
                } else return new MNKCell(1, 2); // doppio gioco con (1, 1) (1, 2) (2, 2)
            }
        } else {  // vinci col doppio gioco
            // distingui le tipologie di doppio gioco che posso avere
            if (B.isMine(2, 2)) {
                if (B.isFree(0, 0)) {
                    return new MNKCell(0, 0);
                } else if (B.isFree(0, 2)) {
                    return new MNKCell(0, 2);
                } else if (B.isFree(1, 0)) {
                    return new MNKCell(1, 0);
                } else {
                    return new MNKCell(3, 2);
                } // l'avversario non può aver fatto più mosse di così
            } else { // doppio gioco con 2 1
                if (B.isFree(1, 0)) {
                    return new MNKCell(1, 0);
                } else { // altra possibilità del doppio gioco
                    return new MNKCell(3, 0);
                }
            }
        }
    }

    private MNKCell selectCell678_3() {
        switch(steps) {
            case 0:
                return new MNKCell(2, 2);
            case 1:
                // con queste voglio andare a stoppare un nemico 
                // check row 1
                boolean row1 = true;  // TODO, non so come fare a gestire questoooooo, il mio bot è errattoooo!
                for (int j = 0; j < 6; j++) {
                    if (B.isFree(1, j)) {
                        row1 = false;
                        break;
                    }
                }
                if (!row1) {
                    return new MNKCell(1, 2);
                } else if (B.isFree(3, 2)) {
                    return new MNKCell(3, 2);
                }
                else {
                    return new MNKCell(3, 3);
                }
            case 2:
                if (B.isMine(3, 2)) {
                    if (B.isFree(1, 2)) {
                        return new MNKCell(1, 2);  // (2, 2), (3, 2), (1, 2) miei, nemico in X1 e X2 (sconosciuto)
                    } else if (B.isFree(4, 2)) {
                        return new MNKCell(4, 2);  // (2, 2), (3, 2), (4, 2) miei, nemico in (1, 2) e X
                    } else {
                        return new MNKCell(3, 1);  // (2, 2), (3, 2), (3, 1) miei, nemico in (1, 2) e (4, 2)
                    }
                } else { // 3 3 is mine and 3 2 enemis
                    if (B.isFree(1, 1)) {
                        return new MNKCell(1, 1); // (1, 1), (2, 2), (3, 3) miei, nemico in (3, 2) e X
                    } else {
                         // (2, 2), (3, 3), (2, 3) miei, nemico in (3, 2), (1, 1)
                         // qui ho già vinto, grazie all'allineamento di (2, 2), (3, 3) (2, 3).
                        return new MNKCell(2, 3);
                    }
                }
            case 3:
                // posso avere questo solo se ho allineato (1, 1) e (2, 2) e (3, 3)
                // questo ramo è vero se il nemico ha occupato 3 2 
                if (B.isMine(1, 1)) { 
                    if (B.isFree(0, 0)) {
                        return new MNKCell(0, 0);  // vinto
                    } else if (B.isFree(4, 4)) {
                        return new MNKCell(4, 4);  // vinto
                    } else {
                        // doppio gioco con (3, 3) (2, 2) (1, 1) (2, 3) che sono tutti liberi.
                        // il nemico occupa (3, 2), (0, 0) e (4, 4), ho ampio margine di manovra.
                        caseKey = 'A';
                        return new MNKCell(2, 3);  // CASO A
                    }
                // ho allineati (2, 2), (1, 2), e (3, 2)
                } else if (B.isMine(1, 2)) {
                    if (B.isFree(0, 2)) {
                        return new MNKCell(0, 2);  // vinto
                    } else if (B.isFree(4, 2)) {
                        return new MNKCell(4, 2);  // vinto
                    } else {
                        if (B.isFree(2, 1)) {
                            // ora so che il nemico ha 2 pedine (0, 2) e (4, 2) e X
                            // io ho pedine in (2, 2), (1, 2), (3, 2) e (2, 1)
                            caseKey = 'B';
                            return new MNKCell(2, 1);  // CASO B
                        } else {
                            // ora so che il nemico ha 2 pedine (0, 2) e (4, 2) e (2, 1)
                            // ho allineati (2, 2), (1, 2), e (3, 2) e (2, 3)
                            caseKey = 'C';
                            return new MNKCell(2, 3);  // CASO C
                        }
                    }

                // hoallineati (2, 2), (3, 2) e (4, 2), so che un nemico è in (1, 2)
                } else if (B.isMine(4, 2)) {
                    if (B.isFree(5, 2)) {
                        return new MNKCell(5, 2);  // vinto.
                    } else {
                        if (B.isFree(3, 1)) {
                            // i nemici sono in (1, 2) e (5, 2) e X  
                            // mie pedine in (2, 2), (3, 2) e (4, 2) e (3, 1)
                            caseKey = 'D';
                            return new MNKCell(3, 1);  // CASO D
                        } else {
                            // i nemici sono in (1, 2) e (5, 2) e (3, 1).  
                            // (2, 2), (3, 2) e (4, 2) e(3, 3)
                            caseKey = 'E';
                            return new MNKCell(3, 3);  // CASO E
                        }
                    }

                // (2, 2), (3, 2), (3, 1) miei, nemico in (1, 2) e (4, 2)
                } else if (B.isMine(3, 1)) {
                    caseKey = 'F';
                    return new MNKCell(3, 3);  // praticamente vinto.  CASO F
                } else {  // (2, 2), (3, 3), (2, 3) miei, nemico in (3, 2), (1, 1)
                    // check if line or vert is empty, and go there.
                    boolean isHorizEmpty = true;
                    for (int j = 0; j < 5; j++) {  // anche nei casi per 7 e 8, mi basta checkare 6, il costo resta costante.
                        if (B.isEnemy(2, j)) {
                            isHorizEmpty = false;
                            break;
                        }
                    }

                    if (isHorizEmpty) {
                        // riesco a finire il gioco facendo check su (2, 0), (2, 4);
                        caseKey = 'G';
                        return new MNKCell(2, 1);  // CASO G
                    } else {
                        // so che il vertical è empty.
                        // riesco a finire il gioco facendo check su (0, 3), (4, 3);
                        caseKey = 'H';
                        return new MNKCell(1, 3);  // CASO H
                    }
                }
            default:
                System.out.println(caseKey);
                switch(caseKey) {
                    case 'A': {
                        boolean isHorizEmpty = true;
                        for (int j = 0; j < 5; j++) {  // anche nei casi per 7 e 8, mi basta checkare 6, il costo resta costante.
                            if (B.isEnemy(2, j)) {
                                isHorizEmpty = false;
                                break;
                            }
                        }
    
                        if (isHorizEmpty) {
                            // riesco a finire il gioco facendo check su (2, 0), (2, 4);
                            caseKey = 'G';
                            return new MNKCell(2, 1);  // CASO G
                        } else {
                            // so che il vertical è empty.
                            // riesco a finire il gioco facendo check su (0, 3), (4, 3);
                            caseKey = 'H';
                            return new MNKCell(1, 3);  // CASO H
                        }
                    }
                    case 'B': {
                        boolean isDiagFree = true;
                        for (int i = 0; i < 5; i++) {
                            if (B.isEnemy(i + 1, i)) {
                                isDiagFree = false;
                                break;
                            }
                        }

                        
                        if (isDiagFree) {
                            caseKey = 'J';
                            return new MNKCell(4, 3);  // CASO J
                        }

                        // check if first row is free
                        boolean isRowFree = true;
                        for (int j = 0; j < 5; j++) {
                            if (B.isEnemy(2, j)) {
                                isRowFree = false;
                                break;
                            }
                        }

                        if (isRowFree) {
                            caseKey = 'G';
                            return new MNKCell(2, 3);  // CASO G
                        } else {
                            // arrivati a sto ramo, il nemico ha usato due mosse presenti in questo punto
                            // per la diagonale e per la riga, quindi da ora ho il doppio gioco fatto
                            // sulla riga 3 oppure sulla colonna 1
                            
                            // ora so che il nemico ha 2 pedine (0, 2) e (4, 2) sulla riga 2 e sulla diagonale 1 0
                            // io ho pedine in (2, 2), (1, 2), (3, 2) e (2, 1) e (3, 1)
                            caseKey = 'K';
                            return new MNKCell(3, 1);  // CASO K
                        }
                    }
                    case 'C': {
                        // ora so che il nemico ha 2 pedine (0, 2) e (4, 2) e (2, 1)
                        // ho allineati (2, 2), (1, 2), e (3, 2) e (2, 3)
                        // check allineamento su riga 3
                        boolean isRowFree = true;
                        for (int j = 0; j < 5; j++) {
                            if (B.isEnemy(3, j)) {
                                isRowFree = false;
                                break;
                            }
                        }
                        if (isRowFree) {
                            // ossia ho il doppio gioco impostato sulla riga 3
                            caseKey = 'F';
                            return new MNKCell(3, 3);  // CASO F
                        } else {
                            // doppio gioco impostato su una diagonale minore
                            caseKey = 'M';
                            return new MNKCell(1, 3);  // CASO M
                        }
                    }
                    case 'D': {
                        boolean isDiagFree = true;
                        for (int i = 0; i < 5; i++) {
                            if (B.isEnemy(i, 4 - i)) {
                                isDiagFree = false;
                                break;
                            }
                        }
                        
                        if (isDiagFree) {
                            caseKey = 'M';
                            return new MNKCell(1, 3);
                        }

                        boolean isHorizEmpty = true;
                        for (int j = 0; j < 5; j++) {  // anche nei casi per 7 e 8, mi basta checkare 6, il costo resta costante.
                            if (B.isEnemy(3, j)) {
                                isHorizEmpty = false;
                                break;
                            }
                        }
    
                        if (isHorizEmpty) {
                            caseKey = 'F';
                            return new MNKCell(3, 3);  // CASO F
                        } else {
                            // i nemici sono in (1, 2) e (5, 2) e una sulla riga 3, una sulla diagonale minore 0 4 
                            // mie pedine in (2, 2), (3, 2) e (4, 2) e (3, 1)

                            // arrivati a sto ramo, il nemico ha usato due mosse presenti in questo punto
                            // per la diagonale e per la riga, quindi da ora ho il doppio gioco fatto
                            // sulla riga 2 oppure sulla colonna 1
                            caseKey = 'I';
                            return new MNKCell(2, 1);  // CASO I
                        }
                    }
                    case 'E': {
                        System.out.println("CASO E");
                        // check diagonale maggiore
                        boolean isDiagFree = true;
                        for (int i = 0; i < 5; i++) {
                            if (B.isEnemy(i, i)) {
                                isDiagFree = false;
                                break;
                            }
                        }

                        if (isDiagFree) {
                            caseKey = 'N';
                            return new MNKCell(1, 1);  // CASO N
                        } else {
                            // so che è libera, altrimenti la diagonale sarebbe stata libera.
                            // ora posso andare a fare doppio gioco sulla colonna 3 o riga 2
                            caseKey = 'O';
                            return new MNKCell(2, 3);  // CASO E
                        }
                    }
                    case 'F': // doppio gioco sulla 3 riga
                        if (B.isFree(3, 0)) {
                            return new MNKCell(3, 0);
                        } else {
                            return new MNKCell(3, 4);
                        }
                    case 'G': // doppio gioco sulla 2 riga
                        if (B.isFree(2, 0)) {
                            return new MNKCell(2, 0);
                        } else {
                            return new MNKCell(2, 4);
                        }
                    case 'H': {  // doppio gioco sulla 3 colonna
                        int first = 5;
                        int last = 0;
                        // prendo le ultime celle libere
                        for (int i = 0; i < 6; i++) {
                            if (B.isFree(i, 3) || B.isEnemy(i, 3)) {
                                first = i;
                            }
                        }
                        for (int i = 5; i >= 0; i--) {
                            if (B.isFree(i, 3) || B.isEnemy(i, 3)) {
                                last = i;
                            }
                        }

                        if (B.isFree(first, 3)) {
                            return new MNKCell(first, 3);
                        } else {
                            return new MNKCell(last, 3);
                        }
                    }
                    case 'I': {
                        // check 2 riga
                        boolean isRowFree = true;
                        for (int j = 0; j < 5; j++) {
                            if (B.isEnemy(2, j)) {
                                isRowFree = false;
                                break;
                            }
                        }

                        if (isRowFree) {
                            caseKey = 'G';
                            return new MNKCell(2, 3);  // CASO G
                        } else {
                            // doppio gioco impostato sulla colonna 1
                            caseKey = 'L';
                            return new MNKCell(1, 3);  // CASO L
                        }
                    }
                    case 'J':  // check sulla diagonale maggiore da 1 0
                        if (B.isFree(1, 0)) {
                            return new MNKCell(1, 0);  // win
                        } else {
                            return new MNKCell(5, 4);  // win
                        }
                    case 'K': {
                            // check row 3
                            boolean isRowFree = true;
                            for (int j = 0; j < 5; j++) {
                                if (B.isEnemy(3, j)) {
                                    isRowFree = false;
                                    break;
                                }
                            }

                            if (isRowFree) {
                                caseKey = 'F';
                                return new MNKCell(3, 3);  // CASO F
                            } else {
                                // doppio gioco impostato sulla colonna 1
                                caseKey = 'L';
                                return new MNKCell(1, 3);  // CASO L
                            }
                        }
                    case 'L':  {// doppio gioco sulla colonna 1
                        int first = 5;
                        int last = 0;
                        // prendo le ultime celle libere
                        for (int i = 0; i < 6; i++) {
                            if (B.isFree(i, 1)) {
                                first = i;
                            }
                        }

                        for (int i = 5; i >= 0; i--) {
                            if (B.isFree(i, 1)) {
                                last = i;
                            }
                        }

                        if (B.isFree(first, 1)) {
                            return new MNKCell(first, 1);  // win
                        } else {
                            return new MNKCell(last, 1);  // win
                        }
                    }
                    case 'M':  // doppio gioco sulla diagonale minore da 0 4
                        if (B.isFree(0, 4)) {
                            return new MNKCell(0, 4);  // win
                        } else {
                            return new MNKCell(4, 0);  // win
                        }
                    case 'N':  // doppio gioco sulla diagonale maggiore da 0 0
                        if (B.isFree(0, 0)) {
                            return new MNKCell(0, 0);  // win
                        } else {
                            return new MNKCell(4, 4);  // win
                        }
                }
        }
        return null;  // non succede mai.
    }

    public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        if (movedCells.length > 0) {
            MNKCell c = movedCells[movedCells.length - 1]; // Recover the last move from MC
            B.markCell(c.i, c.j); // Save the last move in the local MNKBoard
        }
        MNKCell specialCaseCell = null;
        if (myWin == MNKGameState.WINP1) {
            if (M == 4 && K == 3)
                specialCaseCell = selectCell4_3();
            else if ((M == 6 || M == 7 || M == 8) && K == 4)
                specialCaseCell = selectCell678_3();
        }

        if (specialCaseCell != null) {
            B.markCell(specialCaseCell.i, specialCaseCell.j);
            steps++;

            return specialCaseCell;
        }

        MNKCell winCell = findWinCell(freeCells);
        if (winCell != null) return winCell;

        MNKCell preventWinCell = findPreventWinCell(freeCells);
        if (preventWinCell != null) return preventWinCell;

        for (int i = 0; i < freeCells.length; i++) {
            int heuristic = 0;
            heuristic += B.getHeuristic(freeCells[i].i, freeCells[i].j);    
            heuristic += B.getSwappedHeuristics(freeCells[i].i, freeCells[i].j);    
            moves.add(new CellPair(heuristic, freeCells[i]));
        }

        Collections.sort(moves);
        MNKCell bestCell = moves.get(0).cell;
        B.markCell(bestCell.i, bestCell.j);
        moves.clear();
        return bestCell;
    }

    public String playerName() {
        return "Mics Player";
    }
}
