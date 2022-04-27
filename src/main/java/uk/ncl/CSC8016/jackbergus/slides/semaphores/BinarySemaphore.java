package uk.ncl.CSC8016.jackbergus.slides.semaphores;

import java.util.concurrent.Semaphore;

public class BinarySemaphore {
    private Semaphore s0, s1;
    public BinarySemaphore(int init) {
        assert ((init == 1) || (init == 0));
        s0 = new Semaphore(init);
        s1 = new Semaphore(1-init);
    }

    public void acquire() throws InterruptedException {
        s0.acquire();
        s1.release();
    }

    public void release() throws InterruptedException {
        s1.acquire();
        s0.release();
    }
}
