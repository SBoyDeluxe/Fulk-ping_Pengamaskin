package org.example.moneymachine.controller.UI;

import lombok.*;
import org.example.moneymachine.banks.*;
import org.springframework.stereotype.*;

import java.util.*;
@AllArgsConstructor
@Component
public class UserInterface implements ATMUserInterface {


    public static final String DELIMITER = "**********************************************************************";
    public static final String LINEBREAK_AND_TWO_TABS = "\n\t\t";
    public  static final String WELCOME_MESSAGE = "\n\tWelcome!" + LINEBREAK_AND_TWO_TABS +
            "Please insert card to access ATM" + LINEBREAK_AND_TWO_TABS
            +"Currently connected banks : ";
    public static final String WELCOME_LIST_WHITESPACES = String.format("%28s", " ");
    public static final String DELIMITER_HALF = String.format("%27s", " ");
    /**
     * Used to gather user input from keyboard
     */
    private Scanner scanner;

    /**
     * String builder for string building in menus with custom list options
     */
    private StringBuilder stringBuilder;

    public UserInterface(){
        scanner = new Scanner( System.in);
        stringBuilder = new StringBuilder();
    }

    /**
     * Shows idle welcome message before card insertion
     *
     */
    @Override
    public void startMenu(List<APIBank> connectedBanks){

        resetStringBuilder();

        System.out.println(DELIMITER);
        stringBuilder.append(WELCOME_MESSAGE);
        presentListOfBanks(connectedBanks);

        stringBuilder.append("\n\n").append(DELIMITER);


        System.out.println(stringBuilder);


    }

    private void presentListOfBanks(List<APIBank> connectedBanks) {
        int bankCounter = 1;
        for(APIBank bank : connectedBanks){
            //28 chars in "Currently connected banks : "
            if(bankCounter == 1){
                stringBuilder
                        .append(bankCounter)
                        .append(". ")
                        .append(bank.getBankNameAsStaticMethod());
            }else {
                stringBuilder.append(LINEBREAK_AND_TWO_TABS)
                        .append(WELCOME_LIST_WHITESPACES)
                        .append(bankCounter)
                        .append(". ")
                        .append(bank.getBankNameAsStaticMethod());
            }
            //add to list counter
            bankCounter++;
        }
    }

    @Override
    public int loggedInMenu(List<String> actions, String bankName) {
        resetStringBuilder();

        stringBuilder.append(DELIMITER)
                .append("\n"+DELIMITER_HALF + "|")
                .append(bankName)
                .append("|")
                .append(LINEBREAK_AND_TWO_TABS);
        int listCounter = 1;
        for(String action : actions){
            stringBuilder.append(listCounter + ". ")
                    .append(action)
                    .append(LINEBREAK_AND_TWO_TABS);
            listCounter++;
        }

        stringBuilder.append("\n")
                .append(DELIMITER);

        System.out.println(stringBuilder);

        return getInputOfList(actions);


    }

    /**
     * Prompts user to select an int given a list where the return value
     * is the index of the selected option in the list
     * @param optionsList - List of options the user can choose from
     * @return - The selected option
     */
    protected int getInputOfList(List<String> optionsList) {


        boolean validInput = false;
        boolean inputConfirmed = false;
        int listInput = -1;
        String confirmInput = "";


        while(!validInput) {

            inputConfirmed = false;
            //Prompt for username
            System.out.println("\n\t Please select an option from the list \n\t Select: ");

            try {
                listInput = scanner.nextInt();
                //Gets it back to the index value
                listInput--;

                boolean inputInList = (listInput >=0) && (listInput < optionsList.size());

                if (inputInList) {
                    validInput = true;
                } else {
                    if (optionsList.size() == 1) System.out.println("\n Please choose a valid option : 1 ");
                    System.out.println("\n Please choose a valid option 1 -> " + optionsList.size());
                }

            } catch (InputMismatchException e) {
                if(optionsList.size() == 1)System.out.println("\n Please choose a valid option : 1 ");

                System.out.println("\n Please choose a valid option 1 -> " + optionsList.size());
                scanner = new Scanner(System.in);
            }
        }

        return listInput;
    }

    @Override
    public void displayError(Exception e) {

        resetStringBuilder();

        stringBuilder.append(DELIMITER)
                .append("\n")
                .append(DELIMITER_HALF)
                .append(e.getMessage())
                .append("\n");
        System.out.println(stringBuilder);
        boolean confirmed = false;

        while(!confirmed) {
            confirmed = awaitConfirmation();
        }
        System.out.println(" ");
        System.out.println(DELIMITER);


    }

    protected boolean awaitConfirmation() {

        boolean validConfirmation = false;
        boolean inputConfirmed = false;
        String confirmInput = "";
        while(!inputConfirmed) {
            System.out.print("\n\t Confirm : OK/ok \n\t");

            confirmInput = scanner.nextLine().toLowerCase(Locale.ROOT);

            if (confirmInput.equals("ok")) {
                validConfirmation = true;
                inputConfirmed = true;
            } else {
                System.out.println("Please confirm : Ok/ok \n");
            }
        }
        //Is now confirmed
        return validConfirmation;
    }

    @Override
    public void logoutConfirmation() {
    resetStringBuilder();
    stringBuilder
            .append("\n")
            .append(DELIMITER)
            .append(LINEBREAK_AND_TWO_TABS)
            .append("\n\t")
            .append(" Thank you! ")
            .append("\n\t")
            .append("You have been logged out")
            .append("\n");
        System.out.println(stringBuilder);
        System.out.println(DELIMITER);
    }

    /**
     * Resets {@linkplain UserInterface#stringBuilder the StringBuilder}
     */
    private void resetStringBuilder() {
        stringBuilder = (stringBuilder.isEmpty()) ? stringBuilder : new StringBuilder();
    }


    /**
     *
     * @return - The input user pin
     */
    @Override
    public String getPinInput(){

        boolean validPinInput = false;
        boolean inputConfirmed = false;
        String pinInput = "";
        String confirmInput = "";


        while(!validPinInput) {

            inputConfirmed = false;
            //Prompt for username
            System.out.print("\n\t Please enter pin \n\t Pin: ");
            pinInput = scanner.nextLine();
            boolean onlyNumbers = checkOnlyNumbers(pinInput);
            while(pinInput.isEmpty() || pinInput.isBlank()){

                System.out.print("\n\t Pin cannot be empty \n ");
                System.out.print("\n\t Please enter pin: ");
                pinInput = scanner.nextLine();

            }
            while(!onlyNumbers){
                System.out.println("\n\t Pin can only consist of numbers. Please try again \n\t Pin:");
                pinInput = scanner.nextLine();
                 onlyNumbers = checkOnlyNumbers(pinInput);
            }
            //Valid username input -> Prompt to confirm and redo if not wanted
            while(!inputConfirmed) {
                System.out.print("\n\t Confirm : Y(es)/N(o) \n\t");

                confirmInput = scanner.nextLine().toLowerCase(Locale.ROOT);

                switch (confirmInput) {
                    case "y", "yes": {
                        validPinInput = true;
                        inputConfirmed = true;
                    }
                    break;
                    case "n", "no": {
                        //Nothing needs to be done, on next loop the username will be prompted -> We just need to get out of inputConfirmed loop
                        inputConfirmed = true;

                    }
                    break;
                    default: {
                        System.out.println("Please enter valid option : Y(es) or N(o) \n");

                    }
                }
            }
        }

        return  pinInput;










    }

    private static boolean checkOnlyNumbers(String pinInput) {
        boolean onlyContainsNumbers = true;
        char[] inputCharArray = pinInput.toCharArray();
        for (int i = 0; i < inputCharArray.length && onlyContainsNumbers ; i++){
            char character1 = inputCharArray[i];
            switch (character1){
                case '0','1','2','3','4','5','6','7','8','9' ->{

                }
                default -> {onlyContainsNumbers = false;
                }
            }
        }
        return onlyContainsNumbers;
    }




}