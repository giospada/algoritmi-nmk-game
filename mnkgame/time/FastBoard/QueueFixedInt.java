package mnkgame.time.FastBoard;

public class QueueFixedInt{
    private int size;
    private int last;
    private int first;
    private int array[];
    private int internalSize;

    QueueFixedInt(int size){
        this.size = size;
        this.array = new int[size];
        this.first = 0;
        this.last = 0;
        this.internalSize = 0;
    }

    public int getLastEl(){
        if (last == 0) return array[size-1];
        else return array[last-1];
    }

    public int getFirstEl(){
        return array[first];
    }

    public void addFront(int i){
        first = (first+1)%size;
        array[first] = i;

        internalSize++;
        if(internalSize>size) throw new RuntimeException("Queue is full");
    }
    public void addBack(int i){
        array[last] = i;
        last = (last+1)%size;

        internalSize++;
        if(internalSize>size) throw new RuntimeException("Queue is full");
    }
    
    public void popFront() {
        if(internalSize == 0) throw new RuntimeException("Queue is empty can't pop front");
        first = (first+1)%size;
        internalSize--;
        
    }
    public void popBack() {
        if(internalSize == 0) throw new RuntimeException("Queue is empty can't pop back");
        last = (last -1 + size)%size; 
        internalSize--;
    }

    public int getSize(){
        return internalSize;
    }
}
