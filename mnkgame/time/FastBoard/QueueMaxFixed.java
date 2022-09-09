package mnkgame.time.FastBoard;

public class QueueMaxFixed {

    private QueueFixedInt queue;
    int [] valori;
    QueueMaxFixed(int size){
        queue = new QueueFixedInt(size); 
        valori = new int [size];
        for(int i = 0; i< size ; i++){
            valori[i] = 0;
        }
    }

    public void pop(){
        if(queue.getSize() == 0) throw new RuntimeException("Queue is empty can't pop");
        int val = queue.getFirstEl();
        valori[val]--;
        queue.popFront();
    }

    public void add(int c){
        while(queue.getSize()>0 && queue.getLastEl() < c){
            valori[queue.getLastEl()]--;
            queue.popBack();
            
        }
        queue.addBack(c);
        valori[queue.getLastEl()]++;
    }

    public int getMaxCount(){
        return valori[queue.getFirstEl()];
    }
    public int getMax(){
        return queue.getFirstEl();
    }
    
    public int getSize(){
        return queue.getSize();
    }

}
