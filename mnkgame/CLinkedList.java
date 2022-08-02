package mnkgame;

public class CLinkedList<T>{

    private CNode<T> head;

    public CLinkedList(){
        head = null;
    }

    public CNode<T> getHead() {
        return head;
    }

    //O(1)
    public void pushHead(T data){
        CNode<T> newNode = new CNode<T>(data);
        newNode.next = head;
        if(head != null)
            head.prev = newNode;
        head = newNode;
    }

    //O(1) 
    public void remove(CNode<T> t){
        if(t == head) {
            head = head.next;
            if(head != null)
                head.prev = null;
            return;
        } else {
            t.prev.next = t.next;
            if(t.next != null)
                t.next.prev = t.prev; 
        }
    }

    public boolean isEmpty(){
        return head==null;
    } 
}