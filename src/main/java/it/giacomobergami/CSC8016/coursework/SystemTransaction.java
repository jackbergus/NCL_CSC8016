package it.giacomobergami.CSC8016.coursework;


import it.giacomobergami.CSC8016.coursework.operations.MoneyOperation;
import it.giacomobergami.CSC8016.coursework.operations.Operation;

import java.util.Date;
import java.util.List;

public interface SystemTransaction extends AutoCloseable {

    boolean depositMoney(double amount);
    boolean withdrawMoney(double amount);
    double getTotalAmount();
    List<Operation> getLast10Operations();
    List<MoneyOperation> getLast10MoneyOperations();
    Date getLastAccess();

}
