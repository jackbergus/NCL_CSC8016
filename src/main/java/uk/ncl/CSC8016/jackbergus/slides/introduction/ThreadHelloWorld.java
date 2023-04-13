package uk.ncl.CSC8016.jackbergus.slides.introduction;

public class ThreadHelloWorld {

    public static void main(String[] args) throws Exception {
        System.out.println("Running the main thread from the process!");
        var x = new Thread(() -> System.out.println("Hello new Thread!"));
        x.start();
        x.join(); // Waiting for the thread to finish its computation
    }

}
