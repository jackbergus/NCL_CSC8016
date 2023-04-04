package uk.ncl.CSC8016.jackbergus.coursework.project2.utils;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.Item;

import java.util.List;

public class BasketResult {
    public final List<Item> boughtItems;
    public final List<Item> unavailableItems;
    public double total_given;
    public double total_cost;
    public double account_result;

    public BasketResult(List<Item> boughtItems, List<Item> unavailableItems, double total_given, double total_cost, double account_result) {
        this.boughtItems = boughtItems;
        this.unavailableItems = unavailableItems;
        this.total_given = total_given;
        this.total_cost = total_cost;
        this.account_result = account_result;
    }

    @Override
    public String toString() {
        return "The user successfully bought: "+boughtItems+" but the following ones were not available or the user had not enough money to buy all the products: "+unavailableItems+" with a total of given money of "+total_given+" and a total item cost of "+total_cost+", the user was given back "+account_result;
    }
}
