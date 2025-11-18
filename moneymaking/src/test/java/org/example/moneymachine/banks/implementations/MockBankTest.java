package org.example.moneymachine.banks.implementations;

import static org.junit.jupiter.api.Assertions.*;

import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.exceptions.*;
import org.example.moneymachine.model.DTO.*;
import org.example.moneymachine.model.entity.*;
import org.example.moneymachine.model.entity.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class MockBankTest {



    private List<UserEntity> users;

    @BeforeAll
    void setUp() {
        users = new ArrayList<>();



    }




    @Order(1)
    @Nested
    class setUpUserEntitesList{


        @Order(1)
        @ParameterizedTest
        @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/users.csv")
        void UserEntityList_Initialize(String id, String pin, double balance, int failedAttempts, boolean isLocked) {

            users.add(new UserEntity(balance, failedAttempts, id, isLocked, pin));
        }
        public static String getStringPresentationOfUserEntity(UserEntity userEntity) {
            return new String("\n\t Id : " + userEntity.getId() + "\n\t Balance :" + userEntity.getBalance() + "\n\t isLocked :" + userEntity.isLocked() + "\n\t Pin: " + userEntity.getPin() + "\n\t Failed attempts : " + userEntity.getFailedAttempts() + "\n\n");
        }
    }
    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class FunctionalityTest{
        private UserService userServiceMock;

        private MockBank mockOfMockBank;

        @BeforeEach
        void setUp() {
            userServiceMock = mock(UserService.class);
            mockOfMockBank = new MockBank(userServiceMock);

        }


        @AfterEach
        void tearDown() {

        }

        @Order(1)
        @DisplayName("Check if user exists in bank persistence layer")
        @Test
        void isExistingUser() {
            //Valid input
            String validId = getRandomUser().getId();
            String invalidId = "asfnjansf";

            Mockito.when(userServiceMock.isExistingUser(validId))
                    .thenReturn(true);
            boolean existingUser = mockOfMockBank.isExistingUser(validId);

            Mockito.verify(userServiceMock, Mockito.times(1)).isExistingUser(validId);


            assertTrue(existingUser);


            assertThrows(InvalidInputException.class,()->mockOfMockBank.isExistingUser(invalidId));


        }
        @Order(2)
        @DisplayName("Gets user by id")
        @Test
        void getUserById() {
            UserEntity randomUser = getRandomUser();
            Mockito.when(userServiceMock.getUserById(randomUser.getId())).thenReturn(Optional.of(randomUser));
            Optional<UserDTO> userDTO = mockOfMockBank.getUserById(randomUser.getId());
            assertFalse(userDTO.isEmpty());
            System.out.println(userDTO.toString());
        }


        private static void resetRandomUser1Attempts(UserEntity randomUser1) {
            randomUser1.setFailedAttempts(0);
        }
        @Order(4)
        @DisplayName("Make deposit into user account")
        @Test
        void makeDeposit() {

            UserEntity randomUser = getRandomUser();
            double oldBalance = randomUser.getBalance();
            double maxVal = Double.MAX_VALUE;
            double negativeDeposit = Double.MAX_VALUE+1;

            Mockito.when(userServiceMock.deposit(randomUser.getId(), 2000.05))
                    .thenReturn(oldBalance + 2000.05);

            double newBalance = mockOfMockBank.makeDeposit(randomUser.getId(), 2000.05);

            assertEquals(oldBalance + 2000.05, newBalance);

            assertThrows(InvalidInputException.class, ()->mockOfMockBank.makeDeposit(randomUser.getId(),maxVal));
            assertThrows(InvalidInputException.class, ()->mockOfMockBank.makeDeposit(randomUser.getId(),negativeDeposit));

        }
        @Order(5)
        @DisplayName("Withdraw from account, throw error on negative withdrawal and value exceeding account balance ")
        @Test
        void makeWithdrawal() {
            UserEntity randomUser = getRandomUser();

            double amountToWithdraw = randomUser.getBalance() - 2;
            Mockito.when(userServiceMock.getUserById(randomUser.getId())).thenReturn(Optional.of(randomUser));
            Mockito.when(userServiceMock.withdraw(randomUser.getId(), amountToWithdraw)).thenReturn(2.);
            double newBalance = mockOfMockBank.makeWithdrawal(randomUser.getId(), amountToWithdraw);

            assertEquals(2, newBalance);

            //Withdraw more than balance
            assertThrows(InvalidInputException.class, ()-> mockOfMockBank.makeWithdrawal(randomUser.getId(), randomUser.getBalance() +12));

            assertThrows(InvalidInputException.class, ()->mockOfMockBank.makeWithdrawal(randomUser.getId(), -30));
            //Assert throw on zero funds
            randomUser.setBalance(0.);
            assertThrows(InvalidInputException.class, ()->mockOfMockBank.makeWithdrawal(randomUser.getId(), 3));

        }
        @Order(6)
        @Test
        @DisplayName("Get bank name via static method")
        void getBankName() {
            assertEquals("MockBank", MockBank.getBankName());
        }

        @Order(7)
        @ParameterizedTest
        @DisplayName("Tests with card numbers following schema")
        @CsvFileSource(numLinesToSkip = 0, files = {"src/test/java/org/example/moneymachine/repository/csv/validmockbankcardnumbers.csv"})
        void cardNumberFollowsFormat(String userId) {


            boolean followsFormat = mockOfMockBank.cardNumberFollowsFormat(userId);

            assertTrue(followsFormat);

        }
        @Order(8)
        @DisplayName("Tests with card numbers not following the schema")
        @ParameterizedTest
        @CsvFileSource(numLinesToSkip = 0, files = {"src/test/java/org/example/moneymachine/repository/csv/invalidMockBankCrdNmbrs.csv"})
        void cardNumberFollowsFormat_WithInvalidCardNumbers(String userId) {


            boolean followsFormat = mockOfMockBank.cardNumberFollowsFormat(userId);
            assertFalse(followsFormat);
        }
        @Order(9)
        @Test
        void getBankNameAsStaticMethod() {


            String name = mockOfMockBank.getBankNameAsStaticMethod();

            assertEquals(MockBank.getBankName(), name);


        }
        @Order(10)
        @DisplayName("Authenticate user login with valid user")
        @Test
        void authenticateUserLogin() {
            UserEntity randomUser1 = getRandomUser();

            UserEntity validEntity = new UserEntity(randomUser1.getBalance(), 1,randomUser1.getId(), false, randomUser1.getPin());





            //Mock valid/Invalid input on userExistsCheck
            when(userServiceMock.isExistingUser(validEntity.getId())).thenReturn(true);
            when(userServiceMock.getUserById(validEntity.getId())).thenReturn(Optional.of(validEntity));

            //Mock credentialsMatch-call
            when(userServiceMock.credentialsMatch(validEntity.getId(), validEntity.getPin()))
                    .thenReturn(true);

            //Mock resetFailedAttempts call on completed login

            doAnswer(invocation -> {
                resetRandomUser1Attempts(validEntity);

                return null;
            }).when(userServiceMock).resetFailedAttempts(validEntity.getId());


            //Call authenticate method
            mockOfMockBank.authenticateUserLogin(validEntity.getId(), validEntity.getPin());



            //Should reset number of failed attempts on success

            assertFalse(validEntity.isLocked());
            assertEquals(0, validEntity.getFailedAttempts());

        }

        @Order(11)
        @DisplayName("Authenticate user login with invalid id")
        @Test
        void authenticateUserLogin_InvalidId() {


            String invalidIdAndPin = "132444455";







            //Call authenticate method
            assertThrows(InvalidInputException.class, ()->mockOfMockBank.authenticateUserLogin(invalidIdAndPin,invalidIdAndPin));





        }

        @Order(12)
        @DisplayName("Authenticate user login with locked account")
        @Test
        void authenticateUserLogin_LockedAccount() {


            UserEntity randomUser2 = getRandomUser();
            //Set isLocked on user2 to try throw-case
            randomUser2.setFailedAttempts(3);
            randomUser2.setLocked(true);


            //Mock valid/Invalid input on userExistsCheck
            when(userServiceMock.isExistingUser(randomUser2.getId())).thenReturn(true);
            when(userServiceMock.getUserById(randomUser2.getId())).thenReturn(Optional.of(randomUser2));







            //Should throw LockedAccountException on lockedAccount
            assertThrows(LockedAccountException.class, () -> mockOfMockBank.authenticateUserLogin(randomUser2.getId(), randomUser2.getPin())) ;



        }


    }








    private UserEntity getRandomUser() {
        int randomUserIndex = getRandomIndex(users.size());

        UserEntity aUserEntity = users.get(randomUserIndex);
        return aUserEntity;
    }
    /**
     * Gets a random index in list
     * @param listLength - The highest unoccupied element index
     * @return A random index in the list
     */
    public int getRandomIndex(int listLength){

        return  (int) Math.floor(Math.random() * (listLength));
    }

}