package uk.ncl.CSC8016.jackbergus.slides.introduction;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Strict2PLSimplified {

    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock(true);
    volatile long A, B;

    public Thread genThread() {
        return new Thread(() -> {
            rwl.writeLock().lock();
            var localA = A; // Some read operation
            A = localA + 3;       // Some write operation
            rwl.readLock().lock();
            var localB = B; // Some read operation
            rwl.readLock().unlock(); // Commit!
            rwl.writeLock().unlock(); // Commit!
        });
    }

    public static void main(String[] args) {
        Strict2PLSimplified ex = new Strict2PLSimplified();
        for (int i = 0; i<1000; i++) {
            Thread t1 = ex.genThread(), t2 = ex.genThread();
            t1.start(); t2.start();
            try {
                t1.join();
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
