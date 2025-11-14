package org.example.moneymachine.service;

import org.example.moneymachine.model.entity.*;
import org.example.moneymachine.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

/**
 * Represents the implementation of a User service, wrapping the interactions
 * with the persistence layer and implementing the API-specific functions
 */
@Service
public class UserService implements BankEntityService<UserEntity, String> {

    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    /**
     * Authenticates a user and returns the result of the authentication as a boolean
     * @param userId - The id of the user to authenticate
     * @param pinCode - The pin code to authenticate
     * @return true on authentication, false otherwise
     */
    public boolean credentialsMatch(String userId, String pinCode) {

    return userRepository.existsByIdAndPin(userId, pinCode);


    }

    /**
     * Increments the number of failed attempts for user.
     * Sets isLocked to true when failedAttempts = true
     * @param id - Id of user
     * @return -1 if user does not exist, else the updated number of failed attempts
     */
    public int incrementFailedAttempts(String id) {
        boolean userExists = userRepository.existsById(id);
        if(userExists){
            UserEntity userEntityToIncrementAttmptsFor = userRepository.getReferenceById(id);
            int prevFailedAttempts = userEntityToIncrementAttmptsFor.getFailedAttempts();
            userEntityToIncrementAttmptsFor.setFailedAttempts(prevFailedAttempts+1);
            boolean isLocked = (prevFailedAttempts+1 >=3) ? true : false;
            userEntityToIncrementAttmptsFor.setIsLocked(isLocked);
            UserEntity saved = userRepository.save(userEntityToIncrementAttmptsFor);
            return saved.getFailedAttempts();
        }
        else{
            return -1;
        }
    }

    /**
     * Deposits the specified amount into the user´s account and returns the new balance
     * @param userId - Id of user
     * @param amountToDeposit - Amount to deposit
     * @return The new account balance or -1 on user not found
     */
    public double deposit(String userId, double amountToDeposit) {

        Optional<UserEntity> userEntityById = userRepository.findById(userId);
        if(userEntityById.isPresent()){
            UserEntity userEntityMakingDeposit = userEntityById.get();
            double oldBalance = userEntityMakingDeposit.getBalance();
            double newBalance = oldBalance + amountToDeposit;
            userEntityMakingDeposit.setBalance(newBalance);
           return userRepository.save(userEntityMakingDeposit).getBalance();
        }
        else{
            return -1;
        }

    }

    public Optional<UserEntity> getUserById(String userId){
       return userRepository.findById(userId);
    }

    /**
     * Withdraws the amount from the user´s account and returns the new account balance
     * @param userId - Id of user that requests the withdrawal
     * @param withdrawalAmount - Amount to be withdrawn
     * @return The new account balance on successful withdrawal, -1 on user not found
     */
    public double withdraw(String userId, double withdrawalAmount) {
        Optional<UserEntity> userEntityById = userRepository.findById(userId);
        if(userEntityById.isPresent()){
            UserEntity userEntityMakingDeposit = userEntityById.get();
            double oldBalance = userEntityMakingDeposit.getBalance();
            double newBalance = oldBalance - withdrawalAmount;
            userEntityMakingDeposit.setBalance(newBalance);
            return userRepository.save(userEntityMakingDeposit).getBalance();
        }
        else{
            return -1;
        }
    }

    /**
     * Gets whether a user exists in the banks api system or not
     * @param userId - Userid to check
     * @return true on existing, false otherwise
     */
    public boolean isExistingUser(String userId) {
       return userRepository.existsById(userId);
    }

    /**
     * Resets the number of failed attempts for a user in the bank api-system
     * @param userId - UserId of an authenticated user
     * @apiNote - Only called on successful authentication of user login
     */
    public void resetFailedAttempts(String userId) {
        Optional<UserEntity> authenticatedUser = userRepository.findById(userId);
        if(authenticatedUser.isPresent()){
            UserEntity authenticatedUserEntity = authenticatedUser.get();
            authenticatedUserEntity.setFailedAttempts(0);
            userRepository.save(authenticatedUserEntity);
        }
    }

    @Override
    public BankEntityRepository<UserEntity, String> getBankEntityRepository() {
       return this.userRepository;
    }
}
