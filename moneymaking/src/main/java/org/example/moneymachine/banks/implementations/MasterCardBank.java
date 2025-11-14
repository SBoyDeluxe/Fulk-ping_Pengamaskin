package org.example.moneymachine.banks.implementations;

import org.example.moneymachine.banks.*;
import org.example.moneymachine.model.DTO.*;
import org.example.moneymachine.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Service
public class MasterCardBank extends APIBank implements APIBankInterface {
    public MasterCardBank() {
        super("Mastercard", null);
    }

    @Override
    public boolean cardNumberFollowsFormat(String cardNumber) {
        boolean firstSixCharsFollows = false;
        String startNumbers = cardNumber.substring(0, 4);
        //2221–2720 - First numbers in this range, 51–55 last 2 in this range
        int parsedInt = Integer.parseInt(startNumbers);
        boolean firstFourCorrect = (parsedInt - 2221) > 0 && (2720 - parsedInt < 519);

        String endingNumbers =cardNumber.substring(4,6);
        int parsedInt2 = Integer.parseInt(endingNumbers);

        boolean lastTwoCorrect = (parsedInt2 - 51 > 0) && (55 - parsedInt2 <= 4);
        firstSixCharsFollows = lastTwoCorrect && firstFourCorrect;
        //If 8 first chars follows all we need to do next is check length
        boolean correctLength = cardNumber.length() == 16;

        return (correctLength && firstSixCharsFollows);
    }

    @Override
    public UserDTO getUserById(String id) {
        return null;
    }

    @Override
    public boolean isCardLocked(String userId) {
        return false;
    }
    public static String getBankName(){
        return "Mastercard";
    }
    @Override
    public String getBankNameAsStaticMethod() {
        return MasterCardBank.getBankName();
    }
}
