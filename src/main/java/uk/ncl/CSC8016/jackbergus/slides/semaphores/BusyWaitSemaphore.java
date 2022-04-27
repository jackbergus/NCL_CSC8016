package uk.ncl.CSC8016.jackbergus.slides.semaphores;

import java.util.concurrent.Semaphore;

public class BusyWaitSemaphore {
    private int total_v_minus_total_p;

    public BusyWaitSemaphore(int init) {
        total_v_minus_total_p = init;
        if (init  < 0)
            throw new RuntimeException("Error: init should be greater than zero!");
    }

    synchronized public void acquire() {
        total_v_minus_total_p--;
        while (total_v_minus_total_p < 0)
            try {
                this.wait();
            } catch (InterruptedException e) {}
    }

    synchronized public void release() {
        total_v_minus_total_p++;
        if (total_v_minus_total_p <= 0)
            this.notify();
    }
}
