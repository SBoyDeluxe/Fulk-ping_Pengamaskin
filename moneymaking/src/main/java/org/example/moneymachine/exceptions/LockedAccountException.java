package org.example.moneymachine.exceptions;

/**
 * Thrown on locked user account after too many failed login-attempts
 */
public class LockedAccountException extends RuntimeException {

    /**
     * Thrown on locked user account after too many failed login-attempts
     */
    public LockedAccountException(String message) {
        super("Locked account : \n\t"+message);
    }
}
