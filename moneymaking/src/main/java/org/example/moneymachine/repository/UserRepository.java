package org.example.moneymachine.repository;

import org.example.moneymachine.*;
import org.example.moneymachine.model.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

/**
 * A repository for User-data
 */
@Repository
public interface UserRepository extends BankEntityRepository<UserEntity, String>  {


        public boolean existsByIdAndPin(String userId, String pinCode);
}
