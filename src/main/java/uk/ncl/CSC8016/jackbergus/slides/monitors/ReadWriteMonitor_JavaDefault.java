package uk.ncl.CSC8016.jackbergus.slides.monitors;

import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteMonitor_JavaDefault<T> {

    final ReentrantReadWriteLock.ReadLock sharedLock;
    final ReentrantReadWriteLock.WriteLock exclusiveLock;
    private int nr, nw, ww;
    ReentrantReadWriteLock monitor;
    T object;

    public ReadWriteMonitor_JavaDefault() {
        monitor = new ReentrantReadWriteLock(true);
        sharedLock = monitor.readLock();
        exclusiveLock = monitor.writeLock();
        object = null;
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }

    public Runnable readerRunnable(ReadWriteMonitor_JavaDefault<Integer> monitor) {
        return () -> {
            while (true) {
                sharedLock.lock();
                System.out.println(monitor.get()+"!");
                sharedLock.unlock();
            }
        };
    }

    public Runnable writerRunnable(int pId, int N, ReadWriteMonitor_JavaDefault<Integer> monitor) {
        return () -> {
            var r = new Random();
            for (int i = 0; i<N; i++) {
                exclusiveLock.lock();
                System.out.println((N*pId + i)+" setting...");
                monitor.set(N*pId + i);
                exclusiveLock.unlock();
                try {
                    Thread.sleep(
                            r.nextInt(50));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        var monitor = new ReadWriteMonitor_JavaDefault<Integer>();
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
        System.out.println("[Readers reading the same values forever: quitting!...]");
        System.exit(1);
    }

}