package org.example.moneymachine.banks.implementations;


import org.example.moneymachine.exceptions.*;
import org.example.moneymachine.model.DTO.*;
import org.example.moneymachine.model.entity.*;
import org.example.moneymachine.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
 public class   MasterCardBankTest {

        //private UserRepository userRepositoryMock;

//    @Autowired
//    private UserRepository userRepository;
//    private UserService userService;
//    private MockBank mockBank;

        private List<UserEntity> users;

        @BeforeAll
        void setUp() {
            users = new ArrayList<>();
//        userService = new UserService(userRepository);
//        mockBank = new MockBank(userService);
//        mockOfMockBank = new MockBank(userServiceMock);
//        userServiceMock = Mockito.mock(UserService.class);


        }




    @Order(1)
        @Nested
        class setUpUserEntitesList{


            @Order(1)
            @ParameterizedTest
            @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/mastercardusers.csv")
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

            private MasterCardBank mockOfMockBank;

            @BeforeEach
            void setUp() {
                userServiceMock = mock(UserService.class);
                mockOfMockBank = new MasterCardBank(userServiceMock);

            }
//        private void populateRepository() {
//            userRepository.saveAll(users);
//        }



            @Order(1)
            @DisplayName("Check if user exists in bank persistence layer")
            @Test
            void isExistingUser() {
                //Valid input
                String validId = getRandomUser().getId();
                String invalidId = "1984841165";

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
//        @Order(3)
//        @DisplayName("Authenticate user login")
//        @Test
//        void authenticateUserLogin() {
//            UserEntity randomUser1 = getRandomUser();
//            randomUser1.setFailedAttempts(1);
//            randomUser1.setLocked(false);
//            UserEntity randomUser2 = getRandomUser();
//            //Set isLocked on user2 to try throw-case
//            randomUser2.setFailedAttempts(3);
//            randomUser2.setLocked(true);
//
//            String invalidIdAndPin = "asfasf56456456esdfafd";
//
//            //Mock valid/Invalid input on userExistsCheck
//            Mockito.when(userServiceMock.isExistingUser(randomUser1.getId())).thenReturn(true);
//            Mockito.when(userServiceMock.isExistingUser(randomUser2.getId())).thenReturn(true);
//            Mockito.when(userServiceMock.isExistingUser(invalidIdAndPin)).thenReturn(false);
//            Mockito.when(userServiceMock.getUserById(randomUser1.getId())).thenReturn(Optional.of(randomUser1));
//            Mockito.when(userServiceMock.getUserById(randomUser2.getId())).thenReturn(Optional.of(randomUser2));
//
//            //Mock credentialsMatch-call
//            Mockito.when(userServiceMock.credentialsMatch(randomUser1.getId(), randomUser1.getPin()))
//                    .thenReturn(true);
//
//            //Mock resetFailedAttempts call on completed login
//
//             Mockito.doAnswer(invocation -> {
//                 resetRandomUser1Attempts(randomUser1);
//
//                 return null;
//             }).when(userServiceMock).resetFailedAttempts(randomUser1.getId());
//
//
//            //Call authenticate method
//            mockOfMockBank.authenticateUserLogin(randomUser1.getId(), randomUser1.getPin());
//            assertThrows(InvalidInputException.class, ()->mockOfMockBank.authenticateUserLogin(invalidIdAndPin,invalidIdAndPin));
//
//
//            //Should throw LockedAccountException on lockedAccount
//            assertThrows(LockedAccountException.class, () -> mockOfMockBank.authenticateUserLogin(randomUser2.getId(), randomUser2.getPin())) ;
//
//            //Should reset number of failed attempts on success
//
//            assertEquals(0,randomUser1.getFailedAttempts() );
//
//        }

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

                assertEquals("Mastercard", MasterCardBank.getBankName());
            }

            @Order(7)
            @ParameterizedTest
            @DisplayName("Tests with card numbers following schema")
            @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/mastercardusers.csv")
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

                assertEquals(MasterCardBank.getBankName(), name);


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

            return  (int) Math.floor(Math.random() * (listLength-1));
        }

    }
