package org.example.moneymachine;


import lombok.*;
import org.example.moneymachine.banks.*;
import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.exceptions.*;
import org.example.moneymachine.model.DTO.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.context.properties.bind.*;
import org.springframework.lang.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@Getter
@Setter
public class ATM implements ATMInterface {
    /**
     * An implementation extending the {@linkplain APIBankInterface bank-interface }
     */
    /**
     * The list of connected banks with API-implementations
     */
    private final List<APIBank> connectedBanks;


    /**
     * The basis to know what kind of implementation to call for -> In current version of implenentation
     * we only have one fully realized "Bank", the {@link MockBank}, and as such this value can only be
     * {@link APIBankEnum#MOCKBANK} or null
     */
    @Autowired
    private APIBankEnum selectedBankEnum;

    /**
     * The current, authenticated user or null if not authenticated
     */
    @Autowired
    private Optional<UserDTO> currentUser;


    public ATM(List<APIBank> connectedBanks) {

        this.connectedBanks = connectedBanks;
        this.selectedBankEnum = APIBankEnum.NONE;
        this.currentUser = Optional.empty();

    }
//    @Autowired
//    public ATM(List<APIBank> connectedBanks, @Nullable Optional<APIBank> currentBank, @Nullable Optional<UserDTO> currentUser, APIBankEnum selectedBankEnum) {
//        this.connectedBanks = connectedBanks;
//        this.currentBank = currentBank;
//        this.currentUser = currentUser;
//        this.selectedBankEnum = selectedBankEnum;
//    }

    /**
     * Sets the {@linkplain APIBank} to use for the specific card
     * and sets the userId for the {@linkplain ATM#currentUser currentUser-proptery}
     * @throws InvalidInputException on no matched connected Bank APIs
     * @throws LockedAccountException on locked user account
     * @param userId - The userId, usually a card number
     * @return true on valid userId within some APIBank-implementation
     */
    @Override
    public boolean insertCard(String userId) throws LockedAccountException {

        boolean isMatch = false;
        int indexOfMatchedBank = -1;
        //See if user´s card serial number fits any Bank api´s format
        for (int i = 0; i < connectedBanks.size() && !isMatch ; i++) {

           isMatch = connectedBanks.get(i).cardNumberFollowsFormat(userId);
           //If match we save the index
            indexOfMatchedBank = (isMatch) ? i : indexOfMatchedBank;

        }

        //If isMatch == false then we do not have a compatible API-implementation
        if(!isMatch) throw new InvalidInputException("Your card-provider unfortunately is not registered with this ATM");

        //Else we have a valid index

        APIBank matchedBank = connectedBanks.get(indexOfMatchedBank);
        this.selectedBankEnum = APIBankEnum.values()[indexOfMatchedBank +  1];


        switch (this.selectedBankEnum){
            case MOCKBANK -> {
                MockBank mockBank = (MockBank) matchedBank;


                UserDTO userDTO = mockBank.getUserById(userId);
                if(userDTO.isLocked()) throw new LockedAccountException("There have been too many unsuccessful login-attempts on account with id :" + userId + "\n Please contact your bank : " + mockBank.getBankNameAsStaticMethod());
                //If account is not locked we can set the user-id to be used with entered pin code
                this.setCurrentUser(Optional.of(new UserDTO(userId, -10, -10, false)));


                return true;
            }
            default -> {

                return false;
//                 throw new LockedAccountException("There have been too many unsuccessful login-attempts on account with id :" + userId + "\n Please contact your bank : " + getC.getBankNameAsStaticMethod());

            }
        }




    }

    /**
     *Tries to authenticate the userId/pin combination against the bank api.
     * @param pin - The pin the user enters
     * @throws LockedAccountException on too many failed logins
     * @return true on loginSuccess, false otherwise
     */
    @Override
    public boolean enterPin(String pin) {
        boolean loginSuccess = false;
        if(getCurrentBank().isPresent()) {
            switch (this.selectedBankEnum) {
                case MOCKBANK -> {
                    MockBank mockBank = (MockBank) getCurrentBank().get();
                    try {
                        //Output number of failed attempts and remaining attempts
                        UserDTO specifiedUser = mockBank.getUserById(currentUser.get().id());
                        int failedAttmpts = specifiedUser.failedAttmpts();
                        int attemptsRemaining = 3 - failedAttmpts;
                        if (failedAttmpts > 0)
                            System.out.printf("\n\t\t  You have failed %d times.\n\t\tYou have %d attempts left before you are locked out%n", failedAttmpts, attemptsRemaining);


                         loginSuccess = mockBank.authenticateUserLogin(currentUser.get().id(), pin);
                        currentUser = (loginSuccess) ? Optional.of(specifiedUser) : Optional.empty();

                    } catch (LockedAccountException e) {
                        throw e;
                    }
                }case MASTERCARD -> {
                    loginSuccess = false;
                }
                default -> {
                    loginSuccess = false;
                }
            }
        }
        return loginSuccess;
    }

    /**
     * Gets balance for {@link ATM#currentUser} or throws error if currentUser is not set
     * @throws NotLoggedInException - on currentUser not set
     * @return balance of user´s account
     */
    @Override
    public double checkBalance() {

        if(this.selectedBankEnum == APIBankEnum.NONE || (currentUser.isPresent() && currentUser.get().accountBalance() ==-10)) throw new NotLoggedInException("account balance");


        return currentUser.get().accountBalance();

    }

    /**
     * Deposits the given amount to the user´s account
     * @param amount - The amount to deposit
     * @throws InvalidInputException on negative input amount
     * @throws NotLoggedInException on non-authenticated user
     * @return The new account balance
     */
    @Override
    public double deposit(double amount) {
        double balanceAfterDeposit = -10;
        // Check user-authentication and bank validity
        if(this.selectedBankEnum == APIBankEnum.NONE || currentUser.get().accountBalance() == -10) throw new NotLoggedInException("account deposit action");

        //Make deposit with bank-api, will throw error on negative input

        switch (this.selectedBankEnum){
            case MOCKBANK -> {
                MockBank bank = (MockBank) getCurrentBank().get();

                try {
                    balanceAfterDeposit = bank.makeDeposit(currentUser.get().id(),amount);
                }catch (InvalidInputException e){
                    throw e;
                }

            }
            case null, default -> {}
        }

        return balanceAfterDeposit;
    }
    /**
     * Withdraws the given amount from the user´s account
     * @param amount - The amount to withdraw
     * @throws InvalidInputException on negative input amount
     * @throws NotLoggedInException on non-authenticated user
     * @return The new account balance
     */
    @Override
    public double withdraw(double amount) {
        double balanceAfterDeposit = -10;
        // Check user-authentication and bank validity
        if(this.selectedBankEnum == APIBankEnum.NONE || ( currentUser.isPresent() && currentUser.get().accountBalance() == -10)) throw new NotLoggedInException("account deposit action");

        //Make deposit with bank-api, will throw error on negative input

        switch (this.selectedBankEnum){
            case MOCKBANK -> {
                MockBank bank = (MockBank) getCurrentBank().get();

                try {
                    balanceAfterDeposit = bank.makeWithdrawal(currentUser.get().id(),amount);
                }catch (InvalidInputException e){
                    throw e;
                }

            }
            case null, default -> {}

        }
        return balanceAfterDeposit;

    }

    /**
     * Logs out user
     * <ol>
     *     <li>Sets {@linkplain ATM#currentUser} to {@linkplain Optional#empty()} </li>
     *     <li>Sets {@linkplain ATM#getSelectedBankEnum()} to {@linkplain APIBankEnum#NONE} </li>
     *     <li> {@linkplain ATM#getCurrentBank()} now returns  {@linkplain Optional#empty()} </li>
     * </ol>
     */
    @Override
    public void sessionExit() {

        setCurrentUser(Optional.empty());
        setSelectedBankEnum(APIBankEnum.NONE);

    }

    /**
     * The bank that the authenticated user´s account belongs to
     * or empty optional if user has not been authenticated against a bank.
     *
     * @return {@link Optional#empty()} on no currentBank, otherwise {@linkplain Optional<APIBank>}
     */
    public Optional<APIBank> getCurrentBank() {

       int selectedIndex = (selectedBankEnum.ordinal() -1);
       Optional<APIBank> returnBank = (selectedIndex == -1) ? Optional.empty() : Optional.of(connectedBanks.get(selectedIndex));

        return returnBank;
    }
}
