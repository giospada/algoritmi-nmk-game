package test;

import org.junit.jupiter.api.BeforeAll;

import mnkgame.cboard.CNode;
import mnkgame.cboard.CRemoveReinsertList;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CLinkedListTest {
    private static CRemoveReinsertList<Integer> list;

    @BeforeAll
    static public void beforeAll() {
        list = new CRemoveReinsertList<Integer>();
        System.out.println("@AfterAll executed");

    }

    @Test
    @DisplayName("should correctly pushHead an element to the list")
    public void shouldCorrectlypushHeadAnElementToTheList() {
        list.pushHead(1);
        assertEquals(Integer.valueOf(1), list.getHead().getData());
    }

    @Test
    @DisplayName("should correctly popHead elements")
    public void shouldCorretlypopHeadElements() {
        list.pushHead(1);
        list.pushHead(2);
        list.pushHead(3);
        list.popHead();
        assertEquals(Integer.valueOf(2), list.getHead().getData());
        list.popHead();
        assertEquals(Integer.valueOf(1), list.getHead().getData());
        list.popHead();
    }

    @Test
    @Disabled
    @DisplayName("should return error when popHeadping empty list")
    public void popHeadEmpty() {
        //assertThrows(NullPointerException.class,list.popHead());
    }

    @Test
    @DisplayName("should pushHead multiple elements to the list")
    public void shouldpushHeadMultipleElementsToTheList() {
        for (int i = 0; i < 10; i++) {
            list.pushHead(i);
        }

        for (int i = 9; i >= 0; i--) {
            assertEquals(Integer.valueOf(i), list.getHead().getData());
            list.popHead();
        }
    }

    @Test
    @DisplayName("should correctly remove the head element")
    public void shouldCorrectlyRemoveAnElement() {
        list.pushHead(1);
        list.pushHead(2);
        list.pushHead(3);
        list.remove(list.getHead());
        assertEquals(Integer.valueOf(2), list.getHead().getData());
    }

    @Test
    @DisplayName("should correctly remove the tail element")
    public void shouldCorrectlyRemoveTheTailElement() {
        list.pushHead(1);
        list.pushHead(2);
        list.pushHead(3);
        list.remove(list.getHead().next.next);
        list.popHead();
        assertEquals(Integer.valueOf(2), list.getHead().getData());
    }

    @Test
    @DisplayName("should correctly remove the middle element")
    public void shouldCorrectlyRemoveTheMiddleElement() {
        list.pushHead(1);
        list.pushHead(2);
        list.pushHead(3);
        list.remove(list.getHead().next);
        assertEquals(Integer.valueOf(3), list.getHead().getData());
        list.popHead();
        assertEquals(Integer.valueOf(1), list.getHead().getData());
    }

    @Test
    @DisplayName("should reinsert the middle element")
    public void shouldReinsertTheMiddleElement() {
        list.pushHead(1);
        list.pushHead(2);
        list.pushHead(3);
        CNode<Integer> toRemove = list.getHead().next;
        list.remove(list.getHead().next);
        list.reinsert(toRemove);

        assertEquals(Integer.valueOf(3), list.getHead().getData());
        list.popHead();
        assertEquals(Integer.valueOf(2), list.getHead().getData());
        list.popHead();
        assertEquals(Integer.valueOf(1), list.getHead().getData());
    }

    @Test
    @Disabled
    @DisplayName("should throw on remove of non-existent element")
    public void shouldThrowOnRemoveOfNonExistentElement() {
        list.remove(new CNode<Integer>(4));
    }

    // Ma se io avessi una linked list del tipo head->1->2->3->4->5->6->7->8->9->null
    // rimovessi 8,9 e provassi a reinserire 8, mi inserisce in automatico anche il 9?
}