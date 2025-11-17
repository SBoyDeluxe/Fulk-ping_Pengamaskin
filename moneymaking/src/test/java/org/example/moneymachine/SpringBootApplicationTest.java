package org.example.moneymachine;

import org.example.moneymachine.controller.UI.*;
import org.example.moneymachine.model.entity.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.boot.test.context.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpringBootApplicationTest {
    SpringBootApplication springBootApplication = new SpringBootApplication();
    ATMTestConfig atmTestConfig;
    ATMService mockATMService;
    /**
     * Persists the user-entities between methods
     */
    private List<UserEntity> userEntities;
    private  UserInterface userInterface;


    @Test
    @BeforeEach
    void setUp() {
        //Set up mocked dependencies
        atmTestConfig = new ATMTestConfig();
        mockATMService = atmTestConfig.ATM();
        userEntities = new ArrayList<>();
        userInterface = new UserInterface();

    }
    @Nested
    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @DisplayName("Set up mock user base")
    class SetUp{
        @Order(1)
        @ParameterizedTest
        @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/users.csv")
        void addMockBankUsers(String id, String pin, double balance, int failedAttempts, boolean isLocked){

            int size = userEntities.size();
            UserEntity entity = new UserEntity(balance, failedAttempts, id, isLocked, pin);

            userEntities.add(entity);
            assertEquals(userEntities.size(), size+1);
            assertFalse(userEntities.isEmpty());

        }
        @Order(2)
        @ParameterizedTest
        @CsvFileSource(numLinesToSkip = 0, files = "src/main/resources/csv/mastercardusers.csv")
        void addMastercardBankUsers(String id,int failedAttempts , double balance, String pin, boolean isLocked){

            int size = userEntities.size();
            UserEntity entity = new UserEntity(balance, failedAttempts, id, isLocked, pin);

            userEntities.add(entity);
            assertEquals(userEntities.size(), size+1);
            assertFalse(userEntities.isEmpty());

        }
    }



    @Order(2)
    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class Authentication{

        @Order(1)
        @Test
        @DisplayName("Pin verification screen on valid user")
        void pinInputOnValidUser(){



        }

    }
//
//    @DisplayName("Logged in screen")
//    @Test
//    void loggedin() {
//        MockBank mockBank = Mockito.mock( MockBank.class)
//        ATMConfig atmConfig = new ATMConfig(mockBank, applicationContext.getBean(MasterCardBank.class));
//        ATM atm = atmConfig.ATM();
//        UserInterface userInterface = new UserInterface();
//        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
//    }
//    @DisplayName("auth screen")
//    @Test
//    void auth() {
//        ATMConfig atmConfig = new ATMConfig(mockBank, applicationContext.getBean(MasterCardBank.class));
//        ATM atm = atmConfig.ATM();
//        UserInterface userInterface = new UserInterface();
//        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
//    }
//    @DisplayName("Test application")
//    @Test
//    void main() {
//        ATMConfig atmConfig = new ATMConfig(mockBank, applicationContext.getBean(MasterCardBank.class));
//        ATM atm = atmConfig.ATM();
//        UserInterface userInterface = new UserInterface();
//        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
//    }
}