package com.payme.backend.service;

import com.payme.backend.service.dto.RefundDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.payme.backend.domain.Refund}.
 */
public interface RefundService {
    /**
     * Save a refund.
     *
     * @param refundDTO the entity to save.
     * @return the persisted entity.
     */
    RefundDTO save(RefundDTO refundDTO);

    /**
     * Updates a refund.
     *
     * @param refundDTO the entity to update.
     * @return the persisted entity.
     */
    RefundDTO update(RefundDTO refundDTO);

    /**
     * Partially updates a refund.
     *
     * @param refundDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RefundDTO> partialUpdate(RefundDTO refundDTO);

    /**
     * Get the "id" refund.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RefundDTO> findOne(Long id);

    /**
     * Delete the "id" refund.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the refund corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RefundDTO> search(String query, Pageable pageable);
}
