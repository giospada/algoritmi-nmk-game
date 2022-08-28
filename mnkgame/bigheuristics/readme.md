In questa directory sta presente l'iterative deepening fatto bene (ossia non è un minimax celato)
che utilizza effettivamente il deepening iterativo.
Per fare questo mi voglio creare una nuova struttura, simile a un nodo di ricerca, che mi tenga in conto quali siano state
le mosse fatte dall'inizio fino al nodo attuale.

Inoltre sempre qui volevo provare a implementare una beam search, con un limite di nodi guardati
utilizzando l'euristica del MICS, per scegliere le mosse migliori da considerare.

## Changelog per la versione 2 Iterative player
Ho introdotto una hashmap per ricordarmi quali valori sono stati inseriti e quali no, questo dovrebbe
permettere un uso più efficiente della memoria, e limitare la size della queue, introducendo una maggiore
efficienza, per ora però nella pratica non si vedono cambiamenti sostanziali.

## Idee random che possono servire
Invece di valutare l'euristica in questo modo, si può tenere per ogni cella in modo greedy il minor numero
di mosse necessarie all'avversario per vincere, lo stesso per me stesso e valutare la cella in questo modo.
A giocare con valori o costanti (o numeri magici) bisognerebbe avere anche modo per fare un tuning, in modo
che sia la migliore costante possibile, questo si può fare con un Q-learning, ma non ho compreso bene esattamente
in che modo funzioni.
