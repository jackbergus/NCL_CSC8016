package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Server {

    public ShoppingAttempt isProductAvailable(String item) {
        // TODO: implement
        return null;
    }

    public boolean pick(Item item) {
        return false;
        // TODO: implement
    }

    public void stopShopping(int clientId) {

        // TODO: implement
    }

    public String getRandomAvailableProduct(int clientId) {
        String availableProductName = null; // some name afterwards
        // TODO: implement
        return availableProductName;
    }

    public List<Item> waitWhenFullyAvailable(List<String> noAvailableProduct) {
        // TODO: implement
        return Collections.emptyList();
    }

    public enum Reason {
        AVAIL,
        NOT_AVAIL,
        NOT_HELD
    }

    public class ShoppingAttempt {
        public final Reason motivation;
        public final Item resolved_item;

        public ShoppingAttempt(Reason motivation, Item resolved_item) {
            this.motivation = motivation;
            this.resolved_item = resolved_item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ShoppingAttempt that = (ShoppingAttempt) o;
            return motivation == that.motivation && Objects.equals(resolved_item, that.resolved_item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(motivation, resolved_item);
        }
    }

    /**
     * As soon as the shop flops, this method returns false and stops waiting.
     * Otherwise, it waits until a new order for the supplier is ready.
     * @return
     */
    public boolean hasOrderForSupplier() {
        // TODO: implement
        return false;
    }

    /**
     * Sets the shop as flopped, thus notifying the Supplier that any process waiting for hasOrderForSupplier should stop
     */
    public void setShopFlop() {
        // TODO: implement
    }

    public List<Item> getRequestedProducts() {
        // TODO: implement
        return Collections.emptyList();
    }

    /**
     * Sends the shop the products that are ready to be displayed in the store
     * @param getOrderFromShop
     */
    public void shipmentCompletedWith(List<Item> getOrderFromShop) {
        // TODO: implement
    }

    public boolean purchase(int clientId, List<Item> basket) {
        // TODO: implement
        return false;
    }

    public Item putInVirtualBasket(int clientId, String itemToPurchase) {
        // TODO: implement
        return null;
    }

    public Item shelve(int clientId, String itemToPurchase) {
        // TODO: implement
        return null;
    }

    public void startsShopping(int clientId) {
        // TODO: implement
    }
}
