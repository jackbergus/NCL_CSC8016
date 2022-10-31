package uk.ncl.CSC8016.jackbergus.coursework.project1;

import java.util.HashMap;
import java.util.Optional;

public abstract class BankFacade {

    public BankFacade(HashMap<String, Double> userIdToTotalInitialAmount) {
    }

    public abstract String StudentID();

    /**
     * This command returns an empty transaction if the user cannot create a connection (e.g., the account does not exist).
     * Otherwise, it provides a connection to the
     *
     * @param userId  For an oversimplification, the user is only required to access its account thorugh their id.
     *
     * @return A transaction, if any operation is allowed
     */
    public abstract Optional<TransactionCommands> openTransaction(String userId);

}
