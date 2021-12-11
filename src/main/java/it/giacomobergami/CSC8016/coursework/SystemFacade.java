package it.giacomobergami.CSC8016.coursework;

/**
 * Façade to the Bank system, that contains a server thread by the main program by calling start/stop server, and that
 * is going to be called by the RMI client threads that are going to use the interface.
 */
public interface SystemFacade {

    /**
     * Starts a single server thread. This is not meant to be called by a client thread, but just for initializing
     * the system!
     * @return  True if the server thread was internally created, false if it was already running.
     */
    boolean startServer();

    /**
     * Stops an existing server thread. This is not meant to be called by a client thread, but just for initializing
     *      * the system!
     * @return   True if the server thread was created internally, false if no server was up.
     */
    boolean stopServer();

    /**
     * Creates a new account, only if the server is running.
     * @param username  Name associated to the account
     * @return          It returns false if the server is not up. Otherwise, it returns true if the account did not
     *                  already exist
     */
    boolean createAccount(String username);

    /**
     * Opens an existing account, only if the server is running.
     * @param username  Name associated to the account to be opened
     * @return          It returns null if the server is not up. Otherwise, if the account associated to the username
     *                  exists, it returns a non-null system transaction
     */
    ClientSystemTransaction openAccount(String username);

    /**
     * If the server is upClosing an existing account. After performing this, openAccount will always return null
     * @param username  Name associated to the account to be closed
     * @return          If both the account exists and the server is up, it returns the positive amount of money
     *                  associated to it. Otherwise, it returns a null value
     */
    Double closeAccount(String username);
}
