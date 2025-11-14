package org.example.moneymachine;

import lombok.*;
import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.banks.superclasses.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
@Getter
@Setter
public class ATMConfig{
    private final MockBank mockBank;

    private final MasterCardBank masterCardBank;

    private List<APIBank> apiBankList;


    @Autowired
    public ATMConfig( MockBank mockBank, MasterCardBank masterCardBank) {
        this.mockBank = mockBank;
        this.masterCardBank = masterCardBank;
        this.apiBankList = List.of(mockBank,masterCardBank);

    }


    @Bean
    public ATM ATM(){


        return new ATM(apiBankList);
    }

}
