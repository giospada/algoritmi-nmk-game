## Analisi teorica `markCell` e `unmarkCell`
In questa parte faccio una veloce analisi del tempo della board custom.
Vogliamo principalmente andare a confrontare il tempo di esecuzione di `markCell` 
e `unmarkCell` in entrambe le implementazioni, ed andare a vedere se si ha veramente
qualche guadagno nella nostra Board per i casi grandi. 

### MNKBoard
La `MNKBoard` esegue operazioni a tempo costante per tutte le 4 direzioni, ossia orizzontale,
verticale, diagonale maggiore e minore; al massimo 
per tutte le direzioni esegue for di costo `O(k)`, dato che esegue 2 for per ogni direzione, di distanza massima k.
Quindi il costo teorico per ogni direzione è lineare.
La costante inoltre è abbastanza bassa, circa 4, perché per ogni direzione, mettendoci nel caso in cui non è una configurazione vittoriosa,
può fare al massimo k - 2 checks, altrimenti sarebbe vittoriosa, per cui si avrebbe un costo di `4(k - 2)`.
Anche ponendoci nel caso peggiore di una configurazione vittoriosa non si ha una costante eccessivamente alta:
poniamo che i primi 3 check falliscano, con un costo di `3(k - 2)` e supponendo che l'ultimo check sia eseguito senza avere un break
ha un costo di `2k` per un costo totale di `5k - 6` restiamo comunque in questo ordine di grandezza

### CBoard
La `CBoard` fa differenza fra il verso di ogni direzione, per cui avremo un totale di 8 versi. Per ogni verso... TODO questa analisi è un pò più difficile :P