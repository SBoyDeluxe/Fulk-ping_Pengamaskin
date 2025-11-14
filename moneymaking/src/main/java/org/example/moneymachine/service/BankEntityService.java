package org.example.moneymachine.service;

import org.example.moneymachine.model.entity.*;
import org.example.moneymachine.repository.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

/**
 * An interface specifying the implicit contract of a BankEntityService, that is, that it contains some form of repository
 * @param <E> - The {@link BankApiEntity<id> BankApiEntity}
 * @param <id> - The primary key class
 * @see:| {@link JpaRepository}  | {@link BankEntityRepository}
 */
@Component
public interface BankEntityService<E extends BankApiEntity<id>, id> {

         BankEntityRepository<E,id> getBankEntityRepository();

}
