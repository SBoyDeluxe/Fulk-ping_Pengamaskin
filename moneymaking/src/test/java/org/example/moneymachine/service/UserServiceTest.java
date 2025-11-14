package org.example.moneymachine.service;

import org.example.moneymachine.model.entity.*;
import org.example.moneymachine.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.jdbc.*;
import org.springframework.boot.test.autoconfigure.jdbc.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.stereotype.*;
import org.springframework.test.context.junit.jupiter.*;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Spy
    private UserRepository userRepositoryMock;
    @Autowired
    private UserService userService;
    @InjectMocks
    @Spy
    private UserService userServiceMock;
    /**
     * Persists the user-entities between methods
     */
    private List<UserEntity> userEntities;

    @BeforeAll
    void setUp() {

        userEntities = new ArrayList<>();
    }

    @AfterAll
    void tearDown() {
        userEntities = null;
    }




    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Setup Test")
    @Nested
    class SetupTest{
        @Test
        @Order(1)
        @DisplayName("Spies and auto-wired instances are created")
        void getUserService(){
            System.out.println(userService);
            Assumptions.assumeFalse(userService == null);
            Assumptions.assumeFalse(userRepository == null);
            Assumptions.assumeFalse(userRepositoryMock == null);
            Assumptions.assumeFalse(userServiceMock == null);

        }

        @Order(2)
        @DisplayName("Save user with repository and populate userEntities-list")
        @ParameterizedTest
        @CsvFileSource(numLinesToSkip = 0, files = "src/test/java/org/example/moneymachine/repository/csv/users.csv")
        void UserRepository_SaveUser(String id, String pin, double balance, int failedAttempts, boolean isLocked) {

            int sizeOfUserEntities = userEntities.size();


            if (userEntities.size() == 20) {
                List<UserEntity> userEntityList = userRepository.saveAll(userEntities);
                Stream<String> allUsersInRepository = userRepository.findAll().stream().map(userEntity -> getStringPresentationOfUserEntity(userEntity));
                System.out.println(allUsersInRepository);

                assertAll(
                        () -> Assertions.assertLinesMatch(userEntityList.stream().map(userEntity -> getStringPresentationOfUserEntity(userEntity)).sorted(), allUsersInRepository.sorted()),
                        () -> assertNotNull(userRepository),
                        () -> assertNotNull(allUsersInRepository),
                        () -> assertEquals(allUsersInRepository.toList().size(), sizeOfUserEntities)

                );

            } else {
                UserEntity entity = new UserEntity(balance, failedAttempts, id, isLocked, pin);
                userRepository.saveAndFlush(entity);


                Assumptions.assumeFalse(userRepository.findAll() == null);
                System.out.println(userRepository.findAll().stream().map((UserServiceTest::getStringPresentationOfUserEntity)).toList());
                userEntities.add(new UserEntity(balance, failedAttempts, id, isLocked, pin));
                System.out.println(userEntities.stream().map(UserServiceTest::getStringPresentationOfUserEntity).toList());
                //Checks so that a user has been added to the list
                Assumptions.assumeTrue(userEntities.size() > sizeOfUserEntities);
            }
        }

    }


    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    @DisplayName("Functionality test")
    class FunctionalityTest{

        @BeforeEach
        void setUp() {
            populateRepository();

        }

        @DisplayName("Handle auth-check")
        @Order(1)
        @Test
        void UserNotLoggedIn_InputIdAndPin(){

            UserEntity aUserEntity = getRandomUser();
            boolean isMatch =  userService.credentialsMatch(aUserEntity.getId(), aUserEntity.getPin());

            assertTrue(isMatch);

            //Returns false on invalid input
             isMatch =  userService.credentialsMatch(aUserEntity.getId(), "aaabbb");
            assertFalse(isMatch);
            isMatch =  userService.credentialsMatch("asfaser34534rafs", "aaabbb");
            assertFalse(isMatch);
            isMatch =  userService.credentialsMatch("asfaser34534rafs", aUserEntity.getPin());
            assertFalse(isMatch);





        }

        private void populateRepository() {
            userRepository.saveAll(userEntities);
        }

        @DisplayName("Handle failed attempts increment")
        @Order(2)
        @Test
        void UserNotLoggedIn_WrongInputPin_IncrementFailedAttemptsForUser(){

            UserEntity aUserEntity = getRandomUser();
            int prevFailedAttemptCount = aUserEntity.getFailedAttempts();
            int failedAttempts = userService.incrementFailedAttempts(aUserEntity.getId());

           assertEquals(prevFailedAttemptCount+1, failedAttempts);



        }
        @DisplayName("Handle failed attempts increment and update lock ")
        @Order(3)
        @Test
        void UserNotLoggedIn_WrongInputPin_UpdateLocked(){

            UserEntity aUserEntity = getRandomUser();
            int prevFailedAttemptCount = aUserEntity.getFailedAttempts();
            int failedAttempts = userService.incrementFailedAttempts(aUserEntity.getId());

            while(failedAttempts < 3){
               failedAttempts = userService.incrementFailedAttempts(aUserEntity.getId());
            }

           aUserEntity = userRepository.findById(aUserEntity.getId()).get();

           assertTrue(aUserEntity.isLocked());



        }
        @DisplayName("Handle balance exposure ")
        @Order(4)
        @Test
        void UserLoggedIn_getBalance_IsCorrect(){

            UserEntity aUserEntity = getRandomUser();
            Double balance = aUserEntity.getBalance();

            int userEntityIndex = userEntities.indexOf(aUserEntity);


            assertEquals(balance, userEntities.get(userEntityIndex).getBalance());



        }
        @DisplayName("Handle deposit ")
        @Order(5)
        @Test
        void UserLoggedIn_MakeDeposit(){

            UserEntity aUserEntity = getRandomUser();
            Double balance = aUserEntity.getBalance();

            int userEntityIndex = userEntities.indexOf(aUserEntity);


            assertEquals(balance, userEntities.get(userEntityIndex).getBalance());

            double newBalance = userService.deposit(aUserEntity.getId(), 100);

            Optional<UserEntity> updatedUser = userRepository.findById(aUserEntity.getId());

            assertEquals(balance + 100, newBalance);
            assertEquals(balance + 100, updatedUser.get().getBalance());


        }
        @DisplayName("Handle withdrawal ")
        @Order(6)
        @Test
        void UserLoggedIn_MakeWithdrawal(){
            UserEntity aUserEntity = getRandomUser();
            Double balance = aUserEntity.getBalance();

            int userEntityIndex = userEntities.indexOf(aUserEntity);


            assertEquals(balance, userEntities.get(userEntityIndex).getBalance());

            double newBalance = userService.withdraw(aUserEntity.getId(), 100);

            Optional<UserEntity> updatedUser = userRepository.findById(aUserEntity.getId());

            assertEquals(balance - 100, newBalance);
            assertEquals(balance - 100, updatedUser.get().getBalance());


        }
        @Order(7)
        @DisplayName("Check existing user")
        @Test
        void isExistingUser() {

            String existingId = getRandomUser().getId();
            String invalidId = "asfasfas";

            boolean shouldBeTrue = userService.isExistingUser(existingId);
            boolean shouldBeFalse = userService.isExistingUser(invalidId);

            assertAll(
                    ()->assertTrue(shouldBeTrue),
                    ()->assertFalse(shouldBeFalse)
            );

        }
        @Order(8)
        @Test
        @DisplayName("Reset failed attempts to 0")
        void resetFailedAttempts() {
            UserEntity randomUser = getRandomUser();

            userService.resetFailedAttempts(randomUser.getId());

            assertEquals( 0,userService.getUserById(randomUser.getId()).get().getFailedAttempts());
        }

    }

    private UserEntity getRandomUser() {
        int randomUserIndex = getRandomIndex(userEntities.size());

        UserEntity aUserEntity = userEntities.get(randomUserIndex);
        return aUserEntity;
    }

    public static String getStringPresentationOfUserEntity(UserEntity userEntity) {
        return new String("\n\t Id : " + userEntity.getId() + "\n\t Balance :" + userEntity.getBalance() + "\n\t isLocked :" + userEntity.isLocked() + "\n\t Pin: " + userEntity.getPin() + "\n\t Failed attempts : " + userEntity.getFailedAttempts() + "\n\n");
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