package uk.ncl.CSC8016.jackbergus.slides.monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class HelloWorldMonitor {
    private Lock monitor;
    private Condition variable;
    private int counter, waiting;

    public HelloWorldMonitor() {
        monitor = new ReentrantLock(true);
        variable = monitor.newCondition();
        counter = waiting = 0;
    }

    public void hello_world(final int i) {
        monitor.lock();
        counter++;
        if (counter < 10) {
            try {
                waiting++;
                variable.await();
            } catch (InterruptedException e) {}
            waiting--;
        }
        if (waiting > 0) {
            variable.signal();
        }
        System.out.println("Hello, " + i +"!");
        monitor.unlock();
    }

    public static void main(String args[]) throws InterruptedException {
        HelloWorldMonitor monitor = new HelloWorldMonitor();
        Thread t[] = new Thread[10];
        for (int i = 0; i<t.length; i++) {
            int finalI = i;
            t[finalI] = new Thread(() -> {
                monitor.hello_world(finalI);
            });
        }
        for (int i = 0; i<t.length; i++) t[i].start();
        for (int i = 0; i<t.length; i++) t[i].join();
    }

}
