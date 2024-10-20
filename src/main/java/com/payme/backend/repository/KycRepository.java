package com.payme.backend.repository;

import com.payme.backend.domain.Kyc;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Kyc entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KycRepository extends JpaRepository<Kyc, Long>, JpaSpecificationExecutor<Kyc> {}
