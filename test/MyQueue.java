package test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

// la mia implementazione di una queue dinamica che si basa su array, per vedere quanto sia veloce...
@SuppressWarnings("unchecked")
public class MyQueue<T> implements Queue<T> {
    private int end_;
    private int start_;
    private int size_;
    private int realSize_;
    private T[] data_;

    private int _getHigherPowerOfTwo(int n) {
        int x = 1;
        while (x <= size_) {
            x <<= 1;
        }
        return x;
    }

    private void _resize(int size) {
        int newSize = _getHigherPowerOfTwo(size);
        int minSize = size_ < newSize ? size_ : newSize;
        T[] new_space = (T[]) new Object[newSize];  // unckeched warning
        for (int i = start_, j = 0; j < minSize; i++, j++) {
            if (i == realSize_) i -= realSize_;
            new_space[j] = data_[i];
        }
        data_ = new_space;
        realSize_ = newSize;
        start_ = 0;
        end_ = size_;
    }

    public MyQueue(int size) {
        end_ = 0;
        start_ = 0;
        size_ = 0;
        realSize_ = _getHigherPowerOfTwo(size);
        data_ = (T[]) new Object[realSize_];
    }

    public MyQueue() {
        this(0);
    }

    public int size() {
        return size_;
    }

    public boolean isEmpty() {
        return size_ == 0;
    }

    @Override
    public boolean add(T element) {
        if (size_ == realSize_) {
            _resize(size_);
        }
        data_[end_] = element;
        end_ += 1;
        size_ += 1;
        if (end_ == realSize_) end_ -= realSize_;
        return true;
    }

    public T last() {
        return data_[end_ - 1];
    }

    @Override
    public T peek() {
        return data_[start_];
    }

    @Override
    public T poll() {
        if (isEmpty()) throw new RuntimeException("Queue is empty");

        if (size_ == realSize_ / 4) {
            _resize(size_);
        }

        T element = data_[start_];
        start_ += 1;
        size_ -= 1;
        if (start_ == realSize_) start_ -= realSize_;
        return element;
    }

    public T at(int index) {
        return data_[(start_ + index) % realSize_];
    }

    @Override
    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean offer(T e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public T remove() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public T element() {
        // TODO Auto-generated method stub
        return null;
    }
};
