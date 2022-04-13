package uk.ncl.CSC8016.jackbergus.slides.semaphores;

import uk.ncl.CSC8016.jackbergus.slides.locks.SimpleLock;

import java.util.concurrent.locks.Lock;

public class SemLock implements SimpleLock {
    BinarySemaphore sem = new BinarySemaphore(1);
    @Override
    public void lock() throws Exception {
        sem.acquire();
    }
    @Override
    public void unlock() throws Exception {
        sem.release();
    }
}
