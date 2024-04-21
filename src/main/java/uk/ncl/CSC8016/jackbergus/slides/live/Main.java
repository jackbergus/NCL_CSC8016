package uk.ncl.CSC8016.jackbergus.slides.live;

public class Main {

    public static void main(String args[]) throws Exception {
        System.out.println(Thread.currentThread().getName());
        var t = new Thread(() -> {
            System.out.println(Thread.currentThread().getName());
        });
        var o = 1;
        t.start();
        t.join();
    }

}
