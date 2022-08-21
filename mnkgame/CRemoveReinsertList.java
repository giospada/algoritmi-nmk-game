package mnkgame;

import java.util.Iterator;

public class CRemoveReinsertList<T> implements Iterable<T> {
    private CNode<T> head;

    public CRemoveReinsertList() {
        head = null;
    }

    public CNode<T> getHead() {
        return head;
    }
    public void pushHead(T data) {
        CNode<T> newNode = new CNode<T>(data);
        newNode.next = head;
        if (head != null)
            head.prev = newNode;
        head = newNode;
    }

    public void remove(CNode<T> t) {
        if (t == head) {
            head = head.next;
            if (head != null)
                head.prev = null;
            return;
        } else {
            t.prev.next = t.next;
            if (t.next != null)
                t.next.prev = t.prev;
        }
    }

    public void reinsert(CNode<T> t) {
        if (t.prev == null) {
            if (t.next != null)
                t.next.prev = t;
            head = t;
        } else {
            t.prev.next = t;
            if (t.next != null)
                t.next.prev = t;
        }
    }

    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            CNode<T> current = head;
            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                T data = current.getData();
                current = current.next;
                return data;
            }
        };
    }
}
