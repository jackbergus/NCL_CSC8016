package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Operation;

import java.util.ArrayList;
import java.util.Random;

public class MobileAppClient extends Client {

    int countMaxOperations;
    Random rng;

    public MobileAppClient(int clientId, Server phisical_shop) {
       super(clientId, phisical_shop);
       rng = new Random();
       shoppingList = new ArrayList<>();
       countMaxOperations = rng.nextInt(0, 11);
    }

    protected void actualCode() {
        Operation op = null;
        do {
            op = getNextOperation();
            switch (op.operationType) {
                case QUITPHYSICALSHOP -> {
                    break;
                }
                case PICK -> {
                    var item = shop.putInVirtualBasket(clientId, op.itemToPurchase);
                    shoppingList.add(op.itemToPurchase);
                    if (item == null) {
                        pick_errors++;
                        System.err.println("ERROR: at this stage, item shall never be null!");
                    }

                    basket.add(item);
                }
                case SHELVE -> {
                    var item = shop.shelve(clientId, op.itemToPurchase);
                    shoppingList.remove(op.itemToPurchase);
                    if (item == null) {
                        shelve_errors++;
                        System.err.println("ERROR: at this stage, item shall never be null!");
                    } else {
                        if (!basket.remove(item)) {
                            System.err.println("ERROR: at this stage, you should be able to remove something from the basket!");
                            basket_remove_errors++;
                        }
                    }
                }
            }
        } while (op.operationType != Operation.type.QUITPHYSICALSHOP);

        // Checkout the basket
        checkoutBasket();
    }


    // Picking only currently available objects form the environment!
    private Operation getNextOperation() {
        if (countMaxOperations == 0) {
            countMaxOperations--;
            return new Operation(Operation.type.QUITPHYSICALSHOP, null);
        } else if (countMaxOperations > 0) {
            var item = shop.getRandomAvailableProduct(clientId);
            countMaxOperations--;
            if (shoppingList.contains(item)) {
                if (rng.nextBoolean()) {
                    return new Operation(Operation.type.PICK, item);
                } else
                    return new Operation(Operation.type.SHELVE, item);
            } else {
                return new Operation(Operation.type.PICK, item);
            }
        } else {
            System.err.println("ERROR: exceeded the maximum amount of operations!");
            countMaxOperations--;
            return new Operation(Operation.type.QUITPHYSICALSHOP, null);
        }
    }
}
