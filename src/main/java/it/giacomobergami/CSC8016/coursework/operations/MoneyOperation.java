package it.giacomobergami.CSC8016.coursework.operations;

public class MoneyOperation extends AccountOperation {

    double amount;

    private MoneyOperation(String operationType, String username, double amount) {
        super(operationType, username);
        this.amount = amount;
    }

    public static MoneyOperation addMoney(String username,  double amount) {
        return new MoneyOperation("addMoney", username, amount);
    }

    public static MoneyOperation withdrawMoney(String username,  double amount) {
        return new MoneyOperation("withdrawMoney", username, amount);
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "MoneyOperation{" + super.toString() +
                "amount=" + amount +
                '}';
    }
}
