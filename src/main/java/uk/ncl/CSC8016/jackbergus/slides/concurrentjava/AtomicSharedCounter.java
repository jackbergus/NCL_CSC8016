package uk.ncl.CSC8016.jackbergus.slides.concurrentjava;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicSharedCounter {

    public static void main(String[] args) throws InterruptedException {
        final AtomicInteger ai = new AtomicInteger(0);
        Thread[] t = new Thread[2];
        for (int i = 0; i<t.length; i++) t[i] = new Thread(() -> {
           for (int j = 0; j < 200; j++) ai.incrementAndGet();
        });
        for (int i = 0; i<t.length; i++) t[i].start();
        for (int i = 0; i<t.length; i++) t[i].join();
        System.out.println(ai.get());
    }

}
