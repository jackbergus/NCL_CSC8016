package it.giacomobergami.CSC8016.coursework.operations;

public class AccountOperation extends Operation {
    private String username;

    public AccountOperation(String operationType, String username) {
        super(operationType);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "AccountOperation{" + super.toString()  +
                "username='" + username + '\'' +
                '}';
    }
}
