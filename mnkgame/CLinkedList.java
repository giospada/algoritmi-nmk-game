package mnkgame;

import javax.swing.Popup;

class CLinkedList<T>{

    private CNode<T> head;

    public CLinkedList(){
        head = null;
    }

    public CNode<T> getHead() {
        return head;
    }

    //O(1)
    public void push(T data){
        CNode<T> newNode = new CNode<T>(data);
        newNode.next = head;
        head.prev=newNode;
        head = newNode;
    }

    //O(1) 
    public void remove(CNode<T> t){
        if(t==head){
            head = head.next;
            head.prev = null;
            return;
        }else{
            t.prev.next = t.next;
            t.next.prev = t.prev; 
        }
    }

    //O(1)
    public T pop(){
        T data = head.getData();
        head = head.next;
        head.prev = null;
        return data;
    }

    public boolean isEmpty(){
        return head==null;
    } 
}