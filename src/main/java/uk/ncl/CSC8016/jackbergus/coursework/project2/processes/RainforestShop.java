package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.BasketResult;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.MyUUID;
import uk.ncl.CSC8016.jackbergus.slides.semaphores.scheduler.Pair;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RainforestShop {

    /// For correctly implementing the server, pelase consider that

    private final boolean isGlobalLock;
    private boolean supplierStopped;
    private Set<String> allowed_clients;
    public HashMap<UUID, String> UUID_to_user;
    private volatile HashMap<String, ProductMonitor> available_withdrawn_products;
    private HashMap<String, Double> productWithCost = new HashMap<>();
    private volatile Queue<String> currentEmptyItem;


    public boolean isGlobalLock() {
        return isGlobalLock;
    }

    /**
     * Please replace this string with your student ID, so to ease the marking process
     * @return  Your student id!
     */
    public String studentId() {
        return "012345678";
    }


    /**
     *
     * @param client_ids                Collection of registered client names that can set up the communication
     * @param available_products        Map associating each product name to its cost and the initial number of available items on the shop
     * @param isGlobalLock              Might be used (but not strictly required) To remark whether your solution uses a
     *                                  pessimistic transaction (isGlobalLock=true) or an optimistic opne (isGlobalLock=false)
     */
    public RainforestShop(Collection<String> client_ids,
                          Map<String, Pair<Double, Integer>> available_products,
                          boolean isGlobalLock) {
        supplierStopped = true;
        currentEmptyItem = new LinkedBlockingQueue<>();
        this.isGlobalLock = isGlobalLock;
        allowed_clients = new HashSet<>();
        if (client_ids != null) allowed_clients.addAll(client_ids);
        this.available_withdrawn_products = new HashMap<>();
        UUID_to_user = new HashMap<>();
        if (available_products != null) for (var x : available_products.entrySet()) {
            if (x.getKey().equals("@stop!")) continue;
            productWithCost.put(x.getKey(), x.getValue().key);
            var p = new ProductMonitor();
            for (int i = 0; i<x.getValue().value; i++) {
                p.addAvailableProduct(new Item(x.getKey(), x.getValue().key, MyUUID.next()));
            }
            this.available_withdrawn_products.put(x.getKey(), p);
        }
    }

    /**
     * Performing an user log-in. To generate a transaction ID, please use the customary Java method
     * 
     * UUID uuid = UUID.randomUUID();
     * 
     * @param username      Username that wants to login
     *
     * @return A non-empty transaction if the user is logged in for the first time, and he hasn't other instances of itself running at the same time
     *         In all the other cases, thus including the ones where the user is not registered, this returns an empty transaction
     *
     */
    public Optional<Transaction> login(String username) {
        Optional<Transaction> result = Optional.empty();
        if (allowed_clients.contains(username)) {
            UUID uuid = UUID.randomUUID();
            UUID_to_user.put(uuid, username);
            result = Optional.of(new Transaction(this, username, uuid));
        }
        return result;
    }

    /**
     * This method should be accessible only to the transaction and not to the public!
     * Logs out the client iff. there was a transaction that was started with a given UUID and that was associated to
     * a given user
     *
     * @param transaction
     * @return false if the transaction is null or whether that was not created by the system
     */
    boolean logout(Transaction transaction) {
        boolean result = false;
        // TODO: Implement the remaining part!
        return result;
    }

    /**
     * Lists all of the items that were not basketed and that are still on the shelf
     * @param transaction
     * @return
     */
    List<String> getAvailableItems(Transaction transaction) {
        List<String> ls = Collections.emptyList();
        // TODO: Implement the remaining part!
        return ls;
    }

    /**
     * If a product can be basketed from the shelf, then a specific instance of the product on the shelf is returned
     *
     * @param transaction   User reference
     * @param name          Product name picked from the shelf
     * @return  Whether the item to be basketed is available or not
     */
    Optional<Item> basketProductByName(Transaction transaction, String name) {
        AtomicReference<Optional<Item>> result = new AtomicReference<>(Optional.empty());
        if (transaction.getSelf() == null || (transaction.getUuid() == null)) return result.get();
        // TODO: Implement the remaining part!
        return result.get();
    }

    /**
     * If the current transaction has withdrawn one of the objects from the shelf and put it inside its basket,
     * then the transaction shall be also able to replace the object back where it was (on its shelf)
     * @param transaction   Transaction that basketed the object
     * @param object        Object to be reshelved
     * @return  Returns true if the object existed before and if that was basketed by the current thread, returns false otherwise
     */
    boolean shelfProduct(Transaction transaction, Item object) {
        boolean result = false;
        if (transaction.getSelf() == null || (transaction.getUuid() == null)) return false;
        // TODO: Implement the remaining part!
        return result;
    }

    /**
     * Stops the food supplier by sending a specific message. Please observe that no product shall be named @stop!
     */
    public void stopSupplier() {
        // TODO: Provide a correct concurrent implementation!
        currentEmptyItem.add("@stop!");
    }

    /**
     * The supplier acknowledges that it was stopped, and updates its internal state. The monitor also receives confirmation
     * @param stopped   Boolean variable from the supplier
     */
    public void supplierStopped(AtomicBoolean stopped) {
        // TODO: Provide a correct concurrent implementation!
        supplierStopped = true;
        stopped.set(true);
    }

    /**
     * The supplier invokes this method when it needs to know that a new product shall be made ready available.
     *
     * This method should be blocking (if currentEmptyItem is empty, then this should wait until currentEmptyItem
     * contains at least one element and, in that occasion, then returns the first element being available)
     * @return
     */
    public String getNextMissingItem() {
        // TODO: Provide a correct concurrent implementation!
        supplierStopped = false;
        while (currentEmptyItem.isEmpty());
        return currentEmptyItem.remove();
    }


    /**
     * This method is invoked by the Supplier to refurbrish the shop of n products of a given time (current item)
     * @param n                 Number of elements to be placed
     * @param currentItem       Type of elements to be placed
     */
    public void refurbishWithItems(int n, String currentItem) {
        // Note: this part of the implementation is completely correct!
        Double cost = productWithCost.get(currentItem);
        if (cost == null) return;
        for (int i = 0; i<n; i++) {
            available_withdrawn_products.get(currentItem).addAvailableProduct(new Item(currentItem, cost, MyUUID.next()));
        }
    }

    /**
     * This operation purchases all the elements available on the basket
     * @param transaction               Transaction containing the current withdrawn elements from the shelf (and therefore basketed)
     * @param total_available_money     How much money can the client spend at maximum
     * @return
     */
    public BasketResult basketCheckout(Transaction transaction, double total_available_money) {
        // Note: this part of the implementation is completely correct!
        BasketResult result = null;
        if (UUID_to_user.getOrDefault(transaction.getUuid(), "").equals(transaction.getUsername())) {
            var b = transaction.getUnmutableBasket();
            double total_cost = (0.0);
            List<Item> currentlyPurchasable = new ArrayList<>();
            List<Item> currentlyUnavailable = new ArrayList<>();
            for (Map.Entry<String, List<Item>> entry : b.stream().collect(Collectors.groupingBy(x -> x.productName)).entrySet()) {
                String k = entry.getKey();
                List<Item> v = entry.getValue();
                total_cost += available_withdrawn_products.get(k).updatePurchase(productWithCost.get(k), v, currentlyPurchasable, currentlyUnavailable);
            }
            if ((total_cost > total_available_money)) {
                for (Map.Entry<String, List<Item>> entry : b.stream().collect(Collectors.groupingBy(x -> x.productName)).entrySet()) {
                    String k = entry.getKey();
                    List<Item> v = entry.getValue();
                    available_withdrawn_products.get(k).makeAvailable(v);
                }
                currentlyUnavailable.clear();
                currentlyPurchasable.clear();
                total_cost = (0.0);
            } else {
                Set<String> s = new HashSet<>();
                for (Map.Entry<String, List<Item>> entry : b.stream().collect(Collectors.groupingBy(x -> x.productName)).entrySet()) {
                    String k = entry.getKey();
                    List<Item> v = entry.getValue();
                    if (available_withdrawn_products.get(k).completelyRemove(v))
                        s.add(k);
                }
                currentEmptyItem.addAll(s);
            }
            result = new BasketResult(currentlyPurchasable, currentlyUnavailable, total_available_money, total_cost, total_available_money- total_cost);
        }
        return result;
    }
}
