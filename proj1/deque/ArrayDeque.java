package deque;

public class ArrayDeque<T> implements Deque<T> {

    private T[] a = (T[]) (new Object[8]);
    private int size;
    private int nextFirst;
    private int nextLast;
    private int x = 4;

    private double resizeFactor = 2.0;



    public ArrayDeque() {
        size = 0;
        nextFirst = x;
        nextLast = x + 1;
    }

    private void resize(double a) {
        int newLength = (int) (this.a.length * a);
        T[] v1 = (T[]) (new Object[newLength]);
        int size1 = this.a.length - this.nextFirst - 1;
        if (this.nextFirst > this.nextLast) {
            if (size1 == 0) {
                System.arraycopy(this.a, 0, v1, 0, size);
                this.a = v1;
                this.nextFirst = newLength - 1;
            }
            System.arraycopy(this.a, this.nextFirst + 1, v1, newLength - size1, size1);
            this.nextFirst = newLength - size1 - 1;
            System.arraycopy(this.a, 0, v1, 0, this.nextLast);
            this.a = v1;
        } else {
            System.arraycopy(this.a, this.nextFirst + 1, v1,this.nextFirst + 1, size);
            this.a = v1;
        }
    }

    @Override
    public void addFirst(T item) {
        if (this.size == this.a.length - 2) {
            resize(resizeFactor);
        }
        this.a[nextFirst] = item;
        size++;
        if (this.nextFirst == 0) {
            this.nextFirst = this.a.length - 1;
        } else {
            this.nextFirst--;
        }
    }

    @Override
    public void addLast(T item) {
        if (this.size == this.a.length - 2) {
            resize(resizeFactor);
        }
        this.a[nextLast] = item;
        size++;
        if (this.nextLast == this.a.length - 1) {
            this.nextLast = 0;
        } else {
            this.nextLast++;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if(isEmpty()) {
            System.out.println("Empty array");
        }

        if (this.nextFirst == this.nextLast) {
            System.out.println("Array has size issue");
        } else if (this.nextFirst > this.nextLast) {
            if (this.nextFirst == this.a.length - 1) {
                for (int i = 0; i < size; i++) {
                    System.out.print(this.a[i] + " ");
                }
            }
            for (int i = this.nextFirst + 1; i < this.a.length; i++) {
                System.out.print(this.a[i] + " ");
            }
            for (int i = 0; i < this.nextLast; i++) {
                System.out.print(this.a[i] + " ");
            }
        } else {
            for (int i = this.nextFirst + 1; i < this.nextLast; i++) {
                System.out.print(this.a[i] + " ");
            }
        }
        System.out.println();


    }

    @Override
    public T removeFirst() {

        if (isEmpty()) {
            return null;
        }

        if (this.nextFirst == this.a.length - 1) {
            this.nextFirst = 0;
        } else {
            this.nextFirst++;
        }

        T t = this.a[this.nextFirst];
        this.a[this.nextFirst] = null;
        size--;

        double usage = (double) this.size / (double) this.a.length;
        if (this.a.length >= 16 && usage < 0.25D) {
            resize(1/resizeFactor);
        }
        return t;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        if (this.nextLast == 0) {
            this.nextLast = this.a.length - 1;
        } else {
            this.nextLast--;
        }

        T t = this.a[this.nextLast];
        this.a[this.nextLast] = null;
        size--;

        double usage = (double) this.size / (double) this.a.length;
        if (this.a.length >= 16 && usage < 0.25D) {
            resize(1/resizeFactor);
        }
        return t;
    }

    @Override
    public T get(int index) {

        if(isEmpty()) {
            System.out.println("Empty array");
        }

        if (this.nextFirst == this.nextLast) {
            System.out.println("Array has size issue");
        } else if (this.nextFirst > this.nextLast) {
            if (this.nextFirst == this.a.length - 1) {
                for (int i = 0; i < size; i++) {
                    return this.a[i];
                }
            }
            int size1 = this.a.length - this.nextFirst - 1;
            if (index <= size1) {
                return a[this.nextFirst + 1 + index];
            } else {
                return a[index - size1 - 1];
            }

        }
        return a[this.nextFirst + 1 + index];

        }

    public static void main(String[] args) {
        ArrayDeque<String> a = new ArrayDeque<>();
        a.addFirst("hello");
        a.addLast("hi");
        a.addLast("hey");
        a.addLast("okay");
        a.addLast("not okay");
        a.addLast("CS61B");
        a.addLast("CS50");
        a.addLast("wow");
        a.addLast("alright");
        a.addFirst("haha");

        System.out.println(a.get(0));
        System.out.println(a.get(1));

        a.printDeque();

        a.removeLast();
        a.removeLast();
        a.removeLast();

        a.printDeque();

        a.removeFirst();
        a.removeFirst();
        a.removeFirst();
        a.removeFirst();
        a.removeFirst();
        a.removeFirst();

        a.printDeque();

        a.removeFirst();
        a.printDeque();

    }
}


