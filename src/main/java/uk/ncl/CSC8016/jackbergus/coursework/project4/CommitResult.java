package uk.ncl.CSC8016.jackbergus.coursework.project4;

import java.util.List;

public class CommitResult {
    public final List<uk.ncl.CSC8016.jackbergus.coursework.project4.Operation> successfulOperations;
    public final List<uk.ncl.CSC8016.jackbergus.coursework.project4.Operation> unsuccessfulOperation;
    public final double totalAmount;

    public CommitResult(List<uk.ncl.CSC8016.jackbergus.coursework.project4.Operation> successfulOperations, List<Operation> ignoredOperations, double totalAmount) {
        this.successfulOperations = successfulOperations;
        this.unsuccessfulOperation = ignoredOperations;
        this.totalAmount = totalAmount;
    }
}
