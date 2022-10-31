package uk.ncl.CSC8016.jackbergus.coursework.project1.wrong_implementation;

import uk.ncl.CSC8016.jackbergus.coursework.project1.BankFacade;
import uk.ncl.CSC8016.jackbergus.coursework.project1.CommitResult;
import uk.ncl.CSC8016.jackbergus.coursework.project1.Operation;
import uk.ncl.CSC8016.jackbergus.coursework.project1.TransactionCommands;
import uk.ncl.CSC8016.jackbergus.coursework.project1.utils.AtomicBigInteger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

public class WrongBankWithLocks extends BankFacade {
    HashMap<String, Double> hashMap;
    AtomicBigInteger abi;
    private ReentrantLock mutex;

    public WrongBankWithLocks(HashMap<String, Double> userIdToTotalInitialAmount) {
        super(userIdToTotalInitialAmount);
        if ((userIdToTotalInitialAmount == null)) throw new RuntimeException();
        hashMap = new HashMap<>(userIdToTotalInitialAmount);
        abi = new AtomicBigInteger(BigInteger.ZERO);
        mutex = new ReentrantLock(true);
    }

    @Override
    public String StudentID() {
        return "ngb113";
    }

    @Override
    public Optional<TransactionCommands> openTransaction(String userId) {
        if(hashMap.containsKey(userId)) {
            return Optional.of(new TransactionCommands() {
                boolean isProcessDone, isProcessAborted, isProcessCommitted;
                double totalLocalOperations;
                List<Operation> journal;
                BigInteger currentTransactionId;
                {
                    journal = new ArrayList<>();
                    mutex.lock();
                    totalLocalOperations = 0;
                    isProcessDone = isProcessAborted = isProcessCommitted = false;
                    currentTransactionId = abi.incrementAndGet();
                }
                @Override
                public BigInteger getTransactionId() {
                    return currentTransactionId;
                }
                @Override
                public double getTentativeTotalAmount() {
                    if (isProcessDone)
                        return hashMap.get(userId);
                    else
                        return -1;
                }
                @Override
                public boolean withdrawMoney(double amount) {
                    if ((amount < 0) || (isProcessDone)) return false;
                    else {
                        double val = hashMap.get(userId);
                        if (val >= amount) {
                            journal.add(Operation.Withdraw(amount, journal.size()));
                            totalLocalOperations -= amount;
                            return true;
                        } else
                            return false;
                    }
                }
                @Override
                public boolean payMoneyToAccount(double amount) {
                    if ((amount < 0) || (isProcessDone)) return false;
                    else {
                        journal.add(Operation.Pay(amount, journal.size()));
                        totalLocalOperations += amount;
                        return true;
                    }
                }
                @Override
                public void abort() {
                    if (!isProcessDone) {
                        isProcessDone = isProcessAborted = true;
                        isProcessCommitted = false;
                        mutex.unlock();
                    }
                }
                @Override
                public CommitResult commit() {
                    if (!isProcessDone) {
                        isProcessAborted = false;
                        isProcessDone = isProcessCommitted = true;
                        hashMap.computeIfPresent(userId, (s, aDouble) -> aDouble += totalLocalOperations);
                        mutex.unlock();
                        return new CommitResult(journal, new ArrayList<>(), hashMap.get(userId));
                    } else {
                        return null;
                    }
                }
            });
        } else
            return Optional.empty();
    }
}
