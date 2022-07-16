package mnkgame;

import java.util.ArrayList;
import java.util.Random;

/**
 * Software player only a bit smarter than random.
 * <p> It can detect a single-move win or loss. In all the other cases behaves randomly.
 * </p> 
 */
public class NostroPlayer implements MNKPlayer {
	private MNKBoard Board;
	private MNKGameState myWin;
	private MNKGameState yourWin;
	private int TIMEOUT;
    private final int kinf = 2; 
    private final int kMyWinValue = 1;
    private final int kYourWinValue = -1;
    private final int kDrawValue = 0;

	public NostroPlayer() {}


	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		// New random seed for each game
		Board   = new MNKBoard(M,N,K);
		myWin   = first ? MNKGameState.WINP1 : MNKGameState.WINP2; 
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
		TIMEOUT = timeout_in_secs;	
	}
    
    
    // il giocatore minimo 
    public int minPlayer(MNKCell[] actions, int alpha, int beta) {
        int v = kinf; 
        for (int i = 0; i < actions.length; i++) {
            MNKGameState newState = MNKGameState.OPEN; 
            newState = Board.markCell(actions[i].i, actions[i].j);

            if (newState == myWin) {
                v = Math.min(v, kMyWinValue);
                Board.unmarkCell();
                continue;
            } else if (newState == yourWin) { 
                Board.unmarkCell();
                return kYourWinValue;
            } else if (newState == MNKGameState.DRAW) {
                v = Math.min(v, kDrawValue);
                Board.unmarkCell();
                continue;
            }
            
            MNKCell newActions[] = new MNKCell[actions.length - 1]; 
            int newActionsIdx = 0; 
            for (int j = 0; j < actions.length; j++) {
                if (j == i) continue; 
                newActions[newActionsIdx++] = actions[j]; 
            }

            v = Math.min(v, maxPlayer(newActions, alpha, beta)); 


            Board.unmarkCell();
            if (v <= alpha) return v;
            beta = Math.min(beta, v);
            // riaggiungere dopo averlo tolto in i, sempre in i
        }
        return v; 
    }

    // nel minPlayer e maxPlayer sarebbe meglio sostituire MNKCELL[] action, con una linked list
    // poichè il costo di scorrerla è n ma tanto bisogna farlo, e l'inserimento e la rimozione è O(1)
    // che viene comodo quando la si passa da una funzione all'altra
    
    // il giocatore massimo 
    private int maxPlayer(MNKCell[] actions, int alpha, int beta) {
        int v = -kinf; 

        for (int i = 0; i < actions.length; i++) {
            
            MNKGameState newState = MNKGameState.OPEN; 
            newState = Board.markCell(actions[i].i, actions[i].j);
            if (newState == myWin) {
                v = Math.max(v, kMyWinValue);
                Board.unmarkCell();
                return kMyWinValue; // il giocatore 1 è il massimo, ora ha vinto.
            } else if (newState == yourWin) { 
                v = Math.max(v, kYourWinValue);
                Board.unmarkCell();
                continue;
            } else if (newState == MNKGameState.DRAW) {
                v = Math.max(v, kDrawValue);
                Board.unmarkCell();
                continue;
            }
            
            MNKCell[] newActions = new MNKCell[actions.length - 1]; 
            int newActionsIdx = 0; 
            for (int j = 0; j < actions.length; j++) {
                if (j == i) continue; 
                newActions[newActionsIdx++] = actions[j]; 
            }

            v = Math.max(v, minPlayer(newActions, alpha, beta)); 

            Board.unmarkCell();
            if (v >= beta) return v;
            alpha = Math.max(alpha, v);
            // riaggiungere dopo averlo tolto in i, sempre in 
        }
        return v;
    }
    

    //utilizziamo la board globale per aggiungere e togliere e ci fermiamo quando uno vince
	public MNKCell selectCell(MNKCell[] FreeCell, MNKCell[] MovedCell) {
        MNKCell bestCell = FreeCell[0]; 
        int v = -kinf; 
        int alpha = -kinf;
        int beta = kinf;
        // questo è come se fosse un max player, ma tiene in conto anche della cella
        for (int i = 0; i < FreeCell.length; i++) {
            MNKGameState newState = MNKGameState.OPEN; 
            newState = Board.markCell(FreeCell[i].i, FreeCell[i].j);
            if (newState == myWin) {
                // print debug cell position
                System.out.println("DEBUG: " + FreeCell[i].i + " " + FreeCell[i].j);

                return FreeCell[i]; // vintoooo
            } else if (newState == yourWin) {
                Board.unmarkCell();
                continue; 
            } else if (newState == MNKGameState.DRAW) {
                if (kDrawValue > v) {
                    v = kDrawValue;
                    bestCell = FreeCell[i];
                }
                Board.unmarkCell();
                continue; 
            }
            
            MNKCell[] newActions = new MNKCell[FreeCell.length - 1]; 
            int newActionsIdx = 0; 
            for (int j = 0; j < FreeCell.length; j++) {
                if (j == i) continue; 
                newActions[newActionsIdx++] = FreeCell[j]; 
            }

            int minPlayerValue = minPlayer(newActions, alpha, beta);
            if (minPlayerValue > v) {
                v = minPlayerValue; 
                bestCell = FreeCell[i]; 
            }

            Board.unmarkCell();
            if (v >= beta) return bestCell;
            alpha = Math.max(alpha, v);
            // riaggiungere dopo averlo tolto in i, sempre in 
        }
        return bestCell;
	}

	public String playerName() {
		return "Nostro";
	}
}
