package org.example.moneymachine.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@MappedSuperclass
public abstract class BankApiEntity<id extends Object> {
    @Id
    protected  id id;
    public BankApiEntity(id id) {
        this.id = id;
    }



    public abstract id getId();
}
