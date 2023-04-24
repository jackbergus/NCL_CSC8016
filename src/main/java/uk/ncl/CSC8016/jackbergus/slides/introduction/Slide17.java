package uk.ncl.CSC8016.jackbergus.slides.introduction;

import java.util.concurrent.locks.ReentrantLock;

public class Slide17 {
        public static void main(String[] args) throws Exception {
            boolean lastExample = false;
            boolean doWait = false;
            for (int i = 0; i<1000; i++ ) {
                // Execrise:
                // 1) What happens if I replace this with a read lock from the same ReadWriteLock?
                // 2) What happens if I replace this with a write lock  from the same ReadWriteLock?
                var rl = new ReentrantLock(true);
                System.out.println("Run #"+i);
                var t1 = new Thread(() -> {
                    if (lastExample) rl.lock();
                    try {
                        System.out.println("A");
                        if (doWait) Thread.sleep(100);
                        System.out.println("B");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (lastExample) rl.unlock();
                    }
                });
                var t2 = new Thread(() -> {
                    if (lastExample) rl.lock();
                    try {
                        System.out.println("1");
                        if (doWait) Thread.sleep(100);
                        System.out.println("2");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (lastExample) rl.unlock();
                    }
                });
                t1.start(); t2.start();
                t1.join(); t2.join();
            }
        }
}
