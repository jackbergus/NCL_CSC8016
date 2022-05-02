package uk.ncl.CSC8016.jackbergus.slides.semaphores;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<T> {
    Queue<T> message = new LinkedList<>();
    Semaphore empty, full, mutex;
    int size;

    BoundedBuffer(int capacity) {
        size = capacity;
        mutex = new Semaphore(1);
        empty = new Semaphore(size);
        full = new Semaphore(0);
    }

    public void put(T m) {
        try {
            empty.acquire();
            mutex.acquire();
            message.add(m);
            mutex.release();
            full.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public T get() {
        T val = null;
        try {
            full.acquire();
            mutex.acquire();
            val = message.remove();
            mutex.release();
            empty.release();
        }catch (InterruptedException e) {

        }
        return val;
    }

    public static int produce(int max, int min) {
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }

    public static void main(String args[]) {
        var buffer = new BoundedBuffer<Integer>(1000);
        var producer = new Thread(() -> {
            while (true) {
                var val = produce(100, 0);
                System.out.println("Putting: "+val);
                buffer.put(val);
            }
        });
        var consumer = new Thread(() -> {
            while (true) {
                var val = buffer.get();
                System.out.println(val);
            }
        });
        producer.start();
        consumer.start();
        // And they lived happily ever after...
    }

}
