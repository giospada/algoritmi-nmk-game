Implementazione del paper di MICS 2016 in questa cartella

### STUDIO SUI DOPPI GIOCHI
Vogliamo cercare di fare delle osservazioni generali in quanto riguarda il doppio gioco.
Ossia vogliamo cercare di codificare alcuni pattern che conducono necessariamente alla vittoria
del giocatore che li utilizzi.

**BASE**
Consideriamo in questo passo i doppi giochi primitivi
1. Doppio gioco su singola linea
   1. Il doppio gioco su singola linea consiste in un allineamento di K - 1 celle in modo tale per cui
   entrambi i due lati siano liberi.
2. Doppio gioco su più linee
   1. Consiste nell'allineamento in due direzioni diverse sulla stessa cella di K - 2 celle
   
Su questi due tipi di doppi giochi primitivi vogliamo poter costruire il nostro gioco.

### Update 04/09
È tutto buggato...
Nemmeno la `findWinCell` sembra funzionare, forse la board sotto non è in una versione buona
dovremmo fare dei test migliori.

Nemmeno la prevent win non sembra funzionare, il giocatore sembra andare mooolto a caso


### Update 06/09
Il nuovo algoritmo per cercare le line-double play è buggata,
considera configurazioni come
OXXX come se fossero dei doppi giochi. 