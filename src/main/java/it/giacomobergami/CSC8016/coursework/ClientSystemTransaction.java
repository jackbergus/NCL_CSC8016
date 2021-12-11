package it.giacomobergami.CSC8016.coursework;


import it.giacomobergami.CSC8016.coursework.operations.MoneyOperation;
import it.giacomobergami.CSC8016.coursework.operations.Operation;

import java.util.Date;
import java.util.List;

/**
 * This is a simple transaction related to one single account, associated to a given username
 */
public interface ClientSystemTransaction extends AutoCloseable {

    /**
     * Deposits some positive money to the account
     * @param amount   Positive amount of money to be deposited
     * @return  If the amount is positive, returns true. Otherwise, false.
     */
    boolean depositMoney(double amount);

    /**
     * If possible, withdraws the money from the account
     * @param amount    Positive amount of money  to be deposited
     * @return If the amount is positive and if the account is the given amount of money, returns true. Otherwise, false.
     */
    boolean withdrawMoney(double amount);

    /**
     * Returns the total possible amount of money
     * @return
     */
    double getTotalAmount();

    /**
     * Returning the last 10 operations at the time the method was called!
     * @return
     */
    List<Operation> getLast10Operations();

    /**
     * Returning the last 10 monetary operations at the time the method was called!
     * @return
     */
    List<MoneyOperation> getLast10MoneyOperations();

    /**
     * Returning the last access (previous to this one) when the account was last accessed
     * @return
     */
    Date getLastAccess();

}
