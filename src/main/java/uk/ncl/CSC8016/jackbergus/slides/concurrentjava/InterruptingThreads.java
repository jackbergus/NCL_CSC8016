package uk.ncl.CSC8016.jackbergus.slides.concurrentjava;

public class InterruptingThreads {

    public static void main(String args[]) throws  Exception {
        System.out.println("Main thread is: " + Thread.currentThread().getName());
        var t = new Thread(() -> {
           for (int i = 0; i<5; i++) {
               try {
                   Thread.sleep(200);
               } catch (InterruptedException e) {
                   System.out.println(Thread.currentThread().getName() + " was interrupted out of seep for the " + i +"-th time");
               }
           }
           System.out.println("Thread quits");
        });
        t.start();
        for (int i = 0; i<3; i++) {
            Thread.sleep(200); t.interrupt();
        }
        System.out.println("Main thread quits");
    }

}
