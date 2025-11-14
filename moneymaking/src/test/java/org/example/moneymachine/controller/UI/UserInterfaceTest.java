package org.example.moneymachine.controller.UI;

import org.example.moneymachine.banks.*;
import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserInterfaceTest {

    private List<APIBank> connectedBanks;
    private UserInterface userInterface;
    private UserInterface userInterfaceMock;
    private List<String> loggedInMenuActions;
    MockedStatic<MockBank> mockBankMockedStatic;
    MockedStatic<MasterCardBank> masterCardBankMockedStatic;
    private MasterCardBank mcBank ;
    private MockBank bank         ;

    private Scanner mockScanner;
    private StringBuilder stringBuilder;
    @BeforeAll
    void setUp() {
         mcBank = mock(MasterCardBank.class);
         bank         = mock(MockBank.class);
        connectedBanks = List.of(bank, mcBank);
        stringBuilder = new StringBuilder();
        mockScanner = mock(Scanner.class);
        userInterface = new UserInterface(mockScanner, stringBuilder);
        userInterfaceMock = mock(UserInterface.class);
        loggedInMenuActions = List.of("Check balance","Make deposit","Make withdrawal","Exit");
    }

    @BeforeEach
    void setUpStatics(){
       masterCardBankMockedStatic = mockStatic(MasterCardBank.class);
        mockBankMockedStatic = mockStatic(MockBank.class);
    }
    @AfterEach
    void tearDownStatics(){
       masterCardBankMockedStatic.close();
        mockBankMockedStatic.close();
    }
    @DisplayName("Presents start menu with list of connected banks")
    @Test
    void startMenu() {
        masterCardBankMockedStatic.when(MasterCardBank::getBankName).thenReturn("Mastercard");
        mockBankMockedStatic.when(MockBank::getBankName).thenReturn("MockBank");


        when(bank.getBankNameAsStaticMethod()).thenCallRealMethod();
        when(mcBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        userInterface.startMenu(connectedBanks);
        masterCardBankMockedStatic.verify(MasterCardBank::getBankName);
        mockBankMockedStatic.verify(MockBank::getBankName);
    }
    @DisplayName("Should display list of logged in actions")
    @Test
    //@ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void loggedInMenu() {
        masterCardBankMockedStatic.when(MasterCardBank::getBankName).thenReturn("Mastercard");
        mockBankMockedStatic.when(MockBank::getBankName).thenReturn("MockBank");


        when(bank.getBankNameAsStaticMethod()).thenCallRealMethod();
        when(mcBank.getBankNameAsStaticMethod()).thenCallRealMethod();

    when(mockScanner.nextInt()).
            thenReturn(9)
                .thenReturn(2)
            .thenReturn(4)

            .thenReturn(1);
        int result1 = userInterface.loggedInMenu(loggedInMenuActions, MasterCardBank.getBankName());
        int result2 = userInterface.loggedInMenu(loggedInMenuActions, MockBank.getBankName());

        assertFalse(result2 > loggedInMenuActions.size());
        assertFalse(result1 > loggedInMenuActions.size());


    }

    @Test
    void displayError() {
        when(mockScanner.nextLine()).thenReturn("s")
                        .thenReturn("OK")
                .thenReturn("-2")
                .thenReturn("")
                .thenReturn("oK");
        userInterface.displayError(new LockedAccountException("Locked"));
        userInterface.displayError(new InvalidInputException("asfasfa"));
    }

    @Test
    void logoutConfirmation() {
        userInterface.logoutConfirmation();

    }

    @Test
    void getPinInput() {
        when(mockScanner.nextLine()).thenReturn("s")
                .thenReturn("OK")
                .thenReturn("-2")
                .thenReturn("")
                .thenReturn("oK")
                .thenReturn("2020").thenReturn("no")
                .thenReturn("2023").thenReturn("yes");
        String pinInput = userInterface.getPinInput();
        assertEquals("2023",pinInput);
    }
}