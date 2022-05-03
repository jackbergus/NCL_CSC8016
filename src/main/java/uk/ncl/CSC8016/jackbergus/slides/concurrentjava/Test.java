package uk.ncl.CSC8016.jackbergus.slides.concurrentjava;

public class Test {
    static  int  x = 0, y = 0;
    static void a() { x = 3; y = 4; }
    static int b() {int z = y; z+=x; return z;}
    public static void main(String[] args) throws InterruptedException {
        var t1 = new Thread(() -> {
            a();
        }, "t1");
        var t2 = new Thread(()-> {
            System.out.println("b() = "+b());
        }, "t2");
        t1.start(); t2.start();
        t1.join(); t2.join();
    }

}
