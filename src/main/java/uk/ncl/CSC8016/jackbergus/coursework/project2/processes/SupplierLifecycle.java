package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class SupplierLifecycle implements Runnable {

    private final RainforestShop s;

    private volatile boolean hasRetrievedOneProduct;
    private AtomicBoolean stopped;
    private final Random rng;

    public SupplierLifecycle(RainforestShop s) {
        this.s = s;
        this.rng = new Random(0);
        hasRetrievedOneProduct = false;
        stopped = new AtomicBoolean(false);
    }

    public Thread startThread() {
        stopped = new AtomicBoolean(false);
        var t = new Thread(this);
        t.start();
        return t;
    }

    @Override
    public void run() {
        while (true) {
            String product = s.getNextMissingItem();
            if (product.equals("@stop!")) {
                s.supplierStopped(stopped);
                return;
            }
            hasRetrievedOneProduct = true;
            int howManyItems = this.rng.nextInt(1, 6);
            s.refurbishWithItems(howManyItems, product);
        }
    }

    public boolean hasAProductBeenProduced() {
        return hasRetrievedOneProduct;
    }

    public boolean isStopped() {
        return stopped.get();
    }
}
