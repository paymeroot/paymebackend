package com.payme.backend.service.impl;

import com.payme.backend.domain.Refund;
import com.payme.backend.repository.RefundRepository;
import com.payme.backend.repository.search.RefundSearchRepository;
import com.payme.backend.service.RefundService;
import com.payme.backend.service.dto.RefundDTO;
import com.payme.backend.service.mapper.RefundMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.payme.backend.domain.Refund}.
 */
@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private static final Logger log = LoggerFactory.getLogger(RefundServiceImpl.class);

    private final RefundRepository refundRepository;

    private final RefundMapper refundMapper;

    private final RefundSearchRepository refundSearchRepository;

    public RefundServiceImpl(RefundRepository refundRepository, RefundMapper refundMapper, RefundSearchRepository refundSearchRepository) {
        this.refundRepository = refundRepository;
        this.refundMapper = refundMapper;
        this.refundSearchRepository = refundSearchRepository;
    }

    @Override
    public RefundDTO save(RefundDTO refundDTO) {
        log.debug("Request to save Refund : {}", refundDTO);
        Refund refund = refundMapper.toEntity(refundDTO);
        refund = refundRepository.save(refund);
        refundSearchRepository.index(refund);
        return refundMapper.toDto(refund);
    }

    @Override
    public RefundDTO update(RefundDTO refundDTO) {
        log.debug("Request to update Refund : {}", refundDTO);
        Refund refund = refundMapper.toEntity(refundDTO);
        refund = refundRepository.save(refund);
        refundSearchRepository.index(refund);
        return refundMapper.toDto(refund);
    }

    @Override
    public Optional<RefundDTO> partialUpdate(RefundDTO refundDTO) {
        log.debug("Request to partially update Refund : {}", refundDTO);

        return refundRepository
            .findById(refundDTO.getId())
            .map(existingRefund -> {
                refundMapper.partialUpdate(existingRefund, refundDTO);

                return existingRefund;
            })
            .map(refundRepository::save)
            .map(savedRefund -> {
                refundSearchRepository.index(savedRefund);
                return savedRefund;
            })
            .map(refundMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefundDTO> findOne(Long id) {
        log.debug("Request to get Refund : {}", id);
        return refundRepository.findById(id).map(refundMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Refund : {}", id);
        refundRepository.deleteById(id);
        refundSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RefundDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Refunds for query {}", query);
        return refundSearchRepository.search(query, pageable).map(refundMapper::toDto);
    }
}
