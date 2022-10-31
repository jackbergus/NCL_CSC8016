package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;


import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.List;
import java.util.Random;

/**
 * Provides the lifecycle of a product supplier
 */
public class RainForestSupplier implements Runnable {

    public Server shop;
    public RainForestSupplier(Server server) {
        this.shop = server;
    }

    @Override
    public void run() {
        Random rng = new Random();
        while (shop.hasOrderForSupplier()) {
            List<Item> getOrderFromShop = shop.getRequestedProducts();
            try {
                // Producing and shipping products to the shop
                Thread.sleep(getOrderFromShop.size() * ((long)rng.nextInt(10, 2000)));
            } catch (InterruptedException ignored) { }
            shop.shipmentCompletedWith(getOrderFromShop);
        }
        System.err.println("I'm so sorry that your shop has flopped...");
    }
}
