package uk.ncl.CSC8016.jackbergus.coursework.project1;

import java.util.*;

import static uk.ncl.CSC8016.jackbergus.coursework.project1.OperationType.*;

public class Operation implements Comparable<Operation> {

    OperationType t;
    double amount;
    Integer time;

    public OperationType getT() {
        return t;
    }

    public void setT(OperationType t) {
        this.t = t;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    private Operation(Integer t) {
        assert t != null;
        time = t;
    }

    public static Operation Pay(double amount, Integer t) {
        assert amount >= 0;
        Operation op = new Operation(t);
        op.amount = amount;
        op.t = Pay;
        return op;
    }


    public static Operation Withdraw(double amount, Integer t) {
        assert amount >= 0;
        Operation op = new Operation(t);
        op.amount = amount;
        op.t = Withdraw;
        return op;
    }


    public static Operation Abort(Integer t) {
        Operation op = new Operation(t);
        op.amount = 0;
        op.t = Abort;
        return op;
    }

    public static Operation Commit(Integer t) {
        Operation op = new Operation(t);
        op.amount = 0;
        op.t = Commit;
        return op;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return Double.compare(operation.amount, amount) == 0 && t == operation.t && Objects.equals(time, operation.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t, amount, time);
    }

    @Override
    public int compareTo(Operation o) {
        if (o == null) return 1;
        return time.compareTo(o.time);
    }

    public static double cumulative(List<Operation> collection) {
        Collections.sort(collection);
        List<Operation> finalOperations = new ArrayList<>(collection.size());
        double totalAmount = 0;
        for (var x : collection) {
            switch (x.t) {
                case Pay -> totalAmount += x.amount;
                case Withdraw -> totalAmount -= x.amount;
                default -> {
                }
            }
        }
        return totalAmount; // The operation was neither committed nor aborted. This should be like if nothing happened
    }
}
