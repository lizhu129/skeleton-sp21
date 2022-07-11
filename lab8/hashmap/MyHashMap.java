package hashmap;

import javax.swing.text.html.HTMLDocument;
import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @LiZhu YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {


    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int m; // Hash Table size
    private int n; // Number of Key Value pairs
    private double maxLoad;
    private static final int INIT_CAPACITY = 16;
    private static final double MAX_LOAD = 0.75;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(INIT_CAPACITY);
    }

    public MyHashMap(int initialSize) {
        this.n = 0;
        this.m = initialSize;
        this.maxLoad = MAX_LOAD;
        this.buckets = this.createTable(initialSize);
        for (int i = 0; i < this.m; i++) {
            buckets[i] = this.createBucket();
        }
    }


    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.n = 0;
        this.m = initialSize;
        this.maxLoad = maxLoad;
        this.buckets = this.createTable(initialSize);
        for (int i = 0; i < this.m; i++) {
            buckets[i] = this.createBucket();
        }
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    private void resize(int newSize) {
        MyHashMap<K, V> tmp = new MyHashMap<>(newSize);
        for (int i = 0; i < this.m; i++) {
            for (Node node : this.buckets[i]) {
                tmp.put(node.key, node.value);
            }
        }
        this.m = tmp.m;
        this.n = tmp.n;
        this.buckets = tmp.buckets;

    }
    @Override
    public void clear() {
        if (this != null && this.n > 0) {
            this.n = 0;
            for (int i = 0; i < this.m; i++) {
                this.buckets[i].clear();
            }
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        int i = hash(key);
        for (Node node : this.buckets[i]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    private int hash(K key) {
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12) ^ (h >>> 7) ^ (h >>> 4);
        return h & (this.m-1);
    }

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");

        // Resize if load factor exceeds maxLoad
        double loadFactor = (double) this.n / this.m;
        if (loadFactor > this.maxLoad) {
            this.resize(2 * m);
        }
        int i = hash(key);

        if ( !containsKey(key)) {
            this.buckets[i].add(new Node(key, value));
            this.n++;
        } else {

            for (Node x : this.buckets[i]) {
                if (x.key == key) {
                    x.value = value;
                }
            }
        }

    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (int i = 0; i < this.m; i++) {
            for (Node node : this.buckets[i]) {
                keySet.add(node.key);
            }
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");

        int i = hash(key);
        Node newNode = null;
        for (Node node : this.buckets[i]) {
            if (node.key == key) {
                newNode = node;
                this.buckets[i].remove(node);
                n--;
            }
        }

        if (m > INIT_CAPACITY && (double) this.n / this.m < maxLoad) {
            this.resize(m/2);
        }
        return newNode.value;
    }


    @Override
    public V remove(K key, V value) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        Node newNode = new Node(key, value);
        int i = hash(key);
        if (this.buckets[i].contains(newNode)) {
            remove(key);
            n--;

            if (m > INIT_CAPACITY && (double) this.n / this.m < maxLoad) {
                this.resize(m / 2);
            }

            return newNode.value;
        }
        return null;

    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        MyHashMap<String, Integer> b = new MyHashMap<>();
        for (int i = 0; i < 2; i++) {
            b.put("hi" + i, 1);
            System.out.println(b.get("hi" + i));
        }

        b.put("hello", 1);
        System.out.println(b.get("hello"));



    }



}
