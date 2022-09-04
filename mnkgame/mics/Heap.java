package mnkgame.mics;

import java.lang.reflect.Array;


/**
 * A Max-heap data structure with fixed size.
 * @return
 */
@SuppressWarnings("unchecked") 
public class Heap<T extends Comparable<T>> {
    private T[] heap;
    private int size;

    public Heap(int size) {
        heap = (T[]) Array.newInstance(Heap.class, size);
        this.size = 0;
    }

    public void insert(T cell) {
        heap[size] = cell;
        int i = size;
        size++;
        while (i > 0 && heap[i].compareTo(heap[(i - 1) / 2]) > 0) {
            swap(i, (i - 1) / 2);
            i = (i - 1) / 2;
        }
    }

    public T extractMax() {
        T max = heap[0];
        heap[0] = heap[size - 1];
        size--;
        heapify(0);
        return max;
    }

    public void heapify(int i) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        int max = i;
        if (l < size && heap[l].compareTo(heap[max]) > 0) {
            max = l;
        }
        if (r < size && heap[r].compareTo(heap[max]) > 0) {
            max = r;
        }
        if (max != i) {
            swap(i, max);
            heapify(max);
        }
    }

    private void swap(int i, int j) {
        T tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
