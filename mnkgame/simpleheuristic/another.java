/*
 *  Copyright (C) 2021 Pietro Di Lena
 *
 *  This file is part of the MNKGame v2.0 software developed for the
 *  students of the course "Algoritmi e Strutture di Dati" first
 *  cycle degree/bachelor in Computer Science, University of Bologna
 *  A.Y. 2020-2021.
 *
 *  MNKGame is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This  is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this file.  If not, see <https://www.gnu.org/licenses/>.
 */

package mnkgame.simpleheuristic;
import mnkgame.MNKCellState;

public class another extends mnkgame.MNKCell {
    private int heuristicValue;

    public another(int i, int j, MNKCellState state) {
        super(i, j, state);
        this.heuristicValue = 0;
    }

    public another(int i, int j) {
        super(i, j);
        this.heuristicValue = 0;
    }

    public int getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(int heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public void addHeuristicValue(int heuristicValue) {
        this.heuristicValue += heuristicValue;
    }
}
