package com.payme.backend.service;

import com.payme.backend.domain.*; // for static metamodels
import com.payme.backend.domain.Refund;
import com.payme.backend.repository.RefundRepository;
import com.payme.backend.repository.search.RefundSearchRepository;
import com.payme.backend.service.criteria.RefundCriteria;
import com.payme.backend.service.dto.RefundDTO;
import com.payme.backend.service.mapper.RefundMapper;
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
 * Service for executing complex queries for {@link Refund} entities in the database.
 * The main input is a {@link RefundCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RefundDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RefundQueryService extends QueryService<Refund> {

    private static final Logger log = LoggerFactory.getLogger(RefundQueryService.class);

    private final RefundRepository refundRepository;

    private final RefundMapper refundMapper;

    private final RefundSearchRepository refundSearchRepository;

    public RefundQueryService(RefundRepository refundRepository, RefundMapper refundMapper, RefundSearchRepository refundSearchRepository) {
        this.refundRepository = refundRepository;
        this.refundMapper = refundMapper;
        this.refundSearchRepository = refundSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link RefundDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RefundDTO> findByCriteria(RefundCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Refund> specification = createSpecification(criteria);
        return refundRepository.findAll(specification, page).map(refundMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RefundCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Refund> specification = createSpecification(criteria);
        return refundRepository.count(specification);
    }

    /**
     * Function to convert {@link RefundCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Refund> createSpecification(RefundCriteria criteria) {
        Specification<Refund> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Refund_.id));
            }
            if (criteria.getReference() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReference(), Refund_.reference));
            }
            if (criteria.getTransactionRef() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTransactionRef(), Refund_.transactionRef));
            }
            if (criteria.getRefundDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRefundDate(), Refund_.refundDate));
            }
            if (criteria.getRefundStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getRefundStatus(), Refund_.refundStatus));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Refund_.amount));
            }
            if (criteria.getTransactionId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getTransactionId(),
                        root -> root.join(Refund_.transaction, JoinType.LEFT).get(Transaction_.id)
                    )
                );
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCustomerId(), root -> root.join(Refund_.customer, JoinType.LEFT).get(Customer_.id))
                );
            }
        }
        return specification;
    }
}
