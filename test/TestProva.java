package test;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import mnkgame.CLinkedList; 
public class TestProva {
    @Test
    public void testAdd() {
        CLinkedList<Integer> list = new CLinkedList<Integer>();
        list.push(1);
        int pop = list.pop();
        assertEquals(pop, 1);
    }
}