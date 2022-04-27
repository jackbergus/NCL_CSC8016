package uk.ncl.CSC8016.jackbergus.slides.monitors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SemMonitor {
    private Lock monitor;
    private Condition c;
    private int value, waiting;
    public SemMonitor() {
        monitor = new ReentrantLock(true);
        c = monitor.newCondition();
        value = waiting = 0;
    }
    public void acquire() throws InterruptedException {
        monitor.lock();
        if (value == 0) {
            waiting++;
            while (value == 0) c.await();
            waiting--;
        }
        value--;
        monitor.unlock();
    }
    public void release() throws InterruptedException {
        monitor.lock();
        value++;
        if (waiting > 0) c.signal();
        monitor.unlock();
    }
}
