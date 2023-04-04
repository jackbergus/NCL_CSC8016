package uk.ncl.CSC8016.jackbergus.coursework.project2.processes;

import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.BasketResult;
import uk.ncl.CSC8016.jackbergus.coursework.project2.utils.UtilityMethods;

import java.util.Random;

public class ClientLifecycle implements Runnable {

    private final String username;
    private final RainforestShop s;
    private final int items_to_pick_up;
    private final double total_available_money, shelfing_prob;

    private final Random rng;

    private boolean doCheckOut = true;

    BasketResult l;

    public ClientLifecycle(String username,
                           RainforestShop s,
                           int items_to_pick_up,
                           double shelfing_prob,
                           double total_available_money,
                           int rng_seed) {
        this.username = username;
        this.s = s;
        this.items_to_pick_up = items_to_pick_up;
        this.total_available_money = total_available_money;
        this.shelfing_prob = Double.min(1.0, Double.max(0.0, shelfing_prob));
        rng = new Random(rng_seed);
    }

    public Thread thread(boolean doCheckOut) {
        this.doCheckOut = doCheckOut;
        return new Thread(this);
    }

    public BasketResult getBasketResult() {
        return l;
    }

    public BasketResult startJoinAndGetResult(boolean doCheckOut) throws InterruptedException {
        this.doCheckOut = doCheckOut;
        Thread t = new Thread(this);
        t.start(); t.join();
        return l;
    }

    @Override
    public void run() {
        l = null;
        double nextAfter = Math.nextUp(1.0);
        if (s != null) {
            s.login(username).ifPresent(transaction -> {
                for (int i = 0; i< items_to_pick_up; i++) {
                    var obj = UtilityMethods.getRandomElement(s.getAvailableItems(transaction), rng);
                    if (obj == null) break;
                    if (transaction.basketProduct(obj)) {
                        if (rng.nextDouble(0.0, nextAfter) < shelfing_prob) {
                            if (!transaction.shelfProduct(UtilityMethods.getRandomElement(transaction.getUnmutableBasket(), rng)))
                                throw new RuntimeException("ERROR: I musth be able to shelf a product that I added!");
                        }
                    }
                }
                if (doCheckOut)
                    l = transaction.basketCheckout(total_available_money);
                else
                    l = null;
                transaction.logout();
            });
        }
    }
}
