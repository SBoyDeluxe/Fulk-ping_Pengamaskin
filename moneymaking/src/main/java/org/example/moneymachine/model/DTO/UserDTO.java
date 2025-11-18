package org.example.moneymachine.model.DTO;

import lombok.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * A DTO of the user class that does not expose the pin code and offers no setters without interaction with the bank api
 */
@Builder
@AllArgsConstructor
@Data
@Component
@NoArgsConstructor
public  class UserDTO {
    private  String id;
    private  double accountBalance;
    private  int failedAttmpts;
    private  boolean isLocked;




    @Override
    public String toString() {
        return "UserDTO[" +
                "id: " + id + ", " +
                "accountBalance: " + accountBalance + ", " +
                "failedAttmpts: " + failedAttmpts + ", " +
                "isLocked: " + isLocked + ']';
    }

    public UserDTO(String id) {
        this(id, -10, 0, false);
    }

    public String id() {
        return id;
    }

    public double accountBalance() {
        return accountBalance;
    }

    public int failedAttmpts() {
        return failedAttmpts;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (UserDTO) obj;
        return Objects.equals(this.id, that.id) &&
                Double.doubleToLongBits(this.accountBalance) == Double.doubleToLongBits(that.accountBalance) &&
                this.failedAttmpts == that.failedAttmpts &&
                this.isLocked == that.isLocked;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountBalance, failedAttmpts, isLocked);
    }

}
