package uk.ncl.CSC8016.jackbergus.slides.monitors;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteMonitor<T> {

    private int nr, nw, ww;
    ReentrantLock monitor;
    Condition okToRead, okToWrite;
    T object;

    public ReadWriteMonitor() {
        monitor = new ReentrantLock(true);
        okToRead = monitor.newCondition();
        okToWrite = monitor.newCondition();
        nr = nw = ww = 0;
        object = null;
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }

    public Runnable readerRunnable(int i, ReadWriteMonitor<Integer> monitor) {
        return () -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                monitor.sharedLock();
                System.out.println(monitor.get()+"!"+i);
                monitor.sharedUnlock();

            }
        };
    }

    public Runnable writerRunnable(int pId, int N, ReadWriteMonitor<Integer> monitor) {
        return () -> {
            var r = new Random();
            for (int i = 0; i<N; i++) {
                monitor.exclusiveLock();
                System.out.println((N*pId + i)+" setting from "+pId);
                monitor.set(N*pId + i);
                monitor.exclusiveUnlock();
                try {
                    Thread.sleep(
                            r.nextInt(50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void exclusiveLock() {  // start write
        monitor.lock();
        if ((nw > 0) || (nr > 0)) {
            ww++;
            try {
                okToWrite.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ww--;
        }
        nw++;
        monitor.unlock();
    }

    private void exclusiveUnlock() { // end write
        monitor.lock();
        nw--;
        if (nr == 0) okToWrite.signal();
        okToRead.signal();
        monitor.unlock();
    }

    private void sharedLock() { // start read
        monitor.lock();
        if ((nw > 0 ) || (ww > 0)) try {
            okToRead.await();
        } catch (Exception e) {}
        nr++;
        okToRead.signal();
        monitor.unlock();
    }

    private void sharedUnlock() {
        monitor.lock();
        nr--;
        if (nr == 0)
            okToWrite.signal();
        monitor.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        var monitor = new ReadWriteMonitor<Integer>();
        Thread readers[] = new Thread[5];
        Thread writers[] = new Thread[2];
        for (int i = 0; i<readers.length; i++) {
            readers[i] = new Thread(monitor.readerRunnable(i, monitor));
        }
        for (int i = 0; i<writers.length; i++) {
            writers[i] = new Thread(monitor.writerRunnable(i, 10, monitor));
        }
        for (int i = 0; i<readers.length; i++) {
            readers[i].start();
        }
        for (int i = 0; i<writers.length; i++) {
            writers[i].start();
        }
        for (int i = 0; i<writers.length; i++) {
            writers[i].join();
        }
        System.out.println("[Readers reading the same values forever....]");
        System.exit(1);
    }

}