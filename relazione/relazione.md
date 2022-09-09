---
header-includes:
  - \usepackage[ruled,vlined,linesnumbered]{algorithm2e}
  - \usepackage{graphicx}  # immagini
  - \usepackage{float}     # immagini, così posso mettere le immagini dove voglio
---
\graphicspath{ {./relazione/images/} }

# Progetto Algoritmi a.s. 2021/2022

## Team

Xuanqiang Huang # TODO: matricola

Giovanni Spadaccini # TODO: matricola

## Riassunto

**first take**
La nostra implementazione di un AI per il gioco di MNKGame utilizza un algoritmo di Minimax 
euristico con alpha-beta pruning. L'algoritmo utilizza l'euristica di valutazione per scoprire l'ordine
di esplorazione di un numero limitato di nodi, le esplora fino a un livello di profondità prefissato, dopo 
il quale ritorna un valore euristico

**second take**
Abbiamo utilizzato di un algoritmo di Minimax euristico con alpha-beta pruning per la risoluzione 
di un gioco di tris generalizzato. L'algoritmo utilizza l'euristica di valutazione per scoprire l'ordine
di esplorazione di un numero limitato di nodi, le esplora fino a un livello di profondità prefissato, dopo 
il quale ritorna un valore euristico.
Dai test fatti in locale, l'algoritmo sembra avere capacità simili o superiori a quelle di un umane per 
le tavole di dimensioni umane (ossia da 10 in giù).

**note a caso**
Il gioco è stato implementato in linguaggio Java, scelta che rendeva difficile la gestione
della memoria a basso livello per la presenza di un garbage collector.

## Introduzione

Il gioco proposto è una versione generalizzata del gioco (nought and crosses) conosciuto più generalmente
in occidente come tris, o gomoku in Giappone: un gioco a due giocatori su una tavola simile 
a quanto in immagine, a turni in cui bisogna allineare un certo numero di pedine del prioprio
giocatore al fine di vincere.

\begin{figure}[H]
    \begin{center} 
    \includegraphics[scale=0.5]{gomoku.png}
    \caption{Esempio di tavola di gioco di gomoku in cui il giocatore nero ha vinto}
    \label{fig:gomoku}
    \end{center}
\end{figure}

Seguendo la caratterizzazione di un ambiente di gioco di [Russel e Norvig$^1$](#refs) possiamo
definire il gioco come un ambiente deterministico, multigiocatore, con informazioni complete, 
sequenziale, statico, conosciuto e discreto. Questa descrizione ci ha permesso di avere una 
prima idea di quali algoritmi potessero essere utilizzati per la risoluzione del gioco, in 
quanto in letteratura questo problema, e problemi simili, sono stati risolti con tecniche
che hanno sopravissuto allo scorrere del tempo.

Sotto questa logica abbiamo scelto l'implementazione di un minimax euristico.

## MarkCell e unmarkCell
Abbiamo ideato un sistema che è ingrado di fare `markCell`, `unmarkCell` in tempo costante 
in caso ottimo, pessimo e medio, senza considerare i checks aggiuntivi per verificare lo stato
del gioco e l'aggiornamento dell'euristica. 

Al fine di raggiungere questa velocità, l'insieme delle mosse eseguite è tenuto alla fine 
di un array che contiene tutte le mosse, in modo simile a quanto fa una heap studiata nel corso
al momento di rimozione. Queste mosse sono tenute come se fossero uno stack, e possono essere 
ripristinate con semplici assegnamenti. Questa implementazione migliora rispetto alla board provvista
nel caso pessimo, in quanto non deve più necessitare di un hashtable, il cui caso pessimo è $O(n)$ con n 
la grandezza della table.

**Nota**: tutte le celle contengono un *index* che indica la posizione dell'array in cui è contenuta la mossa
se questa non è ancora stata rimossa, altrimenti, indica la posizione di ritorno.

\begin{algorithm}[H]
    \DontPrintSemicolon
    \SetAlgoLined
    \KwResult{Cella richiesta della tavola è marcata}
    \SetKwInOut{Input}{Input}
    \SetKwInOut{Output}{Output}
    \Input{int freeCellsCount: numero di celle libere, è compreso fra 1 e allCells.length}
    \Input{int index: l'index della cella da marcare, compresa fra 0 e freeCellsCount}
    \Input{Cell[] allCells: array tutte le celle, in cui le prime freeCellsCount sono considerate libere}
    \Output{void: viene modificata allCells}
    \BlankLine

    allCells[freeCellsCount - 1].index = allCells[index].index\;
    swap(allCells[freeCellsCount - 1], allCells[index])\;
    \tcp{marca la cella come occupata dal giocatore}
    mark(allCells[freeCellsCount - 1])\;
    freeCellsCount = freeCellsCount - 1\;
    \caption{markCell senza checks sulla board}
\end{algorithm}

\begin{algorithm}[H]
    \DontPrintSemicolon
    \SetAlgoLined
    \KwResult{Viene rimosso l'ultima mossa della tavola}
    \SetKwInOut{Input}{Input}
    \SetKwInOut{Output}{Output}
    \Input{int freeCellsCount: numero di celle libere, è compreso fra 0 e allCells.length - 1}
    \Input{Cell[] allCells: array tutte le celle, in cui le prime freeCellsCount sono considerate libere}
    \BlankLine

    \tcp{marca la cella come libera}
    markFree(allCells[freeCellsCount - 1])\;
    swap(allCells[allCells[freeCellsCount].index], allCells[freeCellsCount])\;
    allCells[freeCellsCount].index = freeCellsCount\;
    freeCellsCount = freeCellsCount + 1\;
    \caption{markCell senza checks sulla board}
\end{algorithm}

## L'Euristica

In questo progetto sono fondamentali le euristiche utilizzate al fine del successo del Player.
Esiste una unica euristica che viene utilizzata, in modi diversi, sia per la valutazione della board
sia per la scelta delle mosse.

### MICS - Minimum Incomplete Cell Set

L'euristica del MICS racchiude in sé 3 informazioni principali:
1. Quanto è favorevole una cella secondo aperture e pedine mie e nemiche.
2. In modo analogo calcolo stesso valore per il nemico.
3. La somma dei due valori precedenti mi dà una stima di criticità della singola cella (senza la presenza di doppi-giochi e fine-giochi)

### Calcolo del MICS con le sliding window

### Rilevamento dei doppio-gioci e fine-giochi

Con il sistema a sliding window possiamo anche rilevare con molta facilità alcune celle particolari *critiche* ossia 
situazioni di doppi giochi oppure giochi ad una mossa dalla fine. 

> Definiamo **fine-giochi** le celle per cui esiste almeno una sliding window a cui manca 1 mossa per vincere

È chiaro come queste celle siano molto importanti sia per noi, al fine della vittoria, sia per il nemico, al fine di bloccarli.

> Definiamo **doppi-giochi banali** le celle per cui 
esistono due o più sliding window per cui mancano 2 mosse per vincere

Se abbiamo una tale configurazione, si può notare molto facilmente come muovere su quella cella riduce le mosse per vincere
di 1 in entrambe la sliding window. Abbiamo allora due sliding window in cui manca una mossa per vincere, per cui il nemico
può bloccare al massimo una, garantendoci la vittoria sull'altra.

Riguardo i doppi-giochi non banali, ossia doppi giochi in cui abbiamo bisogno di 3 o più mosse per vincere ci basiamo sulla
capacità del minimax di scovarli, non siamo riusciti a trovare un modo per codificare questo caso tramite le sliding-window.

### Punteggi per configurazioni du doppio-gioco e fine-gioco

Sono assegnati alcuni punteggi speciali alle celle di doppio-gioco o fine-gioco.

Queste configurazioni non sono espressamente visibili al MICS, per cui abbiamo assegnato dei valori fissi a *gradini*,
ossia in qualunque modo si calcoli il MICS, il valore euristico di questo non può superare il valore assegnato da
una cella di doppio gioco, e quest'ultima non può superare il valore di una cella di fine-gioco.

Quindi in ordine di importanza abbiamo:
1. Cella di fine-gioco
2. Cella di doppio-gioco banale
3. Cella valutata dall'euristica del MICS.

# TODOS

Alcune cose importanti che si dovrebbero fare?

- [x] pseudocodice di markCell
- [x] pseudocodice di unmarkCell
- [X] spiegazione delle FreeCell
- [ ] Spiegazione dell'euristica
  - [ ] 1. Spiegazione dell'ordering delle mosse -> cenno a Late Move reduction (o citazione del paper di MICS)
  - [ ] 2. Spiegazine dell'eval della board
  - [ ] 3. Note: sul pruning utile grazie a questo ordering
  - [ ] 4. 
  - [ ] 
- [ ] algoritmo di sorting delle celle
- [ ] spiegazione del timer test (numero dei nodi cercati)
- [ ] spiegazione dei valori euristici per l'esplorazione in depth e in weight

<!-- Esempio di algoritmo in MD, che si può compilare con pandoc -->
## Algorithm 1

Just a sample algorithmn
\begin{algorithm}[H]
\DontPrintSemicolon
\SetAlgoLined
\KwResult{Write here the result}
\SetKwInOut{Input}{Input}\SetKwInOut{Output}{Output}
\Input{Write here the input}
\Output{Write here the output}
\BlankLine
\While{While condition}{
    instructions\;
    \eIf{condition}{
        instructions1\;
        instructions2\;
    }{
        instructions3\;
    }
}
\caption{While loop with If/Else condition}
\end{algorithm}

## Approcci fallimentari
1. Simulazione di Montecarlo (MCTS), dato il grande successo di AlphaGo
   1. Guardava celle che avevano poco valore per la vittoria
   2. Guardava tutti gli stati ad ogni livello, il che pesava molto anche sulla memoria (poichè si teneva tutto il game tree)
   3. Il limite di tempo era troppo basso per avere un numero di simulazioni sufficenti
   4. La capacità dell'hardware incideva molto sui risultati.

2. Puro algoritmo heuristico (anche conosciuto come Greedy best first Search)
   1. Non riusciva ad andare in profondità, dato che selezionava ogni volta la cella con maggiori probabilità di
   vittoria al primo livello, questo non gli permetteva di pianificare le proprie mosse.

3. alpha beta pruning puro, per i test grandi impiegava troppo tempo d'esecuzione, non riuscendo a fare l'eval di neanche una mossa
4. [rule-based strategy$^2$][#refs], basato su 5 steps che riporto qui testualmente: 
Rule 1  If the player has a winning move, take it. 
Rule 2  If the opponent has a winning move, block it. 
Rule 3  If the player can create a fork (two winning ways) after this move, take it. 
Rule 4  Do not let the opponent create a fork after the player’s move. 
Rule 5  Move in a way such as the player may win the most number of possible ways. 
   1. Queste regole sono state molto importanti come guida del nostro progetto, nonostante non siano applicate in modo esplicito, hanno guidato il valore fisso per le celle di doppio-gioco e fine-gioco.

## Miglioramenti possibili
1. Utilizzare un sistema ad apprendimento automatico per decidere il `BRANCHING_FACTOR` e la `DEPTH_LIMIT` che ora sono
hardcodate secondo l'esperienza umana.
2. Utilizzare più threads per l'esplorazione parallela dell'albero di ricerca (non possibile per limiti imposti).

# Conclusione
Abbiamo osservato come un classico algoritmo Minimax con alpha-beta pruning possa giocare in modo simile, o superiore 
rispetto all'essere umano per le board di grandezza adeguata per l'umano (<= 10 per M e N), data una euristica che gli 
permetta di potare ampi rami di albero.

# References
<div id="refs"></div>

1. Russell, Stuart J., e Peter Norvig. Artificial Intelligence: A Modern Approach. Fourth edition, Global edition, Pearson, 2022. Chapt. 2

2. Development of Tic-Tac-Toe Game Using Heuristic Search IOP Publishing, 2nd Joint Conference on Green Engineering Technology & Applied Computing 2020, Zain AM, Chai CW, Goh CC, Lim BJ, Low CJ, Tan SJ
