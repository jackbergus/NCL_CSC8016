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

public class WrongBankNoConcurrency extends BankFacade {

    HashMap<String, Double> hashMap;
    AtomicBigInteger abi;

    public WrongBankNoConcurrency(HashMap<String, Double> userIdToTotalInitialAmount) {
        super(userIdToTotalInitialAmount);
        hashMap = userIdToTotalInitialAmount;
        abi = new AtomicBigInteger(BigInteger.ZERO);
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
                BigInteger currentTransactionId;
                {
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
                    return hashMap.get(userId);
                }

                @Override
                public boolean withdrawMoney(double amount) {
                    if ((amount < 0) || (isProcessDone)) return false;
                    else {
                        double val = hashMap.get(userId);
                        if (val >= amount) {
                            totalLocalOperations -= amount;
                            hashMap.put(userId, val - amount);
                            return true;
                        } else
                            return false;
                    }
                }
                @Override
                public boolean payMoneyToAccount(double amount) {
                    if ((amount < 0) || (isProcessDone)) return false;
                    else {
                        double val = hashMap.get(userId);
                        totalLocalOperations += amount;
                        hashMap.put(userId, val + amount);
                        return true;
                    }
                }
                @Override
                public void abort() {
                    if (!isProcessDone) {
                        hashMap.computeIfPresent(userId, (key, oldValue) -> oldValue - totalLocalOperations);
                        isProcessDone = isProcessAborted = true;
                        isProcessCommitted = false;
                    }
                }
                @Override
                public CommitResult commit() {
                    if (!isProcessDone) {
                        isProcessAborted = false;
                        isProcessDone = isProcessCommitted = true;
                    }
                    return null;
                }

                @Override
                public void close() {
                    commit();
                }
            });
        } else
            return Optional.empty();
    }
}
