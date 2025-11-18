package org.example.moneymachine.model.entity;

import org.example.moneymachine.*;

/**
 * Represents the contract a User-implementation must fullfill to interact with the API according to the specifications
 * in {@link SpringBootApplication#main(String[]) in the program specification}
 */
public interface APIUserInterface {
    /**
     * Get id of users
     * @return id of user
     */
    String getId();

    String getPin();

    double getBalance();

    int getFailedAttempts();

    boolean isLocked();

    void lockCard();

    void incrementFailedAttempts();

    void resetFailedAttempts();

    void deposit(double amount);

    void withdraw(double amount);
}
