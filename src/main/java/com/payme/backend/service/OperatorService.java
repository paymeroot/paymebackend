package com.payme.backend.service;

import com.payme.backend.service.dto.OperatorDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.payme.backend.domain.Operator}.
 */
public interface OperatorService {
    /**
     * Save a operator.
     *
     * @param operatorDTO the entity to save.
     * @return the persisted entity.
     */
    OperatorDTO save(OperatorDTO operatorDTO);

    /**
     * Updates a operator.
     *
     * @param operatorDTO the entity to update.
     * @return the persisted entity.
     */
    OperatorDTO update(OperatorDTO operatorDTO);

    /**
     * Partially updates a operator.
     *
     * @param operatorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OperatorDTO> partialUpdate(OperatorDTO operatorDTO);

    /**
     * Get all the operators.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OperatorDTO> findAll(Pageable pageable);

    /**
     * Get the "id" operator.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OperatorDTO> findOne(Long id);

    /**
     * Delete the "id" operator.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the operator corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OperatorDTO> search(String query, Pageable pageable);
}
