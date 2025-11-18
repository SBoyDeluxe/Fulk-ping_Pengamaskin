package org.example.moneymachine.model.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.io.*;

@Getter
@Setter
@Entity
@DynamicUpdate
public class UserEntity extends BankApiEntity<String>  {


    private String id;
    private String pin;
    private double balance;
    private int failedAttempts;
    private boolean isLocked;
    @Autowired
    public UserEntity(double balance, int failedAttempts,  String id, boolean isLocked, String pin) {
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
