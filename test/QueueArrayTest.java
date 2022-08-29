package test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class QueueArrayTest {
    public static int[] sizes = {
        100,
        1000,
        10000,
        100000,
        1000000
    };

    public static int max = 1000000; // 10 million

    public static void testQueueSpeed(Queue<Integer> queue) {
        int first = queue.poll();
        queue.add(first);
        while (queue.peek() != first) {
            queue.poll();
            queue.add(first);
        }
    }

    public static void testQueueIntSpeed(MyQueueInt queue) {
        int first = queue.pop();
        queue.push(first);
        while (queue.first() != first) {
            queue.pop();
            queue.push(first);
        }
    }

    private static int[] getFreeCellsAfterAction(int[] oldFreeCells, int actionIdx) {
        int[] freeCells = new int[oldFreeCells.length - 1];
        int i = 0;
        for (int j = 0; j < oldFreeCells.length; j++) {
            if (actionIdx == j)
                continue;
            freeCells[i] = oldFreeCells[j];
            i++;
        }
        return freeCells;
    }

    public static void testArraySpeed(int[] array) {
        // mi ricreo un nuovo array, perché è così che facevo
        int[] newArray = getFreeCellsAfterAction(array, 0);
        for (int i = 0; i < newArray.length; i++) {
            array[i] = newArray[i]; // doo random things
        }
    }

    public static void main2(String[] args) {
        for (int size : sizes) {
            int times = max / size;

            long queueTime = 0;
            long arrayTime = 0; 

            for (int i = 0; i < times; i++) {
                int[] array = new int[size];
                for (int j = 0; j < size; j++) {
                    array[j] = j;
                }
                long start = System.nanoTime();
                testArraySpeed(array);
                long end = System.nanoTime();
                arrayTime += end - start;

                Queue<Integer> queue = new LinkedList<>();
                for (int j : array) {
                    queue.add(j);
                }
                start = System.nanoTime();
                testQueueSpeed(queue);
                end = System.nanoTime();
                queueTime += end - start;
            }
            System.out.println("Size: " + size + " Array time: " + arrayTime / times + " Queue time: " + queueTime / times);
        }
        // Risultati omg...
        // Size: 100 Array time: 242 Queue time: 861
        // Size: 1000 Array time: 2141 Queue time: 7723
        // Size: 10000 Array time: 18490 Queue time: 81163
        // Size: 100000 Array time: 150150 Queue time: 780782
    }

    public static void main(String[] args) {
        for (int size : sizes) {
            int times = max / size;

            long queueListTime = 0;
            long queueDequeTime = 0;
            long myQueueTime = 0;
            long myQueueIntTime = 0;

            long arrayTime = 0; 

            for (int i = 0; i < times; i++) {
                int[] array = new int[size];
                for (int j = 0; j < size; j++) {
                    array[j] = j;
                }
                long start = System.nanoTime();
                testArraySpeed(array);
                long end = System.nanoTime();
                arrayTime += end - start;

                Queue<Integer> queueList = new LinkedList<>();
                Queue<Integer> queueDeque = new ArrayDeque<>();
                Queue<Integer> myQueue = new MyQueue<>(size);
                MyQueueInt myQueueInt = new MyQueueInt(size);

                for (int j : array) {
                    queueList.add(j);
                    queueDeque.add(j);
                    myQueue.add(j);
                    myQueueInt.push(j);
                }
                start = System.nanoTime();
                testQueueSpeed(queueList);
                end = System.nanoTime();
                queueListTime += end - start;

                start = System.nanoTime();
                testQueueSpeed(queueDeque);
                end = System.nanoTime();
                queueDequeTime += end - start;

                start = System.nanoTime();
                testQueueSpeed(myQueue);
                end = System.nanoTime();
                myQueueTime += end - start;

                start = System.nanoTime();
                testQueueIntSpeed(myQueueInt);
                end = System.nanoTime();
                myQueueIntTime += end - start;
            }
            System.out.println("Size: " + size + " \tArray time: " + arrayTime / times + " \t Deque time: " + 
                queueDequeTime / times + " \tList time: " + queueListTime / times + " \t MyQueue time: " + 
                myQueueTime / times + " \t MyQueueInt time: " + myQueueIntTime / times);
        }
    //     Size: 100       Array time: 132          Deque time: 586        List time: 875           MyQueue time: 746       MyQueueInt time: 468
    //     Size: 1000      Array time: 1363         Deque time: 9237       List time: 12292         MyQueue time: 8437      MyQueueInt time: 2323
    //     Size: 10000     Array time: 11337        Deque time: 60834      List time: 58120         MyQueue time: 50636     MyQueueInt time: 19240
    //     Size: 100000    Array time: 435690       Deque time: 434585     List time: 786960        MyQueue time: 447844    MyQueueInt time: 169963
    //     Size: 1000000   Array time: 1694020      Deque time: 7321890    List time: 40074260      MyQueue time: 6352220   MyQueueInt time: 1882260
    // NOTE:
    // Tutti i queue che implementano l'interfaccia sembrano comparabili fra di loro come tempo, mentre la queue che utilizza il tipo primitivo
    // sembra essere due volte più veloci. 
    // nonostante ciò sono l'array sembra essere 4 volte più veloce rispetto a tutti gli altri, nonostante non debba ricreare ogni volta un nuovo array
    }
}
