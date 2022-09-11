#! /usr/bin/python3

"""
Questo modulo converte l'output verboso di Player tester 
in codice per settare una cella

esempio di input:
Player 1 (Iterative deepening) -> [1,1]
Player 2 (R4nd0m++) -> [0,1]
Player 1 (Iterative deepening) -> [2,1]
Player 2 (R4nd0m++) -> [3,1]
Player 1 (Iterative deepening) -> [3,2]
Player 2 (R4nd0m++) -> [1,0]
Player 1 (Iterative deepening) -> [1,2]
Player 2 (R4nd0m++) -> [3,0]
Player 1 (Iterative deepening) -> [2,0]
Player 2 (R4nd0m++) -> [2,2]
Player 1 (Iterative deepening) -> [0,2]

output:
B.markCell(1, 1);
B.markCell(0, 1);
B.markCell(2, 1);
B.markCell(3, 1);
B.markCell(3, 2);
B.markCell(1, 0);
B.markCell(1, 2);
B.markCell(3, 0);
B.markCell(2, 0);
B.markCell(2, 2);
B.markCell(0, 2);

e puoi andare ad incollare questo per debug

"""
import argparse
def make_line(inp: list[str], name = "B"):
    # una coppia di interi in input
    return f" new MNKCell({inp[0]}, {inp[1]});\n"

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-n", "--name", default="B")
    global args
    args = parser.parse_args() 

    buffer = ''
    buffer2 = ''
    inp = input().strip()

    xInput = []
    yInput = []

    i = 0
    while inp != "":
        inp = inp.split()[-1]
        xInput.append(eval(inp)[0])
        yInput.append(eval(inp)[1])

        if i % 2 == 0:
            buffer += make_line(eval(inp), args.name)

        i += 1
        buffer2 += make_line(eval(inp), "Board")

        inp = input().strip()
    
    print("let xMosse = {};".format(xInput))
    print("let yMosse = {};".format(yInput))
    print(f"let lenMosse = {len(yInput)};")

    print(buffer)

if __name__ == "__main__":
    main()