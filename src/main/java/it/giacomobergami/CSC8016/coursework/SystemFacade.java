package it.giacomobergami.CSC8016.coursework;

/**
 * Fa¢ade to the Bank system
 */
public interface SystemFacade {

    /**
     * Creates a new account
     * @param username  Name associated to the account
     * @return          Returns true if the account did not already exist
     */
    boolean createAccount(String username);

    /**
     * Opens an existing account
     * @param username  Name associated to the account to be opened
     * @return          If the account associated to the username exists, returns a non-null system transaction
     */
    SystemTransaction openAccount(String username);

    /**
     * Closing an existing account. After performing this, openAccount will always return null
     * @param username  Name associated to the account to be closed
     * @return          If the account exists, returns the positive amount of money associated to it.
     *                  Otherwise, returns a null value
     */
    Double closeAccount(String username);
}
