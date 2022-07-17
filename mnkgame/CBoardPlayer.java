package mnkgame;

import java.util.Stack;

public class CBoardPlayer implements MNKPlayer {
	private CBoard Board;
	private MNKGameState myWin;
	private MNKGameState yourWin;
	private int TIMEOUT;
    private final int kinf = 2; 
    private final int kMyWinValue = 1;
    private final int kYourWinValue = -1;
    private final int kDrawValue = 0;

	public CBoardPlayer() {}

	public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
		Board   = new CBoard(M,N,K);
		myWin   = first ? MNKGameState.WINP1 : MNKGameState.WINP2; 
		yourWin = first ? MNKGameState.WINP2 : MNKGameState.WINP1;
		TIMEOUT = timeout_in_secs;	
	}
    
    // il giocatore minimo 
    public int minPlayer( int alpha, int beta) {
        int v = kinf; 
        
        CNode<CCell> head = Board.getFreeCell().getHead();

        while(head!=null) {
            MNKGameState newState = MNKGameState.OPEN; 
            newState = Board.markCell(head);

            if (newState != MNKGameState.OPEN) Board.unmarkCell();
            if (newState == myWin) {
                continue;
            } else if (newState == yourWin) { 
                return kYourWinValue;
            } else if (newState == MNKGameState.DRAW) {
                v = Math.min(v, kDrawValue);
                continue;
            }
            
            v = Math.min(v, maxPlayer(alpha, beta)); 

            Board.unmarkCell();
            if (v <= alpha) return v;
            beta = Math.min(beta, v);

            head=head.next;
        }
        return v; 
    }

    // nel minPlayer e maxPlayer sarebbe meglio sostituire MNKCELL[] action, con una linked list
    // poichè il costo di scorrerla è n ma tanto bisogna farlo, e l'inserimento e la rimozione è O(1)
    // che viene comodo quando la si passa da una funzione all'altra
    
    // il giocatore massimo 
    private int maxPlayer(int alpha, int beta) {
        int v = -kinf; 
        CNode<CCell> head = Board.getFreeCell().getHead();

        while(head!=null) {
            MNKGameState newState = MNKGameState.OPEN; 
            newState = Board.markCell(head);
            if (newState == myWin) {
                v = Math.max(v, kMyWinValue);
                Board.unmarkCell();
                return kMyWinValue;
            } else if (newState == yourWin) {
                Board.unmarkCell();
                continue;
            } else if (newState == MNKGameState.DRAW) {
                v = Math.max(v, kDrawValue);
                Board.unmarkCell();
                continue;
            }


            v = Math.max(v, minPlayer(alpha, beta)); 

            Board.unmarkCell();
            if (v >= beta) return v;
            alpha = Math.max(alpha, v);

            head=head.next;
        }
        return v;
    }

    // utilizziamo la board globale per aggiungere e togliere e ci fermiamo quando uno vince
	public MNKCell selectCell(MNKCell[] freeCells, MNKCell[] movedCells) {
        if(movedCells.length > 0) {
			MNKCell c = movedCells[movedCells.length-1]; // Recover the last move from MC
			Board.markCell(c.i,c.j);         // Save the last move in the local MNKBoard
		}

        CNode<CCell> head = Board.getFreeCell().getHead();
        CNode<CCell> bestCell = head;
        int v = -kinf; 
        int alpha = -kinf;
        int beta = kinf;

        // questo è come se fosse un max player, ma tiene in conto anche della cella

        while(head!=null) {
            MNKGameState newState = MNKGameState.OPEN; 
            CCell currentCell=head.getData();
            newState = Board.markCell(head);

            if (newState == myWin) {
                return currentCell.toMNKCell(); // vintoooo
            } else if (newState == MNKGameState.DRAW) {
                if (kDrawValue > v) {
                    v = kDrawValue;
                    bestCell = head;
                }
                Board.unmarkCell();
                continue; 
            }
            
            int minPlayerValue = minPlayer(alpha, beta);
            if (minPlayerValue > v) {
                v = minPlayerValue; 
                bestCell = head; 
            }

            Board.unmarkCell();
            if (v >= beta) return bestCell.getData().toMNKCell();
            alpha = Math.max(alpha, v);

            head=head.next;
        }
        Board.markCell(bestCell);
        return bestCell.getData().toMNKCell();
	}

	public String playerName() {
		return "CBoardPlayer";
	}
}
