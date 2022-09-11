package mnkgame.time.HeappedBoard;

import java.util.*;

public class Heap<T extends Comparable<T>> {

    private static final int d = 2;
    private T[] heap;
    private int heapSize;

    // Heap constructor with default size
    @SuppressWarnings("unchecked")
    public Heap(int capacity) {
        heapSize = 0;
        heap = (T[]) new Comparable[capacity + 1];
    }

    public int size(){
        return heapSize;
    }

    // is heap empty?
    public boolean isEmpty() {
        return heapSize == 0;
    }

    // is heap full?
    public boolean isFull() {
        return heapSize == heap.length;
    }

    // return parent
    private int parent(int i) {
        return (i - 1) / d;
    }

    // return kth child
    private int kthChild(int i, int k) {
        return d * i + k;
    }

    // insert new element into the heap
    public void insert(T ele) {
        if (isFull())
            throw new NoSuchElementException("Heap is full, No space to insert new element");
        heap[heapSize++] = ele;
        heapifyUp(heapSize - 1);
    }

    // delete an element from the heap at given position
    public T deleteTop() {
        if (isEmpty())
            throw new NoSuchElementException("Heap is empty, No element to delete");
        T min = heap[0];
        heap[0] = heap[heapSize - 1];
        heapSize--;
        heapifyDown(0);
        return min;
    }

    // maintain heap property during insertion
    private void heapifyUp(int i) {
        T temp = heap[i];
        while (i > 0 && temp.compareTo(heap[parent(i)]) > 0) {
            heap[i] = heap[parent(i)];
            i = parent(i);
        }
        heap[i] = temp;
    }

    // maintain heap property during deletion
    private void heapifyDown(int i) {
        int child;
        T temp = heap[i];
        while (kthChild(i, 1) < heapSize) {
            child = minChild(i);
            if (temp.compareTo(heap[child]) < 0) {
                heap[i] = heap[child];
            } else
                break;
            i = child;
        }
        heap[i] = temp;
    }

    private int minChild(int i) {
        int leftChild = kthChild(i, 1);
        if (kthChild(i, 2) < heapSize) {
            int rightChild = kthChild(i, 2);
            return heap[leftChild].compareTo(heap[rightChild]) > 0 ? leftChild : rightChild;
        }
        return leftChild;
    }

    // print the heap
    public void printHeap() {
        System.out.print("nHeap = ");
        for (int i = 0; i < heapSize; i++)
            System.out.print(heap[i] + " ");
        System.out.println();
    }

    // return max from the heap
    public T findMin() {
        if (isEmpty())
            throw new NoSuchElementException("Heap is empty.");
        return heap[0];
    }

/*
public class BinaryHeap<T extends Comparable<T>> {
    T[] heap;
    int size;

    public BinaryHeap(int n) {
        heap = new Comparable[n];
    }

    // build heap in O(n)

    public T getMax(){
        return heap;
    }

    public T removeMin() {
        T removed = heap[0];
        heap[0] = heap[--size];
        down(0);
        return removed;
    }

    public void add(T value) {
        heap[size] = value;
        up(size++);
    }

    void up(int pos) {
        while (pos > 0) {
            int parent = (pos - 1) / 2;
            if (heap[pos].compareTo(heap[parent]) >= 0)
                break;
            swap(pos, parent);
            pos = parent;
        }
    }

    void down(int pos) {
        while (true) {
            int child = 2 * pos + 1;
            if (child >= size)
                break;
            if (child + 1 < size && heap[child + 1].compareTo(heap[child]) < -)
                ++child;
            if (heap[pos] <= heap[child])
                break;
            swap(pos, child);
            pos = child;
        }
    }

    void swap(int i, int j) {
        T t = heap[i];
        heap[i] = heap[j];
        heap[j] = t;
    } 
    */

}