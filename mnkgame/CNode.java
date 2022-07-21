
package mnkgame;

public class CNode<T>{
    public CNode<T> next; // TODO(team): discutere se ha senso metterle publiche
    public CNode<T> prev;
    private T data;
    public CNode(T data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
    
    public T getData() {
        return data;
    }

}