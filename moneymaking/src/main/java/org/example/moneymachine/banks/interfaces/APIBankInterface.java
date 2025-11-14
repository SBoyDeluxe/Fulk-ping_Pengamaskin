package org.example.moneymachine.banks.interfaces;

import org.example.moneymachine.model.DTO.*;

/**
 * Represents the contract a Bank implementation must fullfill to be able to interact with the API
 */
public interface APIBankInterface {

    /**
     * Authenticates a user with the bank´s api
     * @param userId - The user id of the user wanting to log on
     * @param pinCode - The pin code of the user yet to be authenticated
     * @return true on existing user in bank´s system, false otherwise
     */
    public boolean authenticateUserLogin(String userId, String pinCode);

    /**
     * Registers a deposit in the bank´s api
     * @param userId - userId of an authenticated user
     * @param amountToDeposit - The amount to deposit into the user account
     * @return The new account balance on completion
     */
    public double makeDeposit(String userId, double amountToDeposit);

    /**
     * Registers a withdrawal in the bank´s API for an authenticated user of
     * that bank
     * @param userId - The id of an authenticated user for the specific bank
     * @param amountToWithdraw - Amount to withdraw from that user´s account
     * @return The new account balance on completion
     */
    public double makeWithdrawal(String userId, double amountToWithdraw);

    /**
     * Returns whether a user exists within the bank´s user-system
     * @param userId - Card number of user
     * @return true if userId exists, false otherwise
     */
    public boolean isExistingUser(String userId);

    /**
     * Gets a user with a specific id
     * @param id - The string specifying a user´s card number
     * @return The user with the specified id
     */
    public UserDTO getUserById(String id);

    /**
     * Gets whether a users card is locked
     * @param userId
     * @return true on locked card, false otherwise
     */
    public boolean isCardLocked(String userId);

    /**
     * Checks if card number follows the card number format specified by the bank
     * @param cardNumber - Card number to test (denoted as userId in rest of API)
     * @return true if card number follows bank´s card number format, false otherwise
     * @see- |{@link <a href="https://en.wikipedia.org/wiki/Payment_card_number#Issuer_identification_number_(IIN)"></a> Issuer ID-numbers }|
     */
    public boolean cardNumberFollowsFormat(String cardNumber);

    /**
     * Returns name of bank when implemented, an empty string otherwise
     * @return name of bank when implemented, an empty string otherwise
     */
    public static String getBankName(){
        return "";
    }


}
