package mnkgame.cboard;

import java.lang.Iterable;
import java.util.Iterator;

public class CStack<T> implements Iterable<T> {
    private CNode<T> head;
    public int length;

    public CStack() {
        head = null;
        length = 0;
    }

    public void push(T data) {
        CNode<T> newNode = new CNode<T>(data);
        newNode.prev = head;
        head = newNode;
        length++;
    }

    public T pop() {
        if (isEmpty())
            return null;
        T data = head.getData();
        head = head.prev;
        length--;
        return data;
    }
    // emtpy
    public boolean isEmpty() {
        return head == null;
    }
    // top
    public T top() {
        return head.getData();
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            CNode<T> current = head;
            public boolean hasNext() {
                return current != null;
            }
            public T next() {
                T data = current.getData();
                current = current.prev;
                return data;
            }
        };
    }
}
