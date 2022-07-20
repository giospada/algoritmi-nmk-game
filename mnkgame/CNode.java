
package mnkgame;

class CNode<T>{
    private T data;
    CNode<T> next;
    CNode<T> prev;
    CNode(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
    
    public T getData() {
        return data;
    }

}