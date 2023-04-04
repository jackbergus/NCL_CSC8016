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

public class SolutionServer {

    private final boolean isGlobalLock;
    private boolean supplierStopped;
    private Set<String> allowed_clients;

    public HashMap<UUID, String> UUID_to_user;
    private volatile HashMap<String, SolutionProduct> available_withdrawn_products;
    private HashMap<String, Double> productWithCost = new HashMap<>();

    private volatile Queue<String> currentEmptyItem;


    public boolean isGlobalLock() {
        return isGlobalLock;
    }

    public String studentId() {
        return "012345678";
    }


    public SolutionServer(Collection<String> client_ids,
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
            productWithCost.put(x.getKey(), x.getValue().key);
            var p = new SolutionProduct();
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
     * Logs out the client iff. there was an
     *
     * @param transaction
     * @return false if the transaction is null or whether that was not created by the system
     */
    boolean logout(Transaction transaction) {
        boolean result = false;
        // TODO: Implement the remaining part!
        return result;
    }

    List<String> getAvailableItems(Transaction transaction) {
        List<String> ls = Collections.emptyList();
        // TODO: Implement the remaining part!
        return ls;
    }

    Optional<Item> basketProductByName(Transaction transaction, String name) {
        AtomicReference<Optional<Item>> result = new AtomicReference<>(Optional.empty());
        if (transaction.getSelf() == null || (transaction.getUuid() == null)) return result.get();
        // TODO: Implement the remaining part!
        return result.get();
    }

    boolean shelfProduct(Transaction transaction, Item object) {
        boolean result = false;
        if (transaction.getSelf() == null || (transaction.getUuid() == null)) return false;
        // TODO: Implement the remaining part!
        return result;
    }

    public void stopSupplier() {
        // TODO: Provide a correct concurrent implementation!
        currentEmptyItem.add("@stop!");
    }

    public void supplierStopped(AtomicBoolean stopped) {
        // TODO: Provide a correct concurrent implementation!
        supplierStopped = true;
        stopped.set(true);
    }

    public String getNextMissingItem() {
        // TODO: Provide a correct concurrent implementation!
        supplierStopped = false;
        while (currentEmptyItem.isEmpty());
        return currentEmptyItem.remove();
    }


    public void refurbishWithItems(int n, String currentItem) {
        // Note: this part of the implementation is completely correct!
        Double cost = productWithCost.get(currentItem);
        if (cost == null) return;
        for (int i = 0; i<n; i++) {
            available_withdrawn_products.get(currentItem).addAvailableProduct(new Item(currentItem, cost, MyUUID.next()));
        }
    }

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
