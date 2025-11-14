//package org.example.moneymachine;
//
//import lombok.*;
//import org.example.moneymachine.banks.*;
//import org.example.moneymachine.banks.implementations.*;
//import org.springframework.beans.factory.annotation.*;
//import org.springframework.context.annotation.*;
//
//import java.util.*;
//
//@Configuration
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//public class ATMConfig{
//
//    MockBank mockBank;
//    MasterCardBank masterCardBank;
//    private List<APIBank> apiBankList;
//
//
//    @Autowired
//    public ATMConfig(MockBank mockBank, MasterCardBank masterCardBank) {
//        this.apiBankList = List.of(this.mockBank,masterCardBank);
//        this.mockBank = mockBank;
//        this.masterCardBank = masterCardBank;
//    }
//    @Bean
//    public ATMConfig atmConfig1(MockBank mockBank, MasterCardBank masterCardBank) {
//        this.apiBankList = List.of(this.mockBank,masterCardBank);
//        this.mockBank = mockBank;
//        this.masterCardBank = masterCardBank;
//        return this;
//    }
//
//    @Bean
//    public ATM ATM(){
//        this.apiBankList = List.of(this.mockBank,masterCardBank);
//
//        return new ATM(apiBankList);
//    }
//
//}
