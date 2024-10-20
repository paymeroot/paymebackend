package com.payme.backend.service;

import com.payme.backend.domain.*; // for static metamodels
import com.payme.backend.domain.Kyc;
import com.payme.backend.repository.KycRepository;
import com.payme.backend.repository.search.KycSearchRepository;
import com.payme.backend.service.criteria.KycCriteria;
import com.payme.backend.service.dto.KycDTO;
import com.payme.backend.service.mapper.KycMapper;
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
 * Service for executing complex queries for {@link Kyc} entities in the database.
 * The main input is a {@link KycCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link KycDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class KycQueryService extends QueryService<Kyc> {

    private static final Logger log = LoggerFactory.getLogger(KycQueryService.class);

    private final KycRepository kycRepository;

    private final KycMapper kycMapper;

    private final KycSearchRepository kycSearchRepository;

    public KycQueryService(KycRepository kycRepository, KycMapper kycMapper, KycSearchRepository kycSearchRepository) {
        this.kycRepository = kycRepository;
        this.kycMapper = kycMapper;
        this.kycSearchRepository = kycSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link KycDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<KycDTO> findByCriteria(KycCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Kyc> specification = createSpecification(criteria);
        return kycRepository.findAll(specification, page).map(kycMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(KycCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Kyc> specification = createSpecification(criteria);
        return kycRepository.count(specification);
    }

    /**
     * Function to convert {@link KycCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Kyc> createSpecification(KycCriteria criteria) {
        Specification<Kyc> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Kyc_.id));
            }
            if (criteria.getReference() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReference(), Kyc_.reference));
            }
            if (criteria.getTypePiece() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTypePiece(), Kyc_.typePiece));
            }
            if (criteria.getNumberPiece() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNumberPiece(), Kyc_.numberPiece));
            }
            if (criteria.getPhotoPieceUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhotoPieceUrl(), Kyc_.photoPieceUrl));
            }
            if (criteria.getPhotoSelfieUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhotoSelfieUrl(), Kyc_.photoSelfieUrl));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCustomerId(), root -> root.join(Kyc_.customer, JoinType.LEFT).get(Customer_.id))
                );
            }
        }
        return specification;
    }
}
