package org.example.moneymachine.banks;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import(EnumConfig.class)
class EnumConfigTest {

    @Autowired
    EnumConfig enumConfig;

    @Test
    void none() {
        assertEquals(APIBankEnum.NONE, enumConfig.none());
    }

    @Test
    void mockBankEnum() {
        assertEquals(APIBankEnum.MOCKBANK, enumConfig.mockBankEnum());
    }

    @Test
    void masterCard() {
        assertEquals(APIBankEnum.MASTERCARD, enumConfig.masterCard());
    }
}