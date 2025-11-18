package org.example.moneymachine.banks.superclasses;

import org.example.moneymachine.banks.interfaces.*;
import org.example.moneymachine.model.DTO.*;
import org.example.moneymachine.model.entity.service.*;

import java.util.*;


public abstract class APIBank implements APIBankInterface {
    protected UserService userService;
    protected String bankName;

    @Override
    public boolean authenticateUserLogin(String userId, String pinCode) {
        return false;
    }

    @Override
    public double makeDeposit(String userId, double amountToDeposit) {
        return 0;
    }

    @Override
    public double makeWithdrawal(String userId, double amountToWithdraw) {
        return 0;
    }

    @Override
    public boolean isExistingUser(String userId) {
        return false;
    }

    @Override
    public boolean cardNumberFollowsFormat(String cardNumber) {
        return false;
    }

    public APIBank(String bankName, UserService userService) {
        this.bankName = bankName;
        this.userService = userService;
    }

    public abstract Optional<UserDTO> getUserById(String id);


    public abstract boolean isCardLocked(String userId);
    /**
     * Gets name of the bank
     * @return The name of the bank as a result of a static call to the extending class
     */
    public abstract String getBankNameAsStaticMethod();

}
