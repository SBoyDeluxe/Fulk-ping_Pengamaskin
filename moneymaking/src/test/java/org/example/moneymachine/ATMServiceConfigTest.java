package org.example.moneymachine;

import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.banks.superclasses.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@Import(ATMConfig.class)
class ATMServiceConfigTest {


    @Autowired
    ATMConfig atmConfig;
    @Autowired
    private MockBank mockBank;
    @Autowired
    private MasterCardBank masterCardBank;
    @Autowired
    private List<IntegratedAPIBank> apiBankList;

    @Test
    void ATM() {

        assertEquals(new ATMService(apiBankList), atmConfig.ATM());
    }

    @Test
    void getMockBank() {

        assertEquals(atmConfig.getMockBank(), mockBank);
    }

    @Test
    void getMasterCardBank() {
        assertEquals(atmConfig.getMasterCardBank(), masterCardBank);
    }

    @Test
    void getApiBankList() {
        assertEquals(atmConfig.getApiBankList(), apiBankList);;
    }

    @Test
    void setApiBankList() {
    }
}