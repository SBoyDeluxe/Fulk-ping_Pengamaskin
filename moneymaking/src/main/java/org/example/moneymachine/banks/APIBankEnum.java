package org.example.moneymachine.banks;

import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

/**
 * This enum lines up with the order of the connected banks such that indexOf(matchedBank) + 1 = Ordinal(APIBankEnum)
 */

public enum APIBankEnum {
    MASTERCARD(), MOCKBANK(), NONE();


    APIBankEnum() {
    }




    @Override
    public String toString() {
        return super.toString();
    }

}
