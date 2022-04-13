package uk.ncl.CSC8016.jackbergus.slides.concurrentjava;

public class MyClass {

    volatile Integer ai = 0;
    class R implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i<200; i++) ai++;
        }
    }

    void mainThread() throws InterruptedException {
        Thread[] t = new Thread[2];
        for (int i = 0; i<t.length; i++) t[i] = new Thread(new R());
        for (int i = 0; i<t.length; i++) t[i].start();
        for (int i = 0; i<t.length; i++) t[i].join();
        System.out.println(ai);
    }

    public static void main(String[] args) throws InterruptedException {
        new MyClass().mainThread();
    }

}
