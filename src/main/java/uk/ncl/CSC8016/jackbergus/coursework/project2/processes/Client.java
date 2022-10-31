package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;


import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class Client implements Runnable {

    protected final int clientId;
    public Server shop;
    public boolean purchase = false;
    protected List<String> shoppingList;
    protected List<String> noAvailableProduct;
    protected List<String> productsNotHoldByTheShop;
    public int pick_errors, shelve_errors, basket_remove_errors, rerun_errors, all_attempt_failed;
    List<Item> basket;
    boolean hasRun;

    public Client(int clientId, Server phisical_shop) {
        this.clientId = clientId;
        shop = phisical_shop;
        pick_errors = 0;
        shelve_errors = 0;
        basket_remove_errors = 0;
        rerun_errors = 0;
        all_attempt_failed = 0;
        basket = new ArrayList<>();
        shoppingList = new ArrayList<>();
        noAvailableProduct = new ArrayList<>();
        productsNotHoldByTheShop = new ArrayList<>();
        hasRun = false;
    }

    protected abstract void actualCode();

    @Override
    public void run() {
        if (!hasRun) {
            actualCode();
        } else {
            rerun_errors++;
            System.err.println("Attempting at re-running the same process! error");
        }
        hasRun = true;
    }


    protected void checkoutBasket() {
        for (int i = 1; i<=3; i++) {
            if (shop.purchase(clientId, basket)) {
                System.out.println("Successful purchase!");
                purchase = true;
                break;
            } else {
                System.err.println("No purchase was possible: re-trying the operation...");
            }
        }
        System.err.println("All of the attempts failed! (error)");
        all_attempt_failed++;
    }

}
