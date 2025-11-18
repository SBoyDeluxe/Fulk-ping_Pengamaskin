package org.example.moneymachine.banks.superclasses;

import org.example.moneymachine.banks.interfaces.*;
import org.example.moneymachine.model.entity.service.*;

public abstract class IntegratedAPIBank extends FunctionalAPIBank implements APIBankInterface, CardProvider {

    /**
     * Responsible for validating that any given userId belongs to its userbase and calling the
     * persistence services to perform the actions associated with the user interface
     */
    public IntegratedAPIBank(String bankName, UserService userService) {
        super(bankName, userService);
    }

    public static String getBankName() {
        return "";
    }

    @Override
    public abstract boolean cardNumberFollowsFormat(String cardNumber);

    @Override
    public abstract String getBankNameAsStaticMethod();
}
