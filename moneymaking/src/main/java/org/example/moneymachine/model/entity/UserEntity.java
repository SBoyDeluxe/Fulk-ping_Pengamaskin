package org.example.moneymachine.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Getter
@Setter
@Entity
public class UserEntity extends BankApiEntity<String>  {


    private String id;
    private String pin;
    private double balance;
    private int failedAttempts;
    private boolean isLocked;
    @Autowired
    public UserEntity(double balance, int failedAttempts, String id, boolean isLocked, String pin) {
        super(id);
        this.balance = balance;
        this.failedAttempts = failedAttempts;
        this.id = super.id;
        this.isLocked = (this.failedAttempts == 3);
        this.pin = pin;
    }

    public UserEntity() {
        super("");
    }

    @Override
    public String getId() {
        return this.id;
    }
}
