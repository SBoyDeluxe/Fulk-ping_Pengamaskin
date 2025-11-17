package org.example.moneymachine.service;

import org.example.moneymachine.*;
import org.example.moneymachine.controller.UI.*;
import org.springframework.stereotype.*;

/**
 * Responsible for calling the ATM-service layer and integrating the
 * user interface in those calls
 */
@Component
public class ATMController {
    /**
     * Presents model and results from calls to the service layer to the user
     */
    private UserInterface userInterface;
    /**
     * Handles processing calls to the model made by the ATM
     */
    private ATMService atmService;

    public ATMController(ATMService atmService, UserInterface userInterface) {
        this.atmService = atmService;
        this.userInterface = userInterface;
    }

    public void startMenu() {

        userInterface.startMenu(atmService.getConnectedBanks());
    }
}
