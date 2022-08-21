package test;
import static org.junit.Assert.assertEquals;

import mnkgame.CLinkedList;
import mnkgame.CNode;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

public class CLinkedListTest {
    private CLinkedList<Integer> list;

    @BeforeAll
    public void beforeAll() {
        list = new CLinkedList<Integer>();
    }

    @Test
    @DisplayName("should correctly push an element to the list")
    public void shouldCorrectlyPushAnElementToTheList() {
        list.push(1);
        assertEquals(Integer.valueOf(1), list.getHead().getData());
    }

    @Test
    @DisplayName("should correctly pop elements")
    public void shouldCorretlyPopElements() {
        list.push(1);
        list.push(2);
        list.push(3);
        assertEquals(Integer.valueOf(3), list.pop());
        assertEquals(Integer.valueOf(2), list.pop());
        assertEquals(Integer.valueOf(1), list.pop());
    }

    @Test(expected = NullPointerException.class)
    @DisplayName("should return error when popping empty list")
    public void popEmpty() {
        list.pop();
    }

    @Test
    @DisplayName("should push multiple elements to the list")
    public void shouldPushMultipleElementsToTheList() {
        for (int i = 0; i < 10; i++) {
            list.push(i);
        }

        for (int i = 9; i >= 0; i--) {
            assertEquals(Integer.valueOf(i), list.getHead().getData());
            list.pop();
        }
    }

    @Test
    @DisplayName("should correctly remove the head element")
    public void shouldCorrectlyRemoveAnElement() {
        list.push(1);
        list.push(2);
        list.push(3);
        list.remove(list.getHead());
        assertEquals(Integer.valueOf(2), list.getHead().getData());
    }

    @Test
    @DisplayName("should correctly remove the tail element")
    public void shouldCorrectlyRemoveTheTailElement() {
        list.push(1);
        list.push(2);
        list.push(3);
        list.remove(list.getHead().next.next);
        list.pop();
        assertEquals(Integer.valueOf(2), list.getHead().getData());
    }

    @Test
    @DisplayName("should correctly remove the middle element")
    public void shouldCorrectlyRemoveTheMiddleElement() {
        list.push(1);
        list.push(2);
        list.push(3);
        list.remove(list.getHead().next);
        assertEquals(Integer.valueOf(3), list.getHead().getData());
        list.pop();
        assertEquals(Integer.valueOf(1), list.getHead().getData());
    }

    @Test
    @DisplayName("should reinsert the middle element")
    public void shouldReinsertTheMiddleElement() {
        list.push(1);
        list.push(2);
        list.push(3);
        CNode<Integer> toRemove = list.getHead().next;
        list.remove(list.getHead().next);
        list.reinsert(toRemove);

        assertEquals(Integer.valueOf(3), list.getHead().getData());
        list.pop();
        assertEquals(Integer.valueOf(2), list.getHead().getData());
        list.pop();
        assertEquals(Integer.valueOf(1), list.getHead().getData());
    }

    @Test(expected = NullPointerException.class)
    @DisplayName("should throw on remove of non-existent element")
    public void shouldThrowOnRemoveOfNonExistentElement() {
        list.remove(new CNode<Integer>(4));
    }

    // Ma se io avessi una linked list del tipo head->1->2->3->4->5->6->7->8->9->null
    // rimovessi 8,9 e provassi a reinserire 8, mi inserisce in automatico anche il 9?
}