package it.giacomobergami.CSC8016.coursework;

public interface SystemFacade {
    boolean createAccount(String username);
    SystemTransaction openAccount(String username);
    Double closeAccount(String username);
}
