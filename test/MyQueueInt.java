package test;

// la mia implementazione di una queue dinamica che si basa su array, per vedere quanto sia veloce...
// è solo un int
public class MyQueueInt {
    private int end_;
    private int start_;
    private int size_;
    private int realSize_;
    private int[] data_;

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
        int[] new_space = new int[newSize];  // unckeched warning
        for (int i = start_, j = 0; j < minSize; i++, j++) {
            if (i == realSize_) i -= realSize_;
            new_space[j] = data_[i];
        }
        data_ = new_space;
        realSize_ = newSize;
        start_ = 0;
        end_ = size_;
    }

    public MyQueueInt(int size) {
        end_ = 0;
        start_ = 0;
        size_ = 0;
        realSize_ = _getHigherPowerOfTwo(size);
        data_ = new int[realSize_];
    }

    public MyQueueInt() {
        this(0);
    }

    public int size() {
        return size_;
    }

    public boolean isEmpty() {
        return size_ == 0;
    }

    public void push(int element) {
        if (size_ == realSize_) {
            _resize(size_);
        }
        data_[end_] = element;
        end_ += 1;
        size_ += 1;
        if (end_ == realSize_) end_ -= realSize_;
    }

    public int last() {
        return data_[end_ - 1];
    }

    public int first() {
        return data_[start_];
    }

    public int pop() {
        if (isEmpty()) throw new RuntimeException("Queue is empty");

        if (size_ == realSize_ / 4) {
            _resize(size_);
        }

        int element = data_[start_];
        start_ += 1;
        size_ -= 1;
        if (start_ == realSize_) start_ -= realSize_;
        return element;
    }

    public int at(int index) {
        return data_[(start_ + index) % realSize_];
    }
};
