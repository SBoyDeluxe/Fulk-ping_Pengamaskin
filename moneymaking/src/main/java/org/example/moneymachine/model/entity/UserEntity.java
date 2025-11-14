package org.example.moneymachine.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;


@Entity
@Getter
@Setter
@Component
public class UserEntity extends BankApiEntity<String>  {


    private String id;
    private String pin;
    private Double balance;
    private Integer failedAttempts;
    private Boolean isLocked;
    @Autowired
    public UserEntity(double balance, int failedAttempts, String id, boolean isLocked, String pin) {
        super(id);
        this.balance = balance;
        this.failedAttempts = failedAttempts;
        this.id = id;
        this.isLocked = (this.failedAttempts == 3);
        this.pin = pin;
    }


    @Override
    public String getId() {
        return this.id;
    }
}
