package uk.ncl.CSC8016.jackbergus.slides.locks;

import java.util.concurrent.atomic.AtomicBoolean;

public class TASLock implements SimpleLock {

    AtomicBoolean state = new AtomicBoolean(false);

    @Override
    public void lock() {
        while (state.getAndSet(true)) {}
    }

    @Override
    public void unlock() {
        state.set(false);
    }
}
