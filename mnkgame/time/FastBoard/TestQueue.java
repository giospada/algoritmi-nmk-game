package mnkgame.time.FastBoard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class TestQueue {
    @Test
    public void testMax() {
        QueueMaxFixed q = new QueueMaxFixed(10);
        q.add(1);
        q.add(2);
        q.add(3);
        assertEquals(q.getMax(), 3);
        assertEquals( q.getMaxCount() , 1);
        q.add(3);
        assertEquals(q.getMaxCount(), 2);
        assertEquals(q.getMax(), 3);
        q.add(2);
        assertEquals(q.getMaxCount(), 2);
        assertEquals(q.getMax(), 3);
        assertEquals(q.getSize(), 3);
        q.pop();
        assertEquals(q.getMaxCount(), 1);
        assertEquals(q.getMax(), 3);
        assertEquals( q.getSize(), 2);
        q.pop();
        assertEquals(q.getMaxCount(), 1);
        assertEquals(q.getMax(), 2);
        assertEquals(q.getSize(), 1);
    }


    @Test
    public void testQueueAddAndPop() {
        QueueFixedInt q = new QueueFixedInt(10);
        q.addBack(1);
        q.addBack(2);
        q.addBack(3);
        q.addBack(4);
        for (int i = 1; i <= 4; i++) {
            assertEquals(i, q.getFirstEl());
            q.popFront();
        }
    }
}
