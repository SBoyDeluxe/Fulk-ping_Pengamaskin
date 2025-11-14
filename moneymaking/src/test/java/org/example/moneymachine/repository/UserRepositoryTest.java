package org.example.moneymachine.repository;

import org.example.moneymachine.*;
import org.example.moneymachine.model.entity.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.jdbc.*;
import org.springframework.boot.test.autoconfigure.jdbc.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.repository.config.*;
import org.springframework.test.context.junit.jupiter.*;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
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

    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    class CRUDFunctionalityTest {

        private int deleteCounter = 1;
        private int updateCounter = 1;
        @Test
        @DisplayName("Initialize user repository")
        @Order(1)
        void initialize_userRepository() {
            Assumptions.assumeFalse(userRepository == null);
            System.out.println(userRepository);
        }

        @Order(2)
        @DisplayName("Save user with repository")
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
                System.out.println(userRepository.findAll().stream().map((UserRepositoryTest::getStringPresentationOfUserEntity)).toList());
                userEntities.add(new UserEntity(balance, failedAttempts, id, isLocked, pin));
                System.out.println(userEntities.stream().map(UserRepositoryTest::getStringPresentationOfUserEntity).toList());
                //Checks so that a user has been added to the list
                Assumptions.assumeTrue(userEntities.size() > sizeOfUserEntities);
            }
        }

        @Order(3)
        @DisplayName("Delete UserEntity")
        @ParameterizedTest
        @CsvFileSource(numLinesToSkip = 0, files = "src/test/java/org/example/moneymachine/repository/csv/users.csv")
        void UserRepository_DeleteUser(String id, String pin, Double balance, Integer failedAttempts, Boolean isLocked) {
            int userCount = userEntities.size();
            int userEntitiesLength = -1;
            //Add all entities to user-repository
            userRepository.saveAll(userEntities);
            //Delete the specific user
            UserEntity entityToDelete = new UserEntity(balance, failedAttempts, id, isLocked, pin);

            //delete
            switch (deleteCounter % 3) {
                case 0 -> {
                    userRepository.deleteById(entityToDelete.getId());
                    deleteCounter++;
                    userEntitiesLength = userRepository.findAll().size();
                }
                case 1 -> {
                    deleteCounter++;
                    userRepository.delete(entityToDelete);
                    userEntitiesLength = userRepository.findAll().size();

                }
                case 2 -> {
                    deleteCounter++;
                    userRepository.deleteAllById(List.of(entityToDelete.getId()));
                    userEntitiesLength = userRepository.findAll().size();

                }
            }
            Assumptions.assumeFalse(userRepository.findAll().contains(entityToDelete));
            assertEquals(userCount, userEntitiesLength + 1);
        }

        @Order(4)
        @DisplayName("Update UserEntity")
        @ParameterizedTest
        @CsvFileSource(numLinesToSkip = 0, files = "src/test/java/org/example/moneymachine/repository/csv/users.csv")
        void UserRepository_UpdateUserEntity(String id, String pin, Double balance, Integer failedAttempts, Boolean isLocked){
            int userCount = userEntities.size();
            int userEntitiesLength = -1;
            //Add all entities to user-repository
            userRepository.saveAll(userEntities);
            //Delete the specific user
            UserEntity entityToUpdate = new UserEntity(balance, failedAttempts, id, isLocked, pin);

            //delete
            switch (updateCounter % 3) {
                case 0 -> {
                    //Get the user-entity instance
                    Optional<UserEntity> entityFromDbToUpdate = userRepository.findById(entityToUpdate.getId());
                    //update the instance
                    updateCounter++;
                   Assumptions.assumingThat(entityFromDbToUpdate.isPresent(), () -> {
                       
                       //Get base version of db
                       List<UserEntity> all = userRepository.findAll();



                       entityFromDbToUpdate.get().setFailedAttempts(updateCounter%4);
                       assertFalse(all.contains(entityFromDbToUpdate.get()));

                       userRepository.save(entityFromDbToUpdate.get());
                       all = userRepository.findAll();

                       assertTrue(all.contains(entityFromDbToUpdate.get()));
                       
                       
                   }); ;
                  
                    userEntitiesLength = userRepository.findAll().size();

                    assertEquals(userEntities.size(), userRepository.count());
                }
                case 1 -> {
                    updateCounter++;


                    double newBalance = Math.random() * balance;
                    entityToUpdate.setBalance(newBalance);
                    userRepository.save(entityToUpdate);

                    Optional<UserEntity> userInDb = userRepository.findById(id);
                    Assumptions.assumingThat(userInDb.isPresent(),() -> {
                        assertTrue(userInDb.get().getBalance() == newBalance);
                        System.out.println(getStringPresentationOfUserEntity(userInDb.get()));
                    });
                    assertEquals(userEntities.size(), userRepository.count());

                }
                case 2 -> {
                    updateCounter++;
                    double randomNumber = Math.random() * balance;
                    double newPin = (int) Math.floor(randomNumber);

                    entityToUpdate.setPin(String.valueOf(newPin));

                    System.out.println("Before change : "+getStringPresentationOfUserEntity(userRepository.findById(id).get()));

                    userRepository.save(entityToUpdate);

                    String pinInDb = userRepository.findById(id).get().getPin();

                    assertEquals(pinInDb, String.valueOf(newPin));

                    assertEquals(userEntities.size(), userRepository.count());



                }
            }
            Assumptions.assumeTrue(userRepository.findAll().contains(entityToUpdate));
            assertEquals(userEntities.size(), userRepository.count() );
        }



    }

    public static String getStringPresentationOfUserEntity(UserEntity userEntity) {
        return new String("\n\t Id : " + userEntity.getId() + "\n\t Balance :" + userEntity.getBalance() + "\n\t isLocked :" + userEntity.getIsLocked() + "\n\t Pin: " + userEntity.getPin() + "\n\t Failed attempts : " + userEntity.getFailedAttempts() + "\n\n");
    }

}