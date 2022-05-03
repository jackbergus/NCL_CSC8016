package uk.ncl.CSC8016.jackbergus.slides.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockCounter {
    private int c;
    Lock lock;

    public ReentrantLockCounter() {
        c = 0;
        lock = new ReentrantLock(true);
    }
    public void increment() {
        lock.lock();
        try {
            c++;
        } finally {
            lock.unlock();
        }
    }
    public  void decrement() {
        lock.lock();
        try {
            c--;
        } finally {
            lock.unlock();
        }
    }
    public  int value() {
        int copy;
        lock.lock();
        try {
            copy = c;
        } finally {
            lock.unlock();
        }
        return copy;
    }
    public  void increment(int n) {
        lock.lock();
        try {
            if (n>0) {
                c++;
                increment(n-1);
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        ReentrantLockCounter rlc = new ReentrantLockCounter();
        var t1 = new Thread(() -> {
            rlc.increment(200);
            System.out.println(rlc.value()+"! [1]");
        });
        var t2 = new Thread(() -> {
            System.out.println("I got "+rlc.value());
        });
        t1.start(); t2.start();
        t1.join(); t2.join();
    }
}
