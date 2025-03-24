package uk.ncl.CSC8016.jackbergus.coursework.project3;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ReadWriteMonitorMultiRead<T> { // This implements a waiting system for a specific value to be produced

    private int nr, nw, ww;
    ReentrantLock monitor;
    Condition okToRead, okToWrite;
    T object;

    public ReadWriteMonitorMultiRead() {
        monitor = new ReentrantLock(true);
        okToRead = monitor.newCondition();
        okToWrite = monitor.newCondition();

        nr = nw = ww = 0; // TODO: remove nrwo
        object = null; // TODO: KEEP
    }

    /**
     * The method used by users merely reading the online blog
     * @param hasToReadEvent        If null, then the user is just polling for the event to be read. If previousObject is
     *                              null, it should return the first non-null object value, and otherwise wait.
     *                              If previousObject is not null, then it shall return the first object being different
     *                              from the non-null value in previousObject.
     *
     * @param previousObject        This provides the non-null object to be compared against object whenever hasToReadEvent is
     *                              null.
     *
     * @return                      The intended result, if any. If any waiting condition was interrupted, then the
     *                              method shall always return null.
     */
    public T get(Supplier<T> hasToReadEvent, T previousObject) {
        T result = null;
        return result;
    }

    /**
     *
     * @param objectProducer        The class providing the object, if any
     * @return                      This returns true if the object was inserted (e.g., the objectProducer returned a non-
     *                              null object that was effectively inserted within the list) and false otherwise, thus
     *                              including whether no producer is provided. If the waiting condition was interrupted,
     *                              this method shall return false in any case.
     */
    public boolean set(Supplier<T> objectProducer) {
        boolean condition= false;
        T obj = null;
        return condition;
    }

    public Runnable readerRunnable(int i, final int timesRead, ReadWriteMonitorMultiRead<Integer> monitor) {
        return () -> {
            for (int j = 0; j<timesRead; j++) {
                System.out.println(monitor.get(null, null)+"!"+i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public Runnable writerRunnable(int N, ReadWriteMonitorMultiRead<Integer> monitor) {
        return () -> {
            var r = new Random(0);
            for (int i = 0; i<N; i++) {
                monitor.set(() -> {
                    var value = r.nextInt();
                    System.out.println("Setting:"+value);
                    return value;
                });
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
        var monitor = new ReadWriteMonitorMultiRead<Integer>();
        Thread readers[] = new Thread[5];
        Thread writers[] = new Thread[1];
        for (int i = 0; i<readers.length; i++) {
            readers[i] = new Thread(monitor.readerRunnable(i, 10, monitor));
        }
        for (int i = 0; i<writers.length; i++) {
            writers[i] = new Thread(monitor.writerRunnable(10, monitor));
        }
        for (int i = 0; i<readers.length; i++) {
            readers[i].start();
        }
        System.out.println("Readers have started! Now, waiting 10s for starting the writers...");
        Thread.sleep(10000);
        System.out.println("Now, starting the writers...");
        for (int i = 0; i<writers.length; i++) {
            writers[i].start();
        }
        for (int i = 0; i<writers.length; i++) {
            writers[i].join();
        }
        for (int i = 0; i<readers.length; i++) {
            readers[i].join();
        }
        System.out.println("[Readers reading the same values forever....]");
        System.exit(1);
    }

}