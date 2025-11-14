package org.example.moneymachine;

import lombok.*;
import org.example.moneymachine.banks.*;
import org.example.moneymachine.banks.implementations.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.*;

import java.util.*;

@TestConfiguration
@Getter
public class ATMTestConfig {


    private MockBank mockBank;

    private List<APIBank> apiBankList;

    @Autowired
    public ATMTestConfig() {
        mockBank = Mockito.mock(MockBank.class);
        apiBankList = List.of(mockBank);

    }

    @Bean
    public ATM ATM(){
        return new ATM(apiBankList);

    }
}
