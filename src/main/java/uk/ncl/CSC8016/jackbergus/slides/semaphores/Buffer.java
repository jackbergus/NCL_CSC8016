package uk.ncl.CSC8016.jackbergus.slides.semaphores;

public class Buffer<T> {
    T message = null;
    BinarySemaphore produced = new BinarySemaphore(0);
    BinarySemaphore consumed = new BinarySemaphore(1);

    public void put(T x) throws InterruptedException {
        consumed.acquire();
        message = x;
        produced.release();
    }

    public T get() throws InterruptedException {
        produced.acquire();
        var local = message;
        consumed.release();
        return local;
    }

    public static int produce(int max, int min) {
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }

    public static void main(String args[]) {
        var buffer = new Buffer<Integer>();
        var producer = new Thread(() -> {
            int i = 0;
            while (true) {
                var val = produce(100, 0);
                System.out.println("Putting: "+val);
                try {
                    buffer.put(val);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        var consumer = new Thread(() -> {
            while (true) {
                try {
                    var val = buffer.get();
                    System.out.println(val);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        producer.start();
        consumer.start();
        // And they lived happily ever after...
    }

}
