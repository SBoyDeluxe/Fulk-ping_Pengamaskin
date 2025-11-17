package org.example.moneymachine;


import lombok.*;
import org.example.moneymachine.banks.*;
import org.example.moneymachine.banks.interfaces.*;
import org.example.moneymachine.banks.superclasses.*;
import org.example.moneymachine.exceptions.*;
import org.example.moneymachine.model.DTO.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@Getter
@Setter
public class ATMService implements ATMInterface {
    /**
     * An implementation extending the {@linkplain APIBankInterface bank-interface }
     */
    /**
     * The list of connected banks with API-implementations
     */
    private final List<IntegratedAPIBank> connectedBanks;


    /**
     * The basis to know what bank to use :
     * {@link APIBankEnum#MOCKBANK}, {@link APIBankEnum#MASTERCARD} or {@link APIBankEnum#NONE}
     */
    @Autowired
    private APIBankEnum selectedBankEnum;

    /**
     * The current, authenticated user or null if not authenticated
     */
    @Autowired
    private Optional<UserDTO> currentUser;


    public ATMService(List<IntegratedAPIBank> connectedBanks) {

        this.connectedBanks = connectedBanks;
        this.selectedBankEnum = APIBankEnum.NONE;
        this.currentUser = Optional.empty();

    }


    /**
     * Sets the {@linkplain IntegratedAPIBank} to use for the specific card
     * and sets the userId for the {@linkplain ATMService#currentUser currentUser-proptery}
     * @throws InvalidInputException on no matched connected Bank APIs
     * @throws LockedAccountException on locked user account
     * @param userId - The userId, usually a card number
     * @return true on valid userId within some APIBank-implementation
     */
    @Override
    public boolean insertCard(String userId) throws LockedAccountException, InvalidInputException {

        boolean isMatch = false;
        int indexOfMatchedBank = -1;
        //See if user´s card serial number fits any Bank card number format
        for (int i = 0; i < connectedBanks.size() && !isMatch ; i++) {

           isMatch = connectedBanks.get(i).cardNumberFollowsFormat(userId);
           //If match we save the index
            indexOfMatchedBank = (isMatch) ? i : indexOfMatchedBank;

        }

        //If isMatch == false then we do not have a compatible API-implementation
        if(!isMatch) throw new InvalidInputException("Your card-provider unfortunately is not registered with this ATM");

        //Else we have a valid index

        IntegratedAPIBank matchedBank = connectedBanks.get(indexOfMatchedBank);
        this.selectedBankEnum = APIBankEnum.values()[indexOfMatchedBank +  1];





                Optional<UserDTO> userDTO = matchedBank.getUserById(userId);
                if(userDTO.isPresent()) {
                    if (userDTO.get().isLocked())
                        throw new LockedAccountException("There have been too many unsuccessful login-attempts on account with id :" + userId + "\n Please contact your bank : " + matchedBank.getBankNameAsStaticMethod());
                    //If account is not locked we can set the user-id to be used with entered pin code
                    this.setCurrentUser(Optional.of(new UserDTO(userId, -10, -10, false)));


                    return true;
                }else {
                    throw new InvalidInputException("Your card-provider unfortunately is not registered with this ATM");                }
//                 throw new LockedAccountException("There have been too many unsuccessful login-attempts on account with id :" + userId + "\n Please contact your bank : " + getC.getBankNameAsStaticMethod());

            }







    /**
     *Tries to authenticate the userId/pin combination against the bank api.
     * @param pin - The pin the user enters
     * @throws LockedAccountException on too many failed logins
     * @return true on loginSuccess, false otherwise
     */
    @Override
    public boolean enterPin(String pin) throws LockedAccountException {
        boolean loginSuccess = false;
        if(getCurrentBank().isPresent()) {
                    IntegratedAPIBank mockBank =  getCurrentBank().get();
                    try {
                        //Output number of failed attempts and remaining attempts
                        Optional<UserDTO> specifiedUser = mockBank.getUserById(currentUser.get().id());
                        if (specifiedUser.isPresent()) {
                            int failedAttmpts = specifiedUser.get().failedAttmpts();
                            int attemptsRemaining = 3 - failedAttmpts;
                            if (failedAttmpts > 0)
                                System.out.printf("\n\t\t  You have failed %d times.\n\t\tYou have %d attempts left before you are locked out%n", failedAttmpts, attemptsRemaining);


                            loginSuccess = mockBank.authenticateUserLogin(currentUser.get().id(), pin);
                            currentUser = (loginSuccess) ? Optional.of(specifiedUser.get()) : Optional.empty();
                        }

                    } catch (LockedAccountException e) {
                        throw e;
                    }


        }
        return loginSuccess;
    }

    /**
     * Gets balance for {@link ATMService#currentUser} or throws error if currentUser is not set
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

        if (getCurrentBank().isPresent()){
                IntegratedAPIBank bank =  getCurrentBank().get();

                try {
                    balanceAfterDeposit = bank.makeDeposit(currentUser.get().id(),amount);
                }catch (InvalidInputException e){
                    throw e;
                }


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

                IntegratedAPIBank bank = getCurrentBank().get();

                try {
                    balanceAfterDeposit = bank.makeWithdrawal(currentUser.get().id(),amount);
                }catch (InvalidInputException e){
                    throw e;
                }




        return balanceAfterDeposit;

    }

    /**
     * Logs out user
     * <ol>
     *     <li>Sets {@linkplain ATMService#currentUser} to {@linkplain Optional#empty()} </li>
     *     <li>Sets {@linkplain ATMService#getSelectedBankEnum()} to {@linkplain APIBankEnum#NONE} </li>
     *     <li> {@linkplain ATMService#getCurrentBank()} now returns  {@linkplain Optional#empty()} </li>
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
     * @return {@link Optional#empty()} on no currentBank, otherwise {@linkplain Optional<IntegratedAPIBank>}
     */
    public Optional<IntegratedAPIBank> getCurrentBank() {

       int selectedIndex = (selectedBankEnum.ordinal() -1);
       Optional<IntegratedAPIBank> returnBank = (selectedIndex == -1) ? Optional.empty() : Optional.of(connectedBanks.get(selectedIndex));

        return returnBank;
    }

}
