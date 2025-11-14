package org.example.moneymachine.exceptions;

public class NotLoggedInException extends RuntimeException {
    /**
     * Error thrown when trying to access user resources without being authenticated
     *  <br>
     *  Message:Your session has not been authenticated so you do not have access to ""nameOfDeniedResource""
     * @param nameOfDeniedResource  - The resource being denied (for example : account balance)
     */
    public NotLoggedInException(String nameOfDeniedResource) {
        super("Not Logged In : \n\t"+"Your session has not been authenticated so you do not have access to this resource " + nameOfDeniedResource);
    }
}
