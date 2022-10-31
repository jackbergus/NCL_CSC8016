package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import java.util.List;
import java.util.Objects;

public class OnLinePurchaseApp extends Client {

    private final List<String> shoppingList;
    int count_errors_for_basketing;
    int noMatchInSize;
    int noMatchInContent;

    public OnLinePurchaseApp(int clientId, Server shop, List<String> shoppingList) {
        super(clientId, shop);
        this.shoppingList = shoppingList;
        count_errors_for_basketing = 0;
        noMatchInSize = 0;
        noMatchInContent = 0;
    }

    @Override
    protected void actualCode() {
        shop.startsShopping(clientId);
        for (var item : shoppingList) {
            var reason = shop.isProductAvailable(item);
            switch (reason.motivation) {
                case AVAIL -> {
                    var obj = shop.putInVirtualBasket(clientId, item);
                    if (Objects.equals(obj, reason.resolved_item)) {
                        basket.add(reason.resolved_item);
                    } else if (obj == null) {
                        System.err.println("ERROR: previously available product cannot be put in the virtual basket!");
                        count_errors_for_basketing++;
                    } else {
                        System.err.println("ERROR: previously available product cannot be put in the virtual basket!");
                        count_errors_for_basketing++;
                    }

                }
                case NOT_AVAIL -> noAvailableProduct.add(item);
                case NOT_HELD -> productsNotHoldByTheShop.add(item);
            }
        }
        shop.stopShopping(clientId);

        checkoutBasket();

        if (!productsNotHoldByTheShop.isEmpty()) {
            System.err.println("The current products are hot held by the shop: ");
            productsNotHoldByTheShop.forEach(System.out::println);
        }

        if (!noAvailableProduct.isEmpty()) {
            System.err.println("The current products are not currently available on the shop: ");
            productsNotHoldByTheShop.forEach(System.out::println);
            System.err.println("You are going to receive a notification as soon as these can be collected phisically at the shop!");
            var ls = shop.waitWhenFullyAvailable(noAvailableProduct);
            if ((ls.size() != noAvailableProduct.size())) {
                System.err.println("ERROR: the two lists should match in size");
                noMatchInSize++;
            } else {
                var availableItems = ls.stream().map(x -> x.productName).sorted().toList();
                var waitedItems = noAvailableProduct.stream().sorted().toList();
                if (!availableItems.equals(waitedItems)) {
                    System.err.println("ERROR: the two lists should contain the same products");
                    noMatchInContent++;
                }
                basket = ls;
                checkoutBasket();
            }
        }
    }
}
