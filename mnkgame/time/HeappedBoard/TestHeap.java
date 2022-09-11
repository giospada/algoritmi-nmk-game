package mnkgame.time.HeappedBoard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;


public class TestHeap {
    
    @Test
    public void testHeap() {
        Heap<Integer> heap = new Heap<>(10);
        heap.insert(10);
        heap.insert(20);
        heap.insert(30);
        heap.insert(25);
        heap.insert(5);
        heap.insert(40);
        heap.insert(35);
        assertEquals(5, heap.findMin());
        heap.deleteTop();
        assertEquals(10, heap.findMin());
        heap.deleteTop();
        assertEquals(20, heap.findMin());
        heap.deleteTop();
        assertEquals(25, heap.findMin());
        heap.insert(15);
        assertEquals(15, heap.findMin());
        heap.insert(5);
        assertEquals(5, heap.findMin());
        heap.insert(10);
        assertEquals(5, heap.findMin());
    }
}
