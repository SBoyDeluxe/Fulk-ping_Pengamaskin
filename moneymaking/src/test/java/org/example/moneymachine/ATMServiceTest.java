package org.example.moneymachine;

import org.example.moneymachine.banks.*;
import org.example.moneymachine.banks.superclasses.*;
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
//@ContextConfiguration(classes = {ATMTestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ATMServiceTest {
    private List<UserEntity> userEntityList;
    private List<UserDTO> users;
    private List<String> validUserIds;
    private List<String> invalidUserIds;
    private ATMService atmService;
    private IntegratedAPIBank mockBank;

    @BeforeAll
    void setUp() {
        users = new ArrayList<>();
        userEntityList = new ArrayList<>();
        validUserIds = new ArrayList<>();
        invalidUserIds = new ArrayList<>();
//        ATMTestConfig atmTestConfig = new ATMTestConfig();

    }

    private void instantiateMockedServiceATM() {
        mockBank = Mockito.mock(IntegratedAPIBank.class);
        atmService = new ATMService(List.of( mockBank));
    }

    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SetUpLists{
        @Order(1)
        @ParameterizedTest
        @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/users.csv")
        void  SetUpValidUserIds(String userId){
            int initialSize = validUserIds.size();

            validUserIds.add(new String(userId));
            assertFalse(validUserIds.isEmpty());
            assertEquals(validUserIds.size(), initialSize + 1);
        }
        @Order(2)
        @ParameterizedTest
        @CsvFileSource(files = {"src/test/java/org/example/moneymachine/repository/csv/invalidMockBankCrdNmbrs.csv"})
        void  SetUpInValidUserIds(String userId){
            int initialSize = invalidUserIds.size();

            invalidUserIds.add(new String(userId));
            assertFalse(invalidUserIds.isEmpty());
            assertEquals(invalidUserIds.size(), initialSize + 1);
        }
        @Order(3)
        @ParameterizedTest
        @CsvFileSource(files = {"src/test/java/org/example/moneymachine/repository/csv/users.csv"})
        void  SetUpUserList(String userId, String pin, double balance, int failedAttempts, boolean isLocked){
            int initialSize = users.size();

            users.add(UserDTO.builder()
                            .id(userId)
                            .failedAttmpts(failedAttempts)
                            .accountBalance(balance)
                            .isLocked(isLocked)
                    .build());
            assertFalse(users.isEmpty());
            assertEquals(users.size(), initialSize + 1);

             initialSize = userEntityList.size();

            userEntityList.add(new UserEntity(balance, failedAttempts, userId, isLocked, pin));
            assertFalse(userEntityList.isEmpty());
            assertEquals(userEntityList.size(), initialSize + 1);
        }




    }

    @Nested
    @Order(2)
    class FunctionalityTest{

        private MockedStatic<IntegratedAPIBank> mockedStatic;
        @BeforeEach
        void setUp() {
            mockedStatic = mockStatic(IntegratedAPIBank.class);
            instantiateMockedServiceATM();

        }

        @AfterEach
        void tearDown() {
            mockedStatic.close();
        }

        @Order(1)
        @DisplayName("Card insertion")
        @Test
        void insertCard() {
            //Get inputs : Valid, locked and not valid
            String randomInvalidUserId = getRandomInvalidUserId();
            String randomValidUserId = getRandomValidUserId();
            UserDTO randomUser = getRandomUser();
            //Make sure valid user is not locked
            while(Objects.equals(randomUser.id(), randomValidUserId)){
                randomUser = getRandomUser();
            }

            UserDTO lockedUser = UserDTO.builder()
                    .id(randomUser.id())
                    .failedAttmpts(3)
                    .isLocked(true)
                    .build();



                mockedStatic.when(IntegratedAPIBank::getBankName).thenReturn("MockBank");
                when(mockBank.cardNumberFollowsFormat(randomValidUserId)).thenReturn(true);
                when(mockBank.cardNumberFollowsFormat(lockedUser.id())).thenReturn(true);
                when(mockBank.cardNumberFollowsFormat(randomInvalidUserId)).thenReturn(false);

                when(mockBank.getBankNameAsStaticMethod()).then(invocation -> IntegratedAPIBank.getBankName());

                when(mockBank.getUserById(randomValidUserId)).thenReturn(
                        UserDTO.builder()
                                .id(randomValidUserId)
                                .build()
                );
                when(mockBank.getUserById(lockedUser.id())).thenReturn(
                        lockedUser
                );


                assertThrows(InvalidInputException.class, ()-> atmService.insertCard(randomInvalidUserId));
            verify(mockBank).cardNumberFollowsFormat(randomInvalidUserId);

            assertThrows(LockedAccountException.class, ()-> atmService.insertCard(lockedUser.id()));
            verify(mockBank).cardNumberFollowsFormat(lockedUser.id());
            verify(mockBank).getUserById(lockedUser.id());

            boolean validResult = atmService.insertCard(randomValidUserId);
            verify(mockBank).cardNumberFollowsFormat(randomValidUserId);
            verify(mockBank).getUserById(randomValidUserId);


            assertTrue(validResult);

                mockedStatic.verify(IntegratedAPIBank::getBankName);




        }

        @Order(2)
        @Test
        @DisplayName("Handle pin-entry")
        void enterPin() {

            //Set up atm to mirror starting point for pin-entry : Selected bank-enum set, currentUser set and currentBank set
            setUpATMWithRandomCurrentUserAndMockedBank();
            Optional<UserDTO> currentUser = atmService.getCurrentUser();

            //Get different inputs
            UserEntity currentUserEntity = userEntityList.stream().filter((userEntity -> Objects.equals(userEntity.getId(), currentUser.get().id()))).toList().get(0);
            UserDTO currentUserCompleteDTO = users.stream().filter((userEntity -> Objects.equals(userEntity.id(), currentUser.get().id()))).toList().get(0);
            UserEntity isLockedUserEntity = getUserEntityWithIdOtherThanCurrentUser(currentUserEntity);

            isLockedUserEntity.setFailedAttempts(3);
            isLockedUserEntity.setLocked(true);
            String validPin = currentUserEntity.getPin();

            String invalidPinInput = "asjfnajksnfkja";


            // Mock internal calls to MockBank instance - getUserById and authenticateUserLogin
            when(mockBank.getUserById(currentUser.get().id())).thenReturn(
                currentUserCompleteDTO
            );
            when(mockBank.getUserById(isLockedUserEntity.getId())).thenReturn(
                UserDTO.builder().id(isLockedUserEntity.getId())
                        .accountBalance(isLockedUserEntity.getBalance())
                        .failedAttmpts(isLockedUserEntity.getFailedAttempts())
                        .isLocked(isLockedUserEntity.isLocked())
                        .build()
            );
            when(mockBank.authenticateUserLogin(currentUser.get().id(),validPin)).thenReturn(true);
            when(mockBank.authenticateUserLogin(currentUser.get().id(),invalidPinInput)).thenReturn(false);
            when(mockBank.authenticateUserLogin(isLockedUserEntity.getId(),isLockedUserEntity.getPin())).thenThrow(LockedAccountException.class);

            boolean success = atmService.enterPin(validPin);
            verify(mockBank).getUserById(currentUser.get().id());
            verify(mockBank).authenticateUserLogin(currentUser.get().id(),validPin);


            //Assert results
            assertTrue(success);

            boolean expectedFailure = atmService.enterPin(invalidPinInput);
            assertFalse(expectedFailure);
            verify(mockBank).authenticateUserLogin(currentUser.get().id(),invalidPinInput);

            atmService.setCurrentUser(Optional.ofNullable(UserDTO.builder().id(isLockedUserEntity.getId()).build()));
              assertThrows(LockedAccountException.class,()-> atmService.enterPin(isLockedUserEntity.getPin()));
            verify(mockBank).getUserById(isLockedUserEntity.getId());
            verify(mockBank).authenticateUserLogin(isLockedUserEntity.getId(),isLockedUserEntity.getPin());









        }

        private UserEntity getUserEntityWithIdOtherThanCurrentUser(UserEntity currentUserEntity) {
            UserEntity isLockedUserEntity = getRandomUserEntity();

            //Make sure we don´t have user collision
            while(isLockedUserEntity.getId() == currentUserEntity.getId()){
                isLockedUserEntity = getRandomUserEntity();
            }
            return isLockedUserEntity;
        }

        /**
         * Sets up currentUser with full valid attributes
         */
        private void setUpATMWithRandomCurrentUserAndMockedBank() {
            UserDTO randomUser = getRandomUser();
            atmService.setCurrentUser(Optional.ofNullable(randomUser));
            atmService.setSelectedBankEnum(APIBankEnum.MOCKBANK);
        }

        /**
         * Sets currentUser-property to mirror unauthenticated user (when pin has not been verified,
         * then userId is set but accountBalance and failed attempts both are set to -10)
         */
        private void setUpATMWithRandomUnauthenticatedUserIdAndMockedBank() {
            UserDTO randomUser = getRandomUser();
            atmService.setCurrentUser(Optional.of(new UserDTO(randomUser.id(), -10, -10, false)));
            atmService.setSelectedBankEnum(APIBankEnum.MOCKBANK);
        }
        @DisplayName("Check balance of authed user, throw error otherwise")
        @Order(3)
        @Test
        void checkBalance() {

            //Check if bank not set up
            setUpATMWithRandomUnauthenticatedUserIdAndMockedBank();

            System.out.println(atmService.getCurrentUser() + "   : " + atmService.getSelectedBankEnum());

            assertThrows(NotLoggedInException.class, ()-> atmService.checkBalance());

            setUpATMWithRandomCurrentUserAndMockedBank();

            UserDTO currentUser = atmService.getCurrentUser().get();
            

            double balance = atmService.checkBalance();

            assertEquals(atmService.getCurrentUser().get().accountBalance(), balance);

        }



        @Order(4)
        @DisplayName("Deposit money into user´s account, throw exception on negative amount and non auth user")
        @Test
        void deposit() {
            setUpATMWithRandomUnauthenticatedUserIdAndMockedBank();
            //Check so error is thrown on not authed user and negative deposit amount

            assertThrows(NotLoggedInException.class, ()-> atmService.deposit(199));
            setUpATMWithRandomCurrentUserAndMockedBank();
            UserDTO currentUser = atmService.getCurrentUser().get();

            when(mockBank.makeDeposit(currentUser.id(), -10)).thenThrow(InvalidInputException.class);
            assertThrows(InvalidInputException.class, ()-> atmService.deposit(-10));
            verify(mockBank).makeDeposit(currentUser.id(), -10);

            // Make deposit and get resulting new balance
            double balanceBeforeDeposit = currentUser.accountBalance();


            double amount = 100;
            when(mockBank.makeDeposit(currentUser.id(), amount)).thenReturn(balanceBeforeDeposit + amount);
            double newBalance = atmService.deposit(amount);
            verify(mockBank).makeDeposit(currentUser.id(), amount);

            assertEquals(balanceBeforeDeposit +100, newBalance);

        }
        @Order(5)
        @DisplayName("Withdraw money from user´s account, throw exception on negative amount or non auth user")
        @Test
        void withdraw() {

            setUpATMWithRandomUnauthenticatedUserIdAndMockedBank();
            //Check so error is thrown on not authed user and negative deposit amount

            assertThrows(NotLoggedInException.class, ()-> atmService.withdraw(199));
            setUpATMWithRandomCurrentUserAndMockedBank();
            Optional<UserDTO> currentUser = atmService.getCurrentUser();

            when(mockBank.makeWithdrawal(currentUser.get().id(), -10)).thenThrow(InvalidInputException.class);
            assertThrows(InvalidInputException.class, ()-> atmService.withdraw(-10));
            verify(mockBank).makeWithdrawal(currentUser.get().id(), -10);

            // Make withdraw() and get resulting new balance
            double balanceBeforeDeposit = currentUser.get().accountBalance();


            double amount = 100;
            when(mockBank.makeWithdrawal(currentUser.get().id(), amount)).thenReturn(balanceBeforeDeposit - amount);
            double newBalance = atmService.withdraw(amount);
            verify(mockBank).makeWithdrawal(currentUser.get().id(), amount);

            assertEquals(balanceBeforeDeposit - 100, newBalance);
        }

        @Order(6)
        @DisplayName("User log out")
        @Test
        void sessionExit() {
            setUpATMWithRandomUnauthenticatedUserIdAndMockedBank();
            atmService.sessionExit();
            assertEquals(atmService.getCurrentUser().isEmpty(),true);
            assertEquals(atmService.getCurrentBank().isEmpty(),true);
            assertEquals(atmService.getSelectedBankEnum(),APIBankEnum.NONE);

        }

    }

    private void handleFailedLogin(UserDTO userDTO){
        //Increment failed attempts by one
        int newFailedAttempts = userDTO.failedAttmpts() + 1;
        userDTO = UserDTO.builder()
                .id(userDTO.id())
                .accountBalance(userDTO.accountBalance())
                .failedAttmpts(newFailedAttempts)
                .isLocked(newFailedAttempts == 3)
                .build();
    }

    private UserEntity getRandomUserEntity() {
        int randomUserIndex = getRandomIndex(userEntityList.size());

        UserEntity aUserEntity = userEntityList.get(randomUserIndex);
        return aUserEntity;
    }
    private UserDTO getRandomUser() {
        int randomUserIndex = getRandomIndex(users.size());

        UserDTO aUserDto = users.get(randomUserIndex);
        return aUserDto;
    }
    private String getRandomValidUserId() {
        int randomUserIndex = getRandomIndex(validUserIds.size());

        String validUserId = validUserIds.get(randomUserIndex);
        return validUserId;
    }
    private String getRandomInvalidUserId() {
        int randomUserIndex = getRandomIndex(invalidUserIds.size());

        String invalidUserId = invalidUserIds.get(randomUserIndex);
        return invalidUserId;
    }
    /**
     * Gets a random index in list
     * @param listLength - The highest unoccupied element index
     * @return A random index in the list
     */
    public int getRandomIndex(int listLength){

        return  (int) Math.floor(Math.random() * (listLength-1));
    }
}
