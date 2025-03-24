package uk.ncl.CSC8016.jackbergus.coursework.project4;

import java.util.Objects;

import static uk.ncl.CSC8016.jackbergus.coursework.project4.OperationType.*;

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

}
