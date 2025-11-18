package org.example.moneymachine;

import lombok.*;
import org.example.moneymachine.atm.*;
import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.banks.superclasses.*;
import org.example.moneymachine.model.entity.service.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.*;

import java.util.*;

@TestConfiguration
@Getter
public class ATMTestConfig {


    private MockBank mockBank;
  private MasterCardBank masterCardBank;
  private UserService userService;


    private List<IntegratedAPIBank> apiBankList;

    @Autowired
    public ATMTestConfig() {
        userService = Mockito.mock(UserService.class);
        mockBank = new MockBank(userService);
        masterCardBank = new MasterCardBank(userService);
        apiBankList = List.of(mockBank,masterCardBank);

    }

    @Bean
    public ATMService ATM(){
        return new ATMService(apiBankList);

    }

    @Bean
    public ATMService ATMWithMockedBanks(){
        mockBank = Mockito.mock(MockBank.class);
        masterCardBank = Mockito.mock(MasterCardBank.class);
        apiBankList = List.of(mockBank,masterCardBank);
        return new ATMService(apiBankList);
    }

}
