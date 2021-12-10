package it.giacomobergami.CSC8016.coursework.operations;

public class Operation {
    private final String operationType;

    public Operation(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationType() {
        return operationType;
    }

    @Override
    public String toString() {
        return "Operation{" + super.toString() +
                "operationType='" + operationType + '\'' +
                '}';
    }
}
