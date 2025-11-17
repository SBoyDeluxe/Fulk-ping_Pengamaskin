package org.example.moneymachine.service;

import org.example.moneymachine.*;
import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.controller.UI.*;
import org.example.moneymachine.exceptions.*;
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
   //
//    private UserService userService;

    @BeforeEach
    void setUp() {
        ATMTestConfig atmTestConfig = new ATMTestConfig();

        atmService =  atmTestConfig.ATMWithMockedBanks();
//        userService = atmTestConfig.getUserService();
        scannerMock = mock(Scanner.class);
        userInterface = new UserInterface(scannerMock, new StringBuilder());
        atmController = new ATMController(atmService, userInterface);
           }

    @Test
    @Order(1)
    @DisplayName("Show welcome message")
    void showWelcomeMessage(){


        MockBank mockBank = (MockBank) atmService.getConnectedBanks().get(0);
        MasterCardBank masterCardBank = (MasterCardBank) atmService.getConnectedBanks().get(1);


        when(masterCardBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        when(mockBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        try(MockedStatic<MockBank>      mockBankMockedStatic = mockStatic(MockBank.class);
            MockedStatic<MasterCardBank> masterCardBankMockedStatic = mockStatic(MasterCardBank.class);
        ) {
            when(MockBank.getBankName()).thenReturn("MockBank");
            when(MasterCardBank.getBankName()).thenReturn("Mastercard");

            atmController.startMenu();
            masterCardBankMockedStatic.verify(MasterCardBank::getBankName);
            mockBankMockedStatic.verify(MockBank::getBankName);
        }







    }
    @ParameterizedTest
    @Order(1)
    @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/users.csv")
    @DisplayName("Card insertion with valid mockbank user id")
    void cardInsertion_WithValidMockBankUsers(String id, String pin, double balance, int failedAttempts, boolean isLocked){

        //Create userDTO to be returned on atmService.getUserById
        UserDTO userDTO = UserDTO.builder().id(id).isLocked(false).build();

        MockBank mockBank = (MockBank) atmService.getConnectedBanks().get(0);
        MasterCardBank masterCardBank = (MasterCardBank) atmService.getConnectedBanks().get(1);

        when(mockBank.cardNumberFollowsFormat(id)).thenCallRealMethod();
        when(masterCardBank.cardNumberFollowsFormat(id)).thenReturn(false);

        when(mockBank.getUserById(id)).thenReturn(Optional.of(userDTO));

        when(mockBank.authenticateUserLogin(id, pin)).thenReturn(true);
        //Mock pinInput
        when(scannerMock.nextLine()).thenReturn(pin).thenReturn("y");




        boolean loginSuccess = atmController.onCardInsertion(id);


        assertTrue(loginSuccess);
        verify(mockBank,times(2)).getUserById(id);
        verify(mockBank).cardNumberFollowsFormat(id);


    }
    @ParameterizedTest
    @Order(2)
    @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/mastercardusers.csv")
    @DisplayName("Card insertion with valid mastercard user id")
    void cardInsertion_WithValidMastercardBankUsers(String id,int failedAttempts , double balance, String pin, boolean isLocked){

        //Create userDTO to be returned on atmService.getUserById
        UserDTO userDTO = UserDTO.builder().id(id).isLocked(false).build();

        MockBank mockBank = (MockBank) atmService.getConnectedBanks().get(0);
        MasterCardBank masterCardBank = (MasterCardBank) atmService.getConnectedBanks().get(1);

        when(masterCardBank.cardNumberFollowsFormat(id)).thenCallRealMethod();
        when(mockBank.cardNumberFollowsFormat(id)).thenReturn(false);

        when(masterCardBank.getUserById(id)).thenReturn(Optional.of(userDTO));

        when(masterCardBank.authenticateUserLogin(id, pin)).thenReturn(true);
        //Mock pinInput
        when(scannerMock.nextLine()).thenReturn(pin).thenReturn("y");

        boolean loginSuccess = atmController.onCardInsertion(id);


        assertTrue(loginSuccess);
        verify(masterCardBank,times(2)).getUserById(id);
        verify(masterCardBank).cardNumberFollowsFormat(id);


    }
    @ParameterizedTest
    @Order(3)
    @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/mastercardusers.csv")
    @DisplayName("Card insertion with invalid mastercard user id")
    void cardInsertion_WithLockedUser(String id,int failedAttempts , double balance, String pin, boolean isLocked){

        //Create userDTO to be returned on atmService.getUserById
        UserDTO userDTO = UserDTO.builder().id(id).isLocked(true).build();

        MockBank mockBank = (MockBank) atmService.getConnectedBanks().get(0);
        MasterCardBank masterCardBank = (MasterCardBank) atmService.getConnectedBanks().get(1);

        when(masterCardBank.cardNumberFollowsFormat(id)).thenCallRealMethod();
        when(mockBank.cardNumberFollowsFormat(id)).thenReturn(false);

        when(masterCardBank.getUserById(id)).thenReturn(Optional.of(userDTO));
        when(masterCardBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        when(mockBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        //On await confirmation
        when(scannerMock.nextLine()).thenReturn("").thenReturn("sss").thenReturn("ok");
        try(        MockedStatic<MockBank> mockBankMockedStatic                  = mockStatic(MockBank.class);
                    MockedStatic<MasterCardBank> masterCardBankMockedStatic = mockStatic(MasterCardBank.class);
        ) {
            when(MasterCardBank.getBankName()).thenReturn("Mastercard");
            when(MockBank.getBankName()).thenReturn("MockBank");

            atmController.onCardInsertion(id);
            verify(masterCardBank,times(1)).getUserById(id);
            verify(masterCardBank).cardNumberFollowsFormat(id);
            masterCardBankMockedStatic.verify(MasterCardBank::getBankName);

        }







    }

    @ParameterizedTest
    @Order(4)
    @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/users.csv")
    @DisplayName("Card insertion with valid mockbank user id")
    void cardInsertion_WithLockedMockBankUsers(String id, String pin, double balance, int failedAttempts, boolean isLocked){

        //Create userDTO to be returned on atmService.getUserById
        UserDTO userDTO = UserDTO.builder().id(id).isLocked(true).build();

        MockBank mockBank = (MockBank) atmService.getConnectedBanks().get(0);
        MasterCardBank masterCardBank = (MasterCardBank) atmService.getConnectedBanks().get(1);

        when(mockBank.cardNumberFollowsFormat(id)).thenCallRealMethod();
        when(masterCardBank.cardNumberFollowsFormat(id)).thenReturn(false);

        when(mockBank.getUserById(id)).thenReturn(Optional.of(userDTO));
        when(mockBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        when(masterCardBank.getBankNameAsStaticMethod()).thenCallRealMethod();
        //On await confirmation
        when(scannerMock.nextLine()).thenReturn("").thenReturn("sss").thenReturn("ok");
        try(        MockedStatic<MockBank> mockBankMockedStatic                  = mockStatic(MockBank.class);
                    MockedStatic<MasterCardBank> masterCardBankMockedStatic = mockStatic(MasterCardBank.class);
        ) {
            when(MasterCardBank.getBankName()).thenReturn("Mastercard");
            when(MockBank.getBankName()).thenReturn("MockBank");

            atmController.onCardInsertion(id);
            verify(mockBank,times(1)).getUserById(id);
            verify(mockBank).cardNumberFollowsFormat(id);
            masterCardBankMockedStatic.verify(MasterCardBank::getBankName, times(0));
            mockBankMockedStatic.verify(MockBank::getBankName);

        }
    }

        @Test
    @Order(5)
    @DisplayName("Card insertion with unknown card provider")
    void cardInsertion_UnknownCardProvider() {

            //Create userDTO to be returned on atmService.getUserById
            String invalidId = "139499534";
            UserDTO userDTO = UserDTO.builder().id(invalidId).build();

            MockBank mockBank = (MockBank) atmService.getConnectedBanks().get(0);
            MasterCardBank masterCardBank = (MasterCardBank) atmService.getConnectedBanks().get(1);

            when(mockBank.cardNumberFollowsFormat(invalidId)).thenCallRealMethod();
            when(masterCardBank.cardNumberFollowsFormat(invalidId)).thenCallRealMethod();


            //On await confirmation
            when(scannerMock.nextLine()).thenReturn("").thenReturn("sss").thenReturn("ok");


            atmController.onCardInsertion(invalidId);
//                assertThrows(InvalidInputException.class,
//                        ()->atmService.insertCard(invalidId));
                verify(mockBank).cardNumberFollowsFormat(invalidId);
                verify(masterCardBank).cardNumberFollowsFormat(invalidId);



        }







    @AfterEach
    void tearDown() {



    }
}