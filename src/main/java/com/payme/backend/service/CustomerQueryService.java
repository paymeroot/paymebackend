package com.payme.backend.service;

import com.payme.backend.domain.*; // for static metamodels
import com.payme.backend.domain.Customer;
import com.payme.backend.repository.CustomerRepository;
import com.payme.backend.repository.search.CustomerSearchRepository;
import com.payme.backend.service.criteria.CustomerCriteria;
import com.payme.backend.service.dto.CustomerDTO;
import com.payme.backend.service.mapper.CustomerMapper;
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
 * Service for executing complex queries for {@link Customer} entities in the database.
 * The main input is a {@link CustomerCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CustomerDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomerQueryService extends QueryService<Customer> {

    private static final Logger log = LoggerFactory.getLogger(CustomerQueryService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerQueryService(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CustomerSearchRepository customerSearchRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchRepository = customerSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link CustomerDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findByCriteria(CustomerCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.findAll(specification, page).map(customerMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomerCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Customer> specification = createSpecification(criteria);
        return customerRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomerCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Customer> createSpecification(CustomerCriteria criteria) {
        Specification<Customer> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Customer_.id));
            }
            if (criteria.getLastname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastname(), Customer_.lastname));
            }
            if (criteria.getFirstname() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFirstname(), Customer_.firstname));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Customer_.phone));
            }
            if (criteria.getCountryCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCountryCode(), Customer_.countryCode));
            }
            if (criteria.getStatusKyc() != null) {
                specification = specification.and(buildSpecification(criteria.getStatusKyc(), Customer_.statusKyc));
            }
            if (criteria.getProfileId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProfileId(), root -> root.join(Customer_.profile, JoinType.LEFT).get(Profile_.id))
                );
            }
            if (criteria.getKycId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getKycId(), root -> root.join(Customer_.kyc, JoinType.LEFT).get(Kyc_.id))
                );
            }
            if (criteria.getCountryId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCountryId(), root -> root.join(Customer_.country, JoinType.LEFT).get(Country_.id))
                );
            }
            if (criteria.getTransactionId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getTransactionId(),
                        root -> root.join(Customer_.transactions, JoinType.LEFT).get(Transaction_.id)
                    )
                );
            }
            if (criteria.getRefundId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRefundId(), root -> root.join(Customer_.refunds, JoinType.LEFT).get(Refund_.id))
                );
            }
        }
        return specification;
    }
}
