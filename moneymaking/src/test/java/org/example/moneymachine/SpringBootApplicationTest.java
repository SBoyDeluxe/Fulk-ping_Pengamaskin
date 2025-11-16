package org.example.moneymachine;

import org.example.moneymachine.banks.implementations.*;
import org.example.moneymachine.controller.UI.*;
import org.example.moneymachine.repository.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.processing.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.jdbc.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpringBootApplicationTest {
    @Test
    @BeforeEach
    void setUp() {

    }

    @DisplayName("Logged in screen")
    @Test
    void loggedin() {
        MockBank mockBank = Mockito.mock( MockBank.class)
        ATMConfig atmConfig = new ATMConfig(mockBank, applicationContext.getBean(MasterCardBank.class));
        ATM atm = atmConfig.ATM();
        UserInterface userInterface = new UserInterface();
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
    }
    @DisplayName("auth screen")
    @Test
    void auth() {
        ATMConfig atmConfig = new ATMConfig(mockBank, applicationContext.getBean(MasterCardBank.class));
        ATM atm = atmConfig.ATM();
        UserInterface userInterface = new UserInterface();
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
    }
    @DisplayName("Test application")
    @Test
    void main() {
        ATMConfig atmConfig = new ATMConfig(mockBank, applicationContext.getBean(MasterCardBank.class));
        ATM atm = atmConfig.ATM();
        UserInterface userInterface = new UserInterface();
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
    }
}