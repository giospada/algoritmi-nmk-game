#! /usr/bin/python3

"""
Questo modulo aiuta a compilare ed eseguire tutti i file, senza stare
ad utilizzare l'interfaccia ultraverbosa di java.
"""

import os
import sys
import argparse

def get_src_dir():
    return os.path.join(os.getcwd(), "mnkgame")

def remove_all_class():
    """
    Rimuove tutti i file .class
    """
    source = get_src_dir()
    for file in os.listdir(source):
        if file.endswith(".class"):
            os.remove(os.path.join("mnkgame", file))

def compile_all_java():
    """
    Compila tutti i file .java
    NON MI WORKA SU WINDOWS BOH
    """
    source = get_src_dir()
    for file in os.listdir(source):
        if file.endswith(".java"):
            os.system(os.path.join("mnkgame", file))

def name_to_classname(name):
    """
    Converte un nome di file in un nome del file
    """
    if name == 'random':
        return 'RandomPlayer'
    elif name == 'qrandom':
        return 'QuasiRandomPlayer'
    elif name == 'iterative':
        return 'IterativeDeepeningPlayer'
    elif name == 'minimax':
        return 'MinimaxPlayer'
    elif name == 'boardminimax':
        return 'BoardMinimaxPlayer'
    else:
        raise Exception('Invalid player name')

def make_command(args):
    if len(args.board) != 3:
        raise Exception('Invalid board size should be 3')

    player1 = name_to_classname(args.player1)
    player2 = name_to_classname(args.player2)
    if args.human:
        return f"java mnkgame.MNKGame {args.board[0]} {args.board[1]} {args.board[2]} mnkgame.{player1}"
    else:
        verbose = "-v" if args.verbose else ""
        return f"java mnkgame.MNKPlayerTester {args.board[0]} {args.board[1]} {args.board[2]} mnkgame.{player1} mnkgame.{player2} -r {args.rounds} -t {args.time} {verbose}"

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='aiuta ad eseguire i file in modo più semplice')
    parser.add_argument('-d', '--delete', help='elimina tutti i file class', action='store_true')
    parser.add_argument('-c', '--compile', help='compila tutti i file .java', action='store_true')
    parser.add_argument('-b', '--board', help='size della board', nargs='+', type=int)
    parser.add_argument('-t', '--time', help='il tempo massimo per mossa', type=int, default=10)
    parser.add_argument('-r', '--rounds', help='il numero di ripetizioni a partite', type=int, default=1)
    parser.add_argument('-v', '--verbose', help='verbose', action='store_true')
    parser.add_argument('--human', help='human player', action='store_true')
    parser.add_argument('-p1', '--player1', help='player 1', default='qrandom')
    parser.add_argument('-p2', '--player2', help='player 2', default='qrandom')
    args = parser.parse_args()

    if args.delete and args.compile:
        raise Exception('You can\'t delete and compile at the same time')

    if args.delete:
        remove_all_class()
        sys.exit(0)

    if args.compile:
        compile_all_java()
        sys.exit(0)

    command = make_command(args)
    print(f"executing: {command}")
    os.system(command)