package com.payme.backend.service;

import com.payme.backend.service.dto.KycDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.payme.backend.domain.Kyc}.
 */
public interface KycService {
    /**
     * Save a kyc.
     *
     * @param kycDTO the entity to save.
     * @return the persisted entity.
     */
    KycDTO save(KycDTO kycDTO);

    /**
     * Updates a kyc.
     *
     * @param kycDTO the entity to update.
     * @return the persisted entity.
     */
    KycDTO update(KycDTO kycDTO);

    /**
     * Partially updates a kyc.
     *
     * @param kycDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<KycDTO> partialUpdate(KycDTO kycDTO);

    /**
     * Get all the KycDTO where Customer is {@code null}.
     *
     * @return the {@link List} of entities.
     */
    List<KycDTO> findAllWhereCustomerIsNull();

    /**
     * Get the "id" kyc.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<KycDTO> findOne(Long id);

    /**
     * Delete the "id" kyc.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the kyc corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<KycDTO> search(String query, Pageable pageable);
}
