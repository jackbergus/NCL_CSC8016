package uk.ncl.CSC8016.jackbergus.slides.monitors;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer<T> {
    Queue<T> message = new LinkedList<>();
    Condition okRead, okWrite;
    Lock mutex;
    int maxsz, waiting;

    public BoundedBuffer(int max) {
        maxsz = max;
        mutex = new ReentrantLock(true);
        okWrite = mutex.newCondition();
        okRead = mutex.newCondition();
        waiting = 0;
    }

    public void put(T m) throws InterruptedException {
        mutex.lock();
        while (message.size() >= maxsz)
            okWrite.await();
        message.add(m);
        okRead.signal();
        mutex.unlock();
    }

    public T get() throws InterruptedException {
        T val;
        mutex.lock();
        while (message.isEmpty())
            okRead.await();
        val = message.remove();
        okWrite.signal();
        mutex.unlock();
        return val;
    }
}
