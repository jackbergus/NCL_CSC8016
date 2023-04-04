package uk.ncl.CSC8016.jackbergus.coursework.project2.testing;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.BasketResult;

import java.util.function.Predicate;

public class CheckValidBasketResultSum implements Predicate<BasketResult> {
    @Override
    public boolean test(BasketResult basketResult) {
        if (basketResult == null) return false;
        double grand_total = 0.0;
        for (var x : basketResult.boughtItems) {
            grand_total += x.cost;
        }
        if (grand_total != basketResult.total_cost) return false;
        return basketResult.account_result == basketResult.total_given - basketResult.total_cost;
    }
}
