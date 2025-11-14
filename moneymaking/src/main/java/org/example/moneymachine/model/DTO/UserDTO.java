package org.example.moneymachine.model.DTO;

import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

/**
 * A DTO of the user class that does not expose the pin code and offers no setters without interaction with the bank api
 */
@Builder
@Component
public record UserDTO(String id, double accountBalance, int failedAttmpts, boolean isLocked) {

    @Autowired
    public UserDTO(String id, double accountBalance, int failedAttmpts, boolean isLocked) {
        this.id = id;
        this.accountBalance = accountBalance;
        this.failedAttmpts = failedAttmpts;
        this.isLocked = isLocked;
    }

    @Override
    public String toString() {
        return "UserDTO[" +
                "id: " + id + ", " +
                "accountBalance: " + accountBalance + ", " +
                "failedAttmpts: " + failedAttmpts + ", " +
                "isLocked: " + isLocked + ']';
    }
}
