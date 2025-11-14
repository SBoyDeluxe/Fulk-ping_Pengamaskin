package org.example.moneymachine.banks.implementations;

import org.example.moneymachine.banks.superclasses.*;
import org.example.moneymachine.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Service
public class MockBank extends IntegratedAPIBank{


    @Autowired
    public MockBank(UserService userService) {
        super("MockBank", userService);
    }


    @Override
    public String getBankNameAsStaticMethod() {
       return MockBank.getBankName();
    }

    @Override
    public boolean cardNumberFollowsFormat(String cardNumber) {
            boolean firstEightCharsFollows = false;
        String startNumbers = cardNumber.substring(0, 8);
        firstEightCharsFollows = (startNumbers.equals("12341234"))
                ? true : false;
        //If 8 first chars follows all we need to do next is check length
        boolean correctLength = cardNumber.length() == 16;

        return (correctLength && firstEightCharsFollows);
    }

    public static String getBankName(){
       return  "Mockbank";
    }
}
