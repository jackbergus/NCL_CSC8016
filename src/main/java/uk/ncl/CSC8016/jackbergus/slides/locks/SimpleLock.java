package uk.ncl.CSC8016.jackbergus.slides.locks;


public interface SimpleLock {

    void lock() throws Exception;
    void unlock() throws Exception;

}
