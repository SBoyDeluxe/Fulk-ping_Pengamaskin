package org.example.moneymachine.atm;

import org.example.moneymachine.UI.*;
import org.example.moneymachine.banks.superclasses.*;
import org.example.moneymachine.exceptions.*;
import org.example.moneymachine.model.DTO.*;
import org.example.moneymachine.model.entity.*;
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
     * boolean representing if ATM currently is on or off
     */
    private boolean isOn = true;
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
                    Optional<UserDTO> currentUserFromDb = atmService.getCurrentBank().get().getUserById(atmService.getCurrentUser().get().getId());
                    pinInput = userInterface.getPinInput(currentUserFromDb.get().getFailedAttmpts());

                    success = atmService.enterPin(pinInput);
                }
                return success;
            }

        }catch (InvalidInputException|LockedAccountException exception){
            userInterface.displayError(exception);
            atmService.sessionExit();
            success = false;
        }

        return success;
    }

    public void onAuthenticatedUser() {
        Optional<IntegratedAPIBank> currentBank = atmService.getCurrentBank();
        Optional<UserDTO> currentUser = this.atmService.getCurrentUser();

        if(currentBank.isPresent() && currentUser.isPresent()) {
            int menuChoice = 0;
            while(menuChoice != 3) {
                try {
                    menuChoice = userInterface.loggedInMenu(ACTIONS, currentBank.get().getBankNameAsStaticMethod());
                    String action = ACTIONS.get(menuChoice);
                    switch (menuChoice) {

                        case 0 -> {
                            //Check balance

                            userInterface.menuOption(action);
                            userInterface.presentMenuResult(atmService.checkBalance(), action);
                        }
                        case 1 -> {
                            //Make deposit
                            userInterface.menuOption(action);
                            //Get deposit amount
                            double amountInput = userInterface.getAmountInput();

                            //Make deposit in service layer
                            double newBalance = atmService.deposit(amountInput);
                            //Present deposition result
                            userInterface.presentMenuResult(amountInput, action);
                            //Present new balance

                            userInterface.presentMenuResult(newBalance, ACTIONS.get(0));

                        }
                        case 2 -> {
                            //Make withdrawal
                            userInterface.menuOption(action);
                            //Get withdrawal amount
                            double amountInput = userInterface.getAmountInput();

                            //Make withdrawal in service layer
                            double newBalance = atmService.withdraw(amountInput);
                            //Present withdrawalion result
                            userInterface.presentMenuResult(amountInput, action);

                            //Show new balance
                            userInterface.presentMenuResult(newBalance, ACTIONS.get(0));

                        }
                    }

                }catch(InvalidInputException|NotLoggedInException exception){
                    userInterface.displayError(exception);

                }
            }
            userInterface.logoutConfirmation();
            atmService.sessionExit();
        }
    }

    /**
     * Demo run of ATM-controller with a number of cards where pin is posted to
     * easier try out functionality
     * @param cardInsertions - The user entities to try, on none the start menu is shown and then
     * system.exit runs
     */
    public void demoRun(List<UserEntity> cardInsertions) {
        if(cardInsertions.isEmpty()){
            startMenu();
            System.exit(2);
        }else {

            int cardsToInsert = cardInsertions.size();
            int counter  = 0;
            double maxLoadTime = 3000;
            cardInsertions.forEach(((userEntity) -> {
                startMenu();
                mockLoading("Waiting for card...", maxLoadTime);
                System.out.println("Pin : " + userEntity.getPin());
                mockLoading("Card inserted, \n Loading...", maxLoadTime);
                boolean authenticated = onCardInsertion(userEntity.getId());
                if(authenticated){
                    onAuthenticatedUser();
                }


            }));
            System.exit(32);



        }




    }
    /**
     * Outputs text and waits a random fraction of the time specified (milliseconds)
     *
     * @param loadText - Text to show while loading
     * @param ms       - Exclusive upper bounding wait time
     */
private static void mockLoading(String loadText, double ms) {

        try {
        System.out.println(loadText);
        double randomTimeToCardInsertion = Math.random() * ms;
        Thread.sleep((long) randomTimeToCardInsertion);
    } catch (InterruptedException e) {
        System.out.println(e.getMessage());
        throw new RuntimeException(e);

    }
}

public boolean isOn() {
        return isOn;
    }

    public void switchOnOff() {
       this.isOn = !isOn;
    }
}
