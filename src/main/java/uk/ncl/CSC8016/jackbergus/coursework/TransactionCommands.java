package uk.ncl.CSC8016.jackbergus.coursework;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public interface TransactionCommands extends AutoCloseable {

    /**
     * Returns the incremental and progressive BigInteger associated to the current transaction of the current user
     * @return
     */
    public BigInteger getTransactionId();

    /**
     * Get the total amount associated to the current account after performing the transactions
     *
     * @return Final Total Amount
     */
    double getTentativeTotalAmount();

    /**
     * If the requested amount does not exceed the getTotalAmount(), it returns
     * @param amount
     * @return
     */
    boolean withdrawMoney(double amount);

    /**
     * This command puts some money into the account
     * @param amount
     * @return
     */
    boolean payMoneyToAccount(double amount);

    void abort();
    CommitResult commit();

    default void close() {
        System.out.println(commit());
    }

}
