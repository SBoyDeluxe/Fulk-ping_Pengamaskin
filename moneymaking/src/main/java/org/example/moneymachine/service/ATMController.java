package org.example.moneymachine.service;

import org.example.moneymachine.*;
import org.example.moneymachine.banks.superclasses.*;
import org.example.moneymachine.controller.UI.*;
import org.example.moneymachine.exceptions.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * Responsible for calling the ATM-service layer and integrating the
 * user interface in those calls
 */
@Component
public class ATMController {
    private static final List<String> ACTIONS = List.of("Check balance", "Make deposit", "Make a withdrawal", "Exit");

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

    public boolean onCardInsertion(String userId) {
        boolean success = false;
        try{
             success = atmService.insertCard(userId);
            if(success){
                String pinInput = "";
                success = false;
                while(!success) {
                    pinInput = userInterface.getPinInput();

                    success = atmService.enterPin(pinInput);
                }
                return success;
            }

        }catch (InvalidInputException|LockedAccountException exception){
            userInterface.displayError(exception);
            success = false;
        }

        return success;
    }

    public void onAuthenticatdUser() {
        Optional<IntegratedAPIBank> currentBank = atmService.getCurrentBank();
        if(currentBank.isPresent()) {
            int menuChoice = 0;
            while(menuChoice != 3) {
                menuChoice = userInterface.loggedInMenu(ACTIONS, currentBank.get().getBankNameAsStaticMethod());
            }
        }
    }
}
