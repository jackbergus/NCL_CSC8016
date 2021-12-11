package it.giacomobergami.CSC8016.coursework.wrong_example_test;

import it.giacomobergami.CSC8016.coursework.SystemFacade;
import it.giacomobergami.CSC8016.coursework.ClientSystemTransaction;
import it.giacomobergami.CSC8016.coursework.operations.AccountOperation;
import it.giacomobergami.CSC8016.coursework.operations.MoneyOperation;
import it.giacomobergami.CSC8016.coursework.operations.Operation;

import javax.el.MethodNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Simple and not completely correct implementation of the coursework, just to provide an example on how to use the
 * interface!
 */
public class SimpleWrongSystem implements SystemFacade {
    private final ReentrantLock mutex; // Global lock for handling the HashMap on creating and reading
    private final HashMap<String, ArrayList<Operation>> accounts; // Each account is just the list of operations
    private final HashMap<String, ReentrantLock> elements;        // Wrong! Allowing only one process at a time for handling an account
    private final HashMap<String, Date> lastTime;                 // Recording the last time the process has been accessed
    boolean isServerStarted;

    public SimpleWrongSystem() {
        // Some initializations
        accounts = new HashMap<>();
        mutex = new ReentrantLock();
        elements = new HashMap<>();
        lastTime = new HashMap<>();
        isServerStarted = false;
    }

    @Override
    public boolean startServer() {
        // ERROR: no server thread is started! Everything is just handled directly by the client
        if (isServerStarted) {
            return false;
        } else {
            isServerStarted = true;
            return true;
        }
    }

    @Override
    public boolean stopServer() {
        // ERROR: no server thread is started! Everything is just handled directly by the client
        if (!isServerStarted) {
            return false;
        } else {
            isServerStarted = false;
            return true;
        }
    }

    @Override
    public boolean createAccount(String username) {
        boolean isCreated = false;
        try {
            mutex.lock();
            isCreated = accounts.putIfAbsent(username, new ArrayList<>()) == null;
            if (isCreated)
                elements.put(username, new ReentrantLock());
        } finally {
            mutex.unlock();
        }
        return isCreated;
    }

    @Override
    public ClientSystemTransaction openAccount(String username) {
        ClientSystemTransaction result = null;
        if (accounts.containsKey(username))
            result = new ClientSystemTransaction() {
                {
                    elements.get(username).lock();       // Locking one transaction at a time! Wrong!
                    lastTime.put(username, new Date());  // Given that only one process will access it, then the current access will have the last date
                }

                @Override
                public boolean depositMoney(double amount) {
                    // Wrong! The client directly handles the connection
                    accounts.get(username).add(MoneyOperation.addMoney(username, amount));
                    return true;
                }

                @Override
                public boolean withdrawMoney(double amount) {
                    // Wrong! The client directly handles the connection
                    double total = getTotalAmount();
                    boolean hasWithdrawn = false;
                    if (total >= amount) {
                        accounts.get(username).add(MoneyOperation.withdrawMoney(username, amount));
                        hasWithdrawn = true;
                    }
                    return hasWithdrawn;
                }

                @Override
                public double getTotalAmount() {
                    // Wrong! The client directly handles the connection
                    double total = 0.0;
                    for (var x : accounts.get(username))
                        if (x instanceof MoneyOperation)
                            total += x.getOperationType().equals("addMoney") ?
                                    ((MoneyOperation)x).getAmount() : -((MoneyOperation)x).getAmount();
                    return total;
                }

                @Override
                public List<Operation> getLast10Operations() {
                    // Wrong! The client directly handles the connection
                    accounts.get(username).add(new AccountOperation("getLast10Operations", username));
                    var x = accounts.get(username);
                    return new ArrayList<>(x.subList(Math.max(x.size()-11, 0), Math.max(x.size()-1, 0)));
                }

                @Override
                public List<MoneyOperation> getLast10MoneyOperations() {
                    // Wrong! The client directly handles the connection
                    accounts.get(username).add(new AccountOperation("getLast10MoneyOperations", username));
                    var x = accounts.get(username).stream().filter(y -> y instanceof  MoneyOperation).map(y -> (MoneyOperation)y).collect(Collectors.toList());
                    return new ArrayList<>(x.subList(Math.max(x.size()-11, 0), Math.max(x.size()-1, 0)));
                }

                @Override
                public Date getLastAccess() {
                    return lastTime.get(username);
                }

                @Override
                public void close() throws Exception {
                    // Wrong! If a client thread breaks, you will have a deadlock, as no other thread is going
                    // to perform a transaction
                    // Furthermore, the system is going to be in a dirty state, where some changes were provided,
                    // but no transaction was committed! And so, the withdrawal was ordered, but the client, by breaking,
                    // never gave the user the money, while the server has the money removed from the bank account.
                    elements.get(username).unlock();
                }
            };
        return result;
    }

    @Override
    public Double closeAccount(String username) {
        throw new MethodNotFoundException("closeAccount");
    }
}
