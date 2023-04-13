package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.*;
import java.util.stream.Collectors;

public class ProductMonitor {
    Queue<Item> available;
    Queue<Item> withdrawn;

    public ProductMonitor() {
        available = new LinkedList<>();
        withdrawn = new LinkedList<>();
    }

    public void removeItemsFromUnavailability(Collection<Item> cls) {
        for (Item x : cls) {
            if (withdrawn.remove(x))
                available.add(x);
        }
    }

    public Optional<Item> getAvailableItem() {
        Optional<Item> o = Optional.empty();
        if (!available.isEmpty()) {
            var obj = available.remove();
            if (obj != null) {
                o = Optional.of(obj);
                withdrawn.add(o.get());
            }
        }
        return o;
    }

    public boolean doShelf(Item u) {
        boolean result = false;
        if (withdrawn.remove(u)) {
            available.add(u);
            result = true;
        }
        return result;
    }

    public Set<String> getAvailableItems() {
        Set<String> s;
        s = available.stream().map(x -> x.productName).collect(Collectors.toSet());
        return s;
    }

    public void addAvailableProduct(Item x) {
        available.add(x);
    }

    public double updatePurchase(Double aDouble,
                                 List<Item> toIterate,
                                 List<Item> currentlyPurchasable,
                                 List<Item> currentlyUnavailable) {
        double total_cost = 0.0;
        for (var x : toIterate) {
            if (withdrawn.contains(x)) {
                currentlyPurchasable.add(x);
                total_cost += aDouble;
            } else {
                currentlyUnavailable.add(x);
            }
        }
        return total_cost;
    }

    public void makeAvailable(List<Item> toIterate) {
        for (var x : toIterate) {
            if (withdrawn.remove(x)) {
                available.add(x);
            }
        }
    }

    public boolean completelyRemove(List<Item> toIterate) {
        boolean allEmpty;
        for (var x : toIterate) {
            withdrawn.remove(x);
            available.remove(x);
        }
        allEmpty = withdrawn.isEmpty() && available.isEmpty();
        return allEmpty;
    }
}
