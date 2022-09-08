package test;

import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.TreeMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

/**
 * This class is used too check the behaviour of some datastructures in the standard library.
 */
public class TestStdDataStructures {
    @Test
    @DisplayName("Test if the hashset updates his values when class is changed")
    @Disabled("The standard hashset does not update his values dinamically when it's updated in this way")
    public void testHashMap() {
        TreeMap<Prova, Integer> treeMap = new TreeMap<>();
        Prova prova = new Prova(1);
        Prova prova2 = new Prova(2);
        treeMap.put(prova, 1);
        treeMap.put(prova2, 2);
        assert treeMap.firstKey().n == 1;
        prova.n = 3;

        assert treeMap.firstKey().n == 2;
    }

    @Test
    @DisplayName("Test whether the priority queue can remove the element if key changed")
    public void testPriorityQueue() {
        PriorityQueue<Prova> priorityQueue = new PriorityQueue<>();
        Prova prova = new Prova(1);
        Prova prova2 = new Prova(2);
        priorityQueue.add(prova);
        priorityQueue.add(prova2);
        assert priorityQueue.peek().n == 1;
        prova.n = 3;
        assert priorityQueue.peek().n == 3;  // ora è in uno stato falso

        assert priorityQueue.remove(prova);  // remove should return true if it has removed
        assert priorityQueue.poll().n == 2;
        assert priorityQueue.size() == 0;
    }
}

class Prova implements Comparable<Prova> {
    public int n;
    Prova(int n) {
        this.n = n;
    }
    @Override
    public int compareTo(Prova o) {
        return n - o.n;        
    }
}