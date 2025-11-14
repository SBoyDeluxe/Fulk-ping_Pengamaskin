package org.example.moneymachine.controller;

import org.example.moneymachine.*;

/**
 * Specifies a controller where the API can make calls to the bank´s  API
 */
public interface APIControllerInterface {

    /**
     * Gets a user with a specific id
     * @param id - The string specifying a user´s card number
     * @return The user with the specified id
     */
    public APIUserInterface getUserById(String id);

    /**
     * Gets whether a users card is locked
     * @param userId
     * @return true on locked card, false otherwise
     */
    public boolean isCardLocked(String userId);


}
