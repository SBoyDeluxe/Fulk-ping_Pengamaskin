package org.example.moneymachine;

import org.example.moneymachine.exceptions.*;

/**
 * Represents the contract a ATM-implementation must fullfill to interact with the api
 */
public interface ATMInterface {
    /**
     * Specifies the handling of card insertion into a specific atm
     * @param userId - The userId, usually a card number
     * @return true on valid user that can do login attempts, false otherwise
     */
    boolean insertCard(String userId) throws LockedAccountException;

    /**
     * Performs validation and authentication for a certain user/pin combo
     * @param pin - The pin the user enters
     * @return true on valid pin, false otherwise
     */
    boolean enterPin(String pin);

    /**
     * Gets balance of user´s account
     * @return Balance of authenticated user account
     */
    double checkBalance();

    /**
     * Deposits an amount of money into the user´s account
     * @param amount - The amount to deposit
     * @return The new balance
     */
    double deposit(double amount);

    /**
     * Withdraws a certain amount from the user´s account
     * @param amount - Amount to withdraw
     * @return The new balance
     */
    double withdraw(double amount);

    /**
     * Invalidates an authenticated session (I.E Log out)
     */
     void sessionExit();

}
