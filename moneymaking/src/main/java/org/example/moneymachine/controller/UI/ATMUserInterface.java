package org.example.moneymachine.controller.UI;

import org.example.moneymachine.banks.superclasses.*;

import java.util.*;

/**
 * Defines the contract needed by a user interface implementation for the atm
 */
public interface ATMUserInterface {

    /**
     * Shows an idle welcome screen before card insertion with
     * a list of the card providers connected to the atm
     * @param connectedBanks
     */
    void startMenu(List<IntegratedAPIBank> connectedBanks);

    /**
     * Displays a menu of options for an authenticated user
     * and returns the menu selection
     * @param actions - The actions a logged in user can perform (Log-in, Check balance, make a deposit...)
     * @param nameOfBank - Name of bank user belongs to
     * @return The menu selection
     */
    int loggedInMenu(List<String> actions, String nameOfBank);

    /**
     * Displays an error to the user
     * @param e
     */
    void displayError(Exception e);

    /**
     * Confirms a successful log-out
     */
    void logoutConfirmation();

    /**
     * Gets pin input
     * @return Pin input
     */
    String getPinInput();
}
