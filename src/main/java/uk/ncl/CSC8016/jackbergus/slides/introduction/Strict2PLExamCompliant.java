package uk.ncl.CSC8016.jackbergus.slides.introduction;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Strict2PLExamCompliant {

    final ReentrantReadWriteLock rwlA = new ReentrantReadWriteLock();
    final ReentrantReadWriteLock rwlB = new ReentrantReadWriteLock();
    volatile long A, B;

    public Thread generateThread(int i, boolean op) {
        return new Thread(() -> {
           rwlA.writeLock().lock();
           var localA = A;
           A = op ? (localA + i): (localA * i);
           rwlB.readLock().lock();
           var localB = B;
           // commit!
           rwlB.readLock().unlock();
           rwlA.writeLock().unlock();
        });
    }

    public static void main(String[] args) throws InterruptedException {
        var global = new Strict2PLExamCompliant();
        for (int i = 0; i<100000; i++) {
            global.A = 0; global.B = 0;
            Thread t1 = global.generateThread(10, true), t2 = global.generateThread(25, false);
            t1.start(); t2.start();
            t1.join(); t2.join();
            System.out.println(global.A+" vs "+global.B);
        }
    }

}
