package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {

    private static class ListNode<T> {
        public T item;
        public ListNode prev;
        public ListNode next;

        public ListNode(T item) {
            this(item, null, null);
        }

        public ListNode(T item, ListNode prev, ListNode next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    private int size;
    private ListNode sentinel;

    public LinkedListDeque() {
        size = 0;
        sentinel = new ListNode(61); // The first node in the List is sentinel.next
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public void addFirst(T item) {

        ListNode<T> newNode = new ListNode(item, sentinel, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size++;

    }

    @Override
    public void addLast(T item) {

        ListNode<T> newNode = new ListNode(item, sentinel.prev, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size++;

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (size == 0) {
            return;
        }
        ListNode<T> p = sentinel.next;
        int count = 0;
        while (count < size) {
            System.out.println(p.item);
            p = p.next;
            count++;
        }

    }

    @Override
    public T removeFirst() {
        return delete(0);
    }

    @Override
    public T removeLast() {
        return delete(size - 1);
    }

    private T delete(int index) {
        if (size == 0 || index < 0 || index > size - 1) {
            return null;
        }
        ListNode prev = sentinel;
        ListNode p = sentinel.next;
        while (index > 0) {
            prev = p;
            p = p.next;
            --index;
        }
        prev.next = p.next;
        p.next.prev = prev;
        size--;
        return (T) p.item;
    }

    @Override
    public T get(int index) {
        if (size == 0 || index < 0 || index > size - 1) {
            return null;
        }
        ListNode<T> p = sentinel.next;
        while (index > 0) {
            p = p.next;
            index--;
        }
            return p.item;
    }

    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private int position;

        public LinkedListDequeIterator() {
            position = 0;
        }

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public T next() {
            ListNode<T> p = sentinel.next;
            if (hasNext()) {
                T returnItem = p.item;
                p = p.next;
                return returnItem;
            }
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LinkedListDeque)) {
            return false;
        }

        LinkedListDeque<T> compared = (LinkedListDeque<T>) o;
        if (this.size == compared.size && this.sentinel.equals(compared.sentinel)) {
            return true;
        }
        return false;

    }

    public T getRecursive(int index) {
        if (size == 0 || index < 0 || index > size - 1) {
            return null;
        }
        ListNode<T> p = recursive(sentinel, index);
        return p.item;

    }

    private ListNode recursive(ListNode node, int index) {
        if (index < 0) {
            return node;
        }
        return recursive(node.next, index - 1);
    }

    public static void main (String[] args) {
        LinkedListDeque<Integer> a = new LinkedListDeque<Integer>();
        a.addFirst(1);
        a.addLast(2);
        a.addLast(3);

        System.out.println(a.get(1));
        System.out.println(a.getRecursive(1));

    }

}

