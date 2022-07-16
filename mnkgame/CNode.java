
package mnkgame;

class CNode<T>{
    private T data;
    CNode next;
    CNode prev;
    CNode(T data){
        this.data = data;
        this.next = null;
        this.prev = null;
    }
    public T getData() {
        return data;
    }

}