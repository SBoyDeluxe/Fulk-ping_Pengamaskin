package org.example.moneymachine.repository;

import org.example.moneymachine.model.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.stereotype.*;

/**
 * Specifies a {@linkplain JpaRepository JPA-repository} for a {@linkplain BankApiEntity Bank-API-Entity}
 */
@NoRepositoryBean
@Component
public interface BankEntityRepository<E extends BankApiEntity<id>, id> extends JpaRepository<E, id> {
}
