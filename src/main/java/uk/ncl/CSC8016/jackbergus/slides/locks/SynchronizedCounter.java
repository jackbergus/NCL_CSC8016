package uk.ncl.CSC8016.jackbergus.slides.locks;

public class SynchronizedCounter {
    private int c = 0;
    public synchronized void increment() {c++;}
    public synchronized void decrement() {c--;}
    public synchronized int value() { return c; }
    public synchronized void increment(int n) {
        if (n>0) {
            c++;
            increment(n-1);
        }
    }
}
