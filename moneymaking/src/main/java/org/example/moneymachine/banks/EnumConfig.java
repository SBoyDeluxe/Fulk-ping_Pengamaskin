package org.example.moneymachine.banks;

import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class EnumConfig {

    public EnumConfig() {
    }

    @Bean
    @Primary
    APIBankEnum none(){
       return APIBankEnum.NONE;
    }@Bean
    APIBankEnum mockBankEnum(){
       return APIBankEnum.MOCKBANK;
    }@Bean
    APIBankEnum masterCard(){
       return APIBankEnum.MASTERCARD;
    }

}
