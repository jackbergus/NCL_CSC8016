package uk.ncl.CSC8016.jackbergus.coursework.project1;

import java.util.List;

public class CommitResult {
    public final List<Operation> successfulOperations;
    public final List<Operation> ignoredOperations;
    public final double totalAmount;

    public CommitResult(List<Operation> successfulOperations, List<Operation> ignoredOperations, double totalAmount) {
        this.successfulOperations = successfulOperations;
        this.ignoredOperations = ignoredOperations;
        this.totalAmount = totalAmount;
    }
}
