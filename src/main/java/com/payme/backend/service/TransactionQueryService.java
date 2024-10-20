package com.payme.backend.service;

import com.payme.backend.domain.*; // for static metamodels
import com.payme.backend.domain.Transaction;
import com.payme.backend.repository.TransactionRepository;
import com.payme.backend.repository.search.TransactionSearchRepository;
import com.payme.backend.service.criteria.TransactionCriteria;
import com.payme.backend.service.dto.TransactionDTO;
import com.payme.backend.service.mapper.TransactionMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Transaction} entities in the database.
 * The main input is a {@link TransactionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TransactionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransactionQueryService extends QueryService<Transaction> {

    private static final Logger log = LoggerFactory.getLogger(TransactionQueryService.class);

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionSearchRepository transactionSearchRepository;

    public TransactionQueryService(
        TransactionRepository transactionRepository,
        TransactionMapper transactionMapper,
        TransactionSearchRepository transactionSearchRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionSearchRepository = transactionSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link TransactionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findByCriteria(TransactionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Transaction> specification = createSpecification(criteria);
        return transactionRepository.findAll(specification, page).map(transactionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransactionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Transaction> specification = createSpecification(criteria);
        return transactionRepository.count(specification);
    }

    /**
     * Function to convert {@link TransactionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Transaction> createSpecification(TransactionCriteria criteria) {
        Specification<Transaction> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Transaction_.id));
            }
            if (criteria.getReference() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReference(), Transaction_.reference));
            }
            if (criteria.getTransactionDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTransactionDate(), Transaction_.transactionDate));
            }
            if (criteria.getSenderNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSenderNumber(), Transaction_.senderNumber));
            }
            if (criteria.getSenderWallet() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSenderWallet(), Transaction_.senderWallet));
            }
            if (criteria.getReceiverNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReceiverNumber(), Transaction_.receiverNumber));
            }
            if (criteria.getReceiverWallet() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReceiverWallet(), Transaction_.receiverWallet));
            }
            if (criteria.getTransactionStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getTransactionStatus(), Transaction_.transactionStatus));
            }
            if (criteria.getPayInStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getPayInStatus(), Transaction_.payInStatus));
            }
            if (criteria.getPayOutStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getPayOutStatus(), Transaction_.payOutStatus));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Transaction_.amount));
            }
            if (criteria.getObject() != null) {
                specification = specification.and(buildStringSpecification(criteria.getObject(), Transaction_.object));
            }
            if (criteria.getPayInFailureReason() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getPayInFailureReason(), Transaction_.payInFailureReason)
                );
            }
            if (criteria.getPayOutFailureReason() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getPayOutFailureReason(), Transaction_.payOutFailureReason)
                );
            }
            if (criteria.getSenderCountryName() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getSenderCountryName(), Transaction_.senderCountryName)
                );
            }
            if (criteria.getReceiverCountryName() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getReceiverCountryName(), Transaction_.receiverCountryName)
                );
            }
            if (criteria.getRefundId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRefundId(), root -> root.join(Transaction_.refund, JoinType.LEFT).get(Refund_.id))
                );
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCustomerId(), root -> root.join(Transaction_.customer, JoinType.LEFT).get(Customer_.id))
                );
            }
        }
        return specification;
    }
}
