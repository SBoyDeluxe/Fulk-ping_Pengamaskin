package org.example.moneymachine.service;

import org.example.moneymachine.*;
import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.controller.UI.*;
import org.example.moneymachine.model.DTO.*;
import org.example.moneymachine.model.entity.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ATMControllerTest {

    private ATMService atmService;
    private UserInterface userInterface;
    private ATMController atmController;
    private Scanner scannerMock;

    @BeforeAll
    void setUp() {
        ATMTestConfig atmTestConfig = new ATMTestConfig();

        atmService =  atmTestConfig.ATM();
        scannerMock = mock(Scanner.class);
        userInterface = new UserInterface(scannerMock, new StringBuilder());
        atmController = new ATMController(atmService, userInterface);
   }

    @Test
    @DisplayName("Show welcome message")
    void showWelcomeMessage(){

        MockedStatic<MockBank> mockBankMockedStatic = mockStatic(MockBank.class);
        MockedStatic<MasterCardBank> masterCardBankMockedStatic = mockStatic(MasterCardBank.class);

        MockBank mockBank = (MockBank) atmService.getConnectedBanks().get(0);
        MasterCardBank masterCardBank = (MasterCardBank) atmService.getConnectedBanks().get(1);


        when(masterCardBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        when(MasterCardBank.getBankName()).thenReturn("Mastercard");
        when(mockBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        when(MockBank.getBankName()).thenReturn("MockBank");

        atmController.startMenu();

        masterCardBankMockedStatic.verify(MasterCardBank::getBankName);
        mockBankMockedStatic.verify(MockBank::getBankName);

        mockBankMockedStatic.close();
        masterCardBankMockedStatic.close();




    }




    @AfterEach
    void tearDown() {
    }
}