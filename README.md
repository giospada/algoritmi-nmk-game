# MNK-GAME
This is the project for a course in the university of Bologna

### Compile
Command-line compile.  In the root directory run::

    ```javac ./mnkgame/*.java```


### MNKGame application:

- Human vs Computer.  In the root directory run:
	
    ```java  mnkgame.MNKGame 3 3 3 mnkgame.RandomPlayer```


- Computer vs Computer. In the root directory run:

    ```java mnkgame.MNKGame 5 5 4 mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer```


### MNKPlayerTester application:

- Output score only:

	```java mnkgame.MNKPlayerTester 5 5 4 mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer```

- Verbose output

	```java mnkgame.MNKPlayerTester 5 5 4 mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer -v```


- Verbose output and customized timeout (1 sec) and number of game repetitions (10 rounds)


	```java mnkgame.MNKPlayerTester 5 5 4 mnkgame.RandomPlayer mnkgame.QuasiRandomPlayer -v -t 1 -r 10```
