package uk.ncl.CSC8016.jackbergus.coursework.project2.utils;

import java.util.Objects;

public class Item {
    public final String productName;
    public final double cost;

    public Item(String productName, double cost) {
        this.productName = productName;
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(item.cost, cost) == 0 && Objects.equals(productName, item.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, cost);
    }
}
