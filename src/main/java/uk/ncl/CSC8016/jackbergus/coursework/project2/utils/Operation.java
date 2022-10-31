package uk.ncl.CSC8016.jackbergus.coursework.project2.utils;

import java.util.Objects;

public class Operation {
    public enum type {
        QUITPHYSICALSHOP,
        PICK,
        SHELVE
    }

    public final type operationType;
    public final String itemToPurchase;

    public Operation(type operationType, String itemToPurchase) {
        this.operationType = operationType;
        this.itemToPurchase = itemToPurchase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return operationType == operation.operationType && Objects.equals(itemToPurchase, operation.itemToPurchase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationType, itemToPurchase);
    }
}
