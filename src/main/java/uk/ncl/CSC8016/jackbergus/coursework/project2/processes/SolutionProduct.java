package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class SolutionProduct {
    Queue<Item> available;
    Queue<Item> withdrawn;
//    ReentrantLock biggus_lock; // TODO: REMOVE THE LOCKS!

    public SolutionProduct() {
        available = new LinkedList<>();
        withdrawn = new LinkedList<>();
//        biggus_lock = new ReentrantLock();
    }

    public void removeItemsFromInavailability(Collection<Item> cls) {
        try {
//            biggus_lock.lock();
            for (Item x : cls) {
                if (withdrawn.remove(x))
                    available.add(x);
            }
        } finally {
//            biggus_lock.unlock();
        }
    }

    public Optional<Item> getAvailableItem() {
        Optional<Item> o = Optional.empty();
        try {
//            biggus_lock.lock();
            if (!available.isEmpty()) {
                var obj = available.remove();
                if (obj != null) {
                    o = Optional.of(obj);
                    withdrawn.add(o.get());
                }
            }
        } finally {
//            biggus_lock.unlock();
        }
        return o;
    }

    public boolean doShelf(Item u) {
        boolean result = false;
        try {
//            biggus_lock.lock();
            if (withdrawn.remove(u)) {
                available.add(u);
                result = true;
            }
        } finally {
//            biggus_lock.unlock();
        }
        return result;
    }

    public Set<String> getAvailableItems() {
        Set<String> s = Collections.emptySet();
        try {
//            biggus_lock.lock();
            s = available.stream().map(x -> x.productName).collect(Collectors.toSet());
        } finally {
//            biggus_lock.unlock();
        }
        return s;
    }

    public void addAvailableProduct(Item x) {
        Set<String> s = Collections.emptySet();
        try {
//            biggus_lock.lock();
            available.add(x);
        } finally {
//            biggus_lock.unlock();
        }
    }

    public double updatePurchase(Double aDouble,
                                 List<Item> toIterate,
                                 List<Item> currentlyPurchasable,
                                 List<Item> currentlyUnavailable) {
        double total_cost = 0.0;
        try {
//            biggus_lock.lock();
            for (var x : toIterate) {
                if (withdrawn.contains(x)) {
                    currentlyPurchasable.add(x);
                    total_cost += aDouble;
                } else {
                    currentlyUnavailable.add(x);
                }
            }
        } finally {
//            biggus_lock.unlock();
        }
        return total_cost;
    }

    public void makeAvailable(List<Item> toIterate) {
        try {
//            biggus_lock.lock();
            for (var x : toIterate) {
                if (withdrawn.remove(x)) {
                    available.add(x);
                }
            }
        } finally {
//            biggus_lock.unlock();
        }
    }

    public boolean completelyRemove(List<Item> toIterate) {
        boolean allEmpty = false;
        try {
//            biggus_lock.lock();
            for (var x : toIterate) {
                withdrawn.remove(x);
                available.remove(x);
            }
            allEmpty = withdrawn.isEmpty() && available.isEmpty();
        } finally {
//            biggus_lock.unlock();
        }
        return allEmpty;
    }
}
