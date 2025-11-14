package org.example.moneymachine.banks;

import lombok.*;
import org.springframework.stereotype.*;

/**
 * This enum lines up with the order of the connected banks such that indexOf(matchedBank) + 1 = Ordinal(APIBankEnum)
 */
@AllArgsConstructor
public enum APIBankEnum {
    NONE,MOCKBANK,MASTERCARD;

}
