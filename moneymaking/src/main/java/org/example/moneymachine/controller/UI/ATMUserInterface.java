package org.example.moneymachine.controller.UI;

import org.example.moneymachine.*;
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
     * For example, make withdrawal :<br>
     *          <kdim>
     *              **********************************************************************
     *              <br>
     *                      Withdraw :
     *                          <br>
     *                          <br>
     *                          Enter the amount you wish to withdraw from your account <br>
     *                        <code>->{@linkplain UserInterface#getAmountInput()}</code>  <p>Please enter amount <br>
     *                          Amount: | </p>
     *              **********************************************************************
     *
     *          </kdim>
     * @param action - Presents the option specified in {@linkplain SpringBootApplication#ACTIONS}
     */
    void menuOption(String action);

    /**
     * Presents the result of the action ; {@linkplain SpringBootApplication#ACTIONS}
     * @param result - The result of the action (Ex: -> The new account balance)
     * @param action - The action performed (Ex: Make withdrawal)
     */
    void presentMenuResult(Object result, String action);

    /**
     * Gets pin input
     * @return Pin input
     */
    String getPinInput();

    /**
     * Gets amount for withdrawal or deposit
     * @return amount
     */
    double getAmountInput();
}
