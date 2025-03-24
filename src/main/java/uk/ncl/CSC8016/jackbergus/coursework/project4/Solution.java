package uk.ncl.CSC8016.jackbergus.coursework.project4;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.Optional;

public class Solution extends BankFacade {

    HashMap<String, Double> hashMap;
    AtomicBigInteger abi;

    public Solution(HashMap<String, Double> userIdToTotalInitialAmount) {
        super(userIdToTotalInitialAmount);
        hashMap = userIdToTotalInitialAmount;
        abi = new AtomicBigInteger(BigInteger.ZERO);
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
                    return 0.0;
                }

                @Override
                public boolean withdrawMoney(double amount) {
                    return false;
                }
                @Override
                public boolean payMoneyToAccount(double amount) {
                    return false;
                }
                @Override
                public void abort() {
                }
                @Override
                public CommitResult commit() {
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
