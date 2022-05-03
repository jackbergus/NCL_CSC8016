package uk.ncl.CSC8016.jackbergus.slides.monitors;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReadWriteMonitor_First<T> {

    private int nr, nw;
    ReentrantLock monitor;
    Condition okToRead, okToWrite;
    T object;

    public ReadWriteMonitor_First() {
        monitor = new ReentrantLock();
        okToRead = monitor.newCondition();
        okToWrite = monitor.newCondition();
        nr = nw = 0;
        object = null;
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }

    public Runnable readerRunnable(ReadWriteMonitor_First<Integer> monitor) {
        return () -> {
            var r = new Random();
            while (true) {
                monitor.sharedLock();
                System.out.println( monitor.get());
                monitor.sharedUnlock();
                try {
                    Thread.sleep(
                            r.nextInt(50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public Runnable writerRunnable(int pId, int N, ReadWriteMonitor_First<Integer> monitor) {
        return () -> {
            var r = new Random();
            for (int i = 0; i<N; i++) {
                monitor.exclusiveLock();
                System.out.println((N*pId + i)+" setting...");
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

    private void exclusiveUnlock() {
        monitor.lock();
        nw--;
        okToRead.signal();
        if ((nw == 0) && (nr == 0)) {
            okToWrite.signal();
        }
        monitor.unlock();
    }

    private void exclusiveLock() {
        monitor.lock();
        while ((nw > 0) || (nr > 0)) try {//changed
            okToWrite.await();
        } catch (Exception e) {
        }
        nw++;
        monitor.unlock();
    }

    private void sharedUnlock() {
        monitor.lock();
        nr--;
        if (nr == 0) okToWrite.signal();
        monitor.unlock();
    }

    private void sharedLock() {
        monitor.lock();
        while (nw > 0 ) try {
            okToRead.await();
        } catch (Exception e) {}
        nr++;
        okToRead.signal();
        monitor.unlock();
    }

    public static void main(String[] args) throws InterruptedException {
        var monitor = new ReadWriteMonitor_First<Integer>();
        Thread readers[] = new Thread[5];
        Thread writers[] = new Thread[2];
        for (int i = 0; i<readers.length; i++) {
            readers[i] = new Thread(monitor.readerRunnable(monitor));
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