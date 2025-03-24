package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.BasketResult;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.*;

public class Transaction {
    private RainforestShop s;
    private String username;
    private UUID uuid;

    private LinkedList<Item> basket;

    Transaction(RainforestShop s, String username, UUID uuid) {
        this.s = s;
        this.username = username;
        this.uuid = uuid;
        basket = new LinkedList<>();
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    RainforestShop getSelf() {
        return s;
    }

    public List<String> getAvailableItems()  {
        if (s == null || (uuid == null)) return Collections.emptyList();
        return s.getAvailableItems(this);
    }

    public boolean logout() {
        if (s == null || (uuid == null)) return false;
        return s.logout(this);
    }

    public boolean basketProduct(String name) {
        if (s == null || (uuid == null)) return false;
        Optional<Item> item = s.basketProductByName(this, name);
        item.ifPresent(basket::add);
        return item.isPresent();
    }

    public List<Item> getUnmutableBasket() {
        if (s == null || (uuid == null)) return Collections.emptyList();
        List<Item> elements = new ArrayList<>();
        for (var x : basket)
            elements.add(x);
        return elements;
    }

public boolean shelfProduct(Item name) {
        if (s == null || (uuid == null)) return false;
        boolean result = s.shelfProduct(this, name);
        if (result) basket.remove(name);
        return result;
    }

    public BasketResult basketCheckout(double total_available_money) {
        if (s == null || (uuid == null)) return null;
        return s.basketCheckout(this, total_available_money);
    }

    void clearBasket() {
        basket.clear();
    }

    void invalidateTransaction() {
        s = null;
        username = null;
        uuid = null;
        basket.clear();
    }

}
