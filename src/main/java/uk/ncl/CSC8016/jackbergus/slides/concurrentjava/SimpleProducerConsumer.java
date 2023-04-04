package uk.ncl.CSC8016.jackbergus.slides.concurrentjava;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleProducerConsumer {

    public static void main(String args[]) throws InterruptedException {
        BlockingQueue<Integer> items = new LinkedBlockingQueue<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i<10; i++) {
                try {
                    System.out.println(items.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i<10; i++) {
                items.add(i);
            }
        });
        t1.start(); t2.start();
        t1.join(); t2.join();
    }

}
