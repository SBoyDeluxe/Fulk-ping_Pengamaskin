package org.example.moneymachine.banks.superclasses;

import org.example.moneymachine.banks.interfaces.*;
import org.example.moneymachine.exceptions.*;
import org.example.moneymachine.model.DTO.*;
import org.example.moneymachine.model.entity.*;
import org.example.moneymachine.service.*;

import java.util.*;


public abstract class FunctionalAPIBank extends APIBank implements APIBankInterface {
    protected final UserService userService;

    /**
     * A functional bank in the API, able to perform CRUD-operations to their user-base.
     *
     * @param bankName - Name of the bank
     * @param userService - The user persistence service
     */
    public FunctionalAPIBank(String bankName, UserService userService) {
        super(bankName, userService);
        this.userService = userService;
    }




    /**
     * Authenticates a user-login attempt and returns true on success, false otherwise.
     * Logs failed attempts to bank api and resets them on authentication
     *
     * @param userId  - The user id of the user wanting to log on
     * @param pinCode - The pin code of the user yet to be authenticated
     * @return true if (userExists && userIsNotLocked && credentialsMatch) else false
     * @throws LockedAccountException on user is locked
     */
    @Override
    public boolean authenticateUserLogin(String userId, String pinCode) {


        // Check if user exists
        boolean userExists = this.isExistingUser(userId);
        if (userExists) {
            //Check if is locked
            Optional<UserDTO> userDTO = getUserById(userId);
            if(userDTO.isPresent() ){
                if (userDTO.get().isLocked()) {

                    throw new LockedAccountException("There have been too many unsuccessful login-attempts on account with id :" + userId + "\n Please contact your bank : " + getBankNameAsStaticMethod());

                } else {
                    boolean isAuthenticated = userService.credentialsMatch(userId, pinCode);
                    if (isAuthenticated) {
                        userService.resetFailedAttempts(userId);
                        return isAuthenticated;

                    }
                    else {
                        int failedAttempts = userService.incrementFailedAttempts(userId);
                        if (failedAttempts == 3) {
                            throw new LockedAccountException("There have been too many unsuccessful login-attempts on account with id :" + userId + "\n Please contact your bank : " + getBankNameAsStaticMethod());
                        }
                        return isAuthenticated;
                    }
                }
            }
            else{
                return false;
            }

        } else {
            //User do not exist, authentication fails
            return false;
        }

    }

    /**
     * Makes a deposit into the user´s account with the specified amount
     *
     * @param userId          - userId of an authenticated user
     * @param amountToDeposit - The amount to deposit into the user account
     * @return - The new account balance after deposit
     * @throws InvalidInputException - on depositAmount == {@link Double#MAX_VALUE}
     *                               or negative deposit amount
     */
    @Override
    public double makeDeposit(String userId, double amountToDeposit) {
        boolean isValidDepositAmount = (amountToDeposit > 0 && amountToDeposit < Double.MAX_VALUE);
        if (isValidDepositAmount) {
            return userService.deposit(userId, amountToDeposit);
        } else {
            if (amountToDeposit == Double.MAX_VALUE) {
                throw new InvalidInputException("Wow, Mr Moneybags over here - That amount is litterarly" +
                        " the capacity of our current data storage for a single value. Please contact your bank :" + getBankNameAsStaticMethod() + " to get more information.");
            } else {
                throw new InvalidInputException("A deposit amount can not be negative. If you wish to withdraw money please choose the 'withdraw'-option. Otherwise, please input a positive deposit-number and try again");
            }

        }
    }

    /**
     * Withdraws the specified amount from the user´s account and returns the new balance
     *
     * @param userId           - The id of an authenticated user for the specific bank
     * @param amountToWithdraw - Amount to withdraw from that user´s account
     * @return The new account balance after withdrawal
     * @throws InvalidInputException on withdrawalAmount > account funds, on negative input and
     *                               userId could not be found in persistence layer, on account balance == 0
     */
    @Override
    public double makeWithdrawal(String userId, double amountToWithdraw) {

        Optional<UserEntity> userById = userService.getUserById(userId);
        if (userById.isPresent()) {
            double accountBalance = userById.get().getBalance();
            boolean isValidInput = (amountToWithdraw <= accountBalance && amountToWithdraw > 0);

            if (isValidInput) {
                return userService.withdraw(userId, amountToWithdraw);
            } else {
                if (amountToWithdraw < 0) {
                    throw new InvalidInputException("Withdrawal amount can not be negative. If you want to deposit money, please try the deposit function.");

                } else {
                    if (accountBalance == 0) {
                        throw new InvalidInputException("You currently have no funds in your account and consequently you can not make a withdrawal at this time. Please try again after next deposit");
                    } else {
                        throw new InvalidInputException("Withdrawal amount can not be more than current account balance, please try again with a value less than : " + accountBalance);
                    }
                }
            }

        } else {
            throw new InvalidInputException("The user with userId : " + userId + "could not be found. Please contact your bank : " + getBankNameAsStaticMethod() + " for more information");
        }

    }

    /**
     *
     * @param userId - Card number of user
     * @throws InvalidInputException on userId not following cardNumberFormat
     * @return true if user exist
     */
    @Override
    public boolean isExistingUser(String userId) {

        if(!this.cardNumberFollowsFormat(userId)) throw new InvalidInputException("The user with userId : " + userId + "could not be found. Please contact your bank : " + getBankNameAsStaticMethod() + " for more information");


        return userService.isExistingUser(userId);
    }

    /**
     * Gets UserDTO representing the UserEntity-instance (w/o pin-code and not exposing any UserEntity-properties)
     * or null if none are found
     *
     * @param id - The string specifying a user´s card number
     * @return A {@link UserDTO UserDTO-instance} specifying the user´s data if user exists in bank api-system, else null
     */
    @Override
    public Optional<UserDTO> getUserById(String id) {
        Optional<UserEntity> userById = userService.getUserById(id);
        if (userById.isPresent()) {
            UserEntity userEntity = userById.get();
            return Optional.ofNullable(UserDTO.builder().id(userEntity.getId())
                    .accountBalance(userEntity.getBalance())
                    .failedAttmpts(userEntity.getFailedAttempts())
                    .isLocked(userEntity.isLocked())
                    .build());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean isCardLocked(String userId) {
        return this.isExistingUser(userId);
    }

    @Override
    public abstract String getBankNameAsStaticMethod();

    @Override
    public abstract boolean cardNumberFollowsFormat(String cardNumber);
}
