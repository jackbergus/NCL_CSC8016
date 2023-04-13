package uk.ncl.CSC8016.jackbergus.slides.concurrentjava;

import java.util.concurrent.*;

public class SimpleProducerConsumer {

    public static void main(String args[]) throws InterruptedException {
        var items = new LinkedBlockingQueue<Integer>();
        final int max_gen_elements = 10;
        Thread consumer = new Thread(() -> {
            for (int i = 0; i<max_gen_elements; i++) {
                try {
                    System.out.println(items.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread producer = new Thread(() -> {
          for (int i = 0; i<max_gen_elements; i++) {
              System.out.println("Producing "+i);
              items.add(i);
          }
        });
        consumer.start(); producer.start();
        consumer.join(); producer.join();
    }

}
