package mnkgame.time.FastBoard;

@SuppressWarnings("unchecked") 
public class QueueFixed<T> {
    private int size;
    private int last;
    private int first;
    private T array[];
    private int internalSize;

    QueueFixed(int size){
        this.size = size;
        this.array = (T[]) new Object[size];
        this.first = 0;
        this.last = 1;
        this.internalSize = 0;
    }

    public T getLastEl(){
        if (last == 0) return array[size-1];
        else return array[last-1];
    }
    public T getFirstEl(){
        return array[first];
    }

    public void add(T i){
        array[last] = i;
        last = (last+1)%size;

        internalSize++;
        if(internalSize>size) throw new RuntimeException("Queue is full");
    }
    public void  popFront() {
        if(internalSize == 0) throw new RuntimeException("Queue is empty can't pop front");
        first = (first+1)%size;
        internalSize--;
        
    }
    public void  popBack() {
        if(internalSize == 0) throw new RuntimeException("Queue is empty can't pop back");
        last = (last -1 + size)%size; 
        internalSize--;
    }
}


