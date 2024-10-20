package com.payme.backend.service.impl;

import com.payme.backend.domain.Kyc;
import com.payme.backend.repository.KycRepository;
import com.payme.backend.repository.search.KycSearchRepository;
import com.payme.backend.service.KycService;
import com.payme.backend.service.dto.KycDTO;
import com.payme.backend.service.mapper.KycMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.payme.backend.domain.Kyc}.
 */
@Service
@Transactional
public class KycServiceImpl implements KycService {

    private static final Logger log = LoggerFactory.getLogger(KycServiceImpl.class);

    private final KycRepository kycRepository;

    private final KycMapper kycMapper;

    private final KycSearchRepository kycSearchRepository;

    public KycServiceImpl(KycRepository kycRepository, KycMapper kycMapper, KycSearchRepository kycSearchRepository) {
        this.kycRepository = kycRepository;
        this.kycMapper = kycMapper;
        this.kycSearchRepository = kycSearchRepository;
    }

    @Override
    public KycDTO save(KycDTO kycDTO) {
        log.debug("Request to save Kyc : {}", kycDTO);
        Kyc kyc = kycMapper.toEntity(kycDTO);
        kyc = kycRepository.save(kyc);
        kycSearchRepository.index(kyc);
        return kycMapper.toDto(kyc);
    }

    @Override
    public KycDTO update(KycDTO kycDTO) {
        log.debug("Request to update Kyc : {}", kycDTO);
        Kyc kyc = kycMapper.toEntity(kycDTO);
        kyc = kycRepository.save(kyc);
        kycSearchRepository.index(kyc);
        return kycMapper.toDto(kyc);
    }

    @Override
    public Optional<KycDTO> partialUpdate(KycDTO kycDTO) {
        log.debug("Request to partially update Kyc : {}", kycDTO);

        return kycRepository
            .findById(kycDTO.getId())
            .map(existingKyc -> {
                kycMapper.partialUpdate(existingKyc, kycDTO);

                return existingKyc;
            })
            .map(kycRepository::save)
            .map(savedKyc -> {
                kycSearchRepository.index(savedKyc);
                return savedKyc;
            })
            .map(kycMapper::toDto);
    }

    /**
     *  Get all the kycs where Customer is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<KycDTO> findAllWhereCustomerIsNull() {
        log.debug("Request to get all kycs where Customer is null");
        return StreamSupport.stream(kycRepository.findAll().spliterator(), false)
            .filter(kyc -> kyc.getCustomer() == null)
            .map(kycMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<KycDTO> findOne(Long id) {
        log.debug("Request to get Kyc : {}", id);
        return kycRepository.findById(id).map(kycMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Kyc : {}", id);
        kycRepository.deleteById(id);
        kycSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KycDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Kycs for query {}", query);
        return kycSearchRepository.search(query, pageable).map(kycMapper::toDto);
    }
}
