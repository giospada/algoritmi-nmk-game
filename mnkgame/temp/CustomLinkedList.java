import javax.swing.Popup;

class CustomLinkedList<T>{
    class CustomLinkedListNode<T>{
        private T data;
        CustomLinkedListNode next;
        CustomLinkedListNode prev;
        CustomLinkedListNode(T data){
            this.data = data;
            this.next = null;
            this.prev = null;
        }
        public T getData() {
            return data;
        }

    }

    private CustomLinkedListNode<T> head;

    public CustomLinkedList(){
        head = null;
    }

    public CustomLinkedListNode<T> getHead() {
        return head;
    }

    public void push(T data){
        CustomLinkedListNode<T> newNode = new CustomLinkedListNode<T>(data);
        newNode.next = head;
        head.prev=newNode;
        head = newNode;
    }
  
    public void remove(Node<T> t){
        if(t==head){
            head = head.next;
            head.prev = null;
            return;
        }else{
            t.prev.next = t.next;
            t.next.prev = t.prev; 
        }
    }

    public void pop(){
        remove(head);
    }
    public bool isEmpty(){
        return head==null;
    } 
}