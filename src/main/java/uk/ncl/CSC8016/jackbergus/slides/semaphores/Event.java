package uk.ncl.CSC8016.jackbergus.slides.semaphores;

import uk.ncl.CSC8016.jackbergus.slides.locks.SimpleLock;

public class Event implements SimpleLock {
    BinarySemaphore sem = new BinarySemaphore(0);
    @Override
    public void lock() throws Exception {
        sem.acquire();
    }
    @Override
    public void unlock() throws Exception {
        sem.release();
    }
}
