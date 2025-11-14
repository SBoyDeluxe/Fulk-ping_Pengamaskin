package org.example.moneymachine.exceptions;

/**
 * Thrown on invalid input : Such as, a negative deposit amount,
 *                           a negative withdrawal amount,
 *                           when withdrawal amount > account balance
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Thrown on invalid input : Such as, a negative deposit amount,
     *                           a negative withdrawal amount,
     *                           when withdrawal amount > account balance
     */
    public InvalidInputException(String message) {
        super("Invalid input : \n\t"+message);
    }
}
