package com.payme.backend.service.impl;

import com.payme.backend.domain.Operator;
import com.payme.backend.repository.OperatorRepository;
import com.payme.backend.repository.search.OperatorSearchRepository;
import com.payme.backend.service.OperatorService;
import com.payme.backend.service.dto.OperatorDTO;
import com.payme.backend.service.mapper.OperatorMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.payme.backend.domain.Operator}.
 */
@Service
@Transactional
public class OperatorServiceImpl implements OperatorService {

    private static final Logger log = LoggerFactory.getLogger(OperatorServiceImpl.class);

    private final OperatorRepository operatorRepository;

    private final OperatorMapper operatorMapper;

    private final OperatorSearchRepository operatorSearchRepository;

    public OperatorServiceImpl(
        OperatorRepository operatorRepository,
        OperatorMapper operatorMapper,
        OperatorSearchRepository operatorSearchRepository
    ) {
        this.operatorRepository = operatorRepository;
        this.operatorMapper = operatorMapper;
        this.operatorSearchRepository = operatorSearchRepository;
    }

    @Override
    public OperatorDTO save(OperatorDTO operatorDTO) {
        log.debug("Request to save Operator : {}", operatorDTO);
        Operator operator = operatorMapper.toEntity(operatorDTO);
        operator = operatorRepository.save(operator);
        operatorSearchRepository.index(operator);
        return operatorMapper.toDto(operator);
    }

    @Override
    public OperatorDTO update(OperatorDTO operatorDTO) {
        log.debug("Request to update Operator : {}", operatorDTO);
        Operator operator = operatorMapper.toEntity(operatorDTO);
        operator = operatorRepository.save(operator);
        operatorSearchRepository.index(operator);
        return operatorMapper.toDto(operator);
    }

    @Override
    public Optional<OperatorDTO> partialUpdate(OperatorDTO operatorDTO) {
        log.debug("Request to partially update Operator : {}", operatorDTO);

        return operatorRepository
            .findById(operatorDTO.getId())
            .map(existingOperator -> {
                operatorMapper.partialUpdate(existingOperator, operatorDTO);

                return existingOperator;
            })
            .map(operatorRepository::save)
            .map(savedOperator -> {
                operatorSearchRepository.index(savedOperator);
                return savedOperator;
            })
            .map(operatorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OperatorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Operators");
        return operatorRepository.findAll(pageable).map(operatorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OperatorDTO> findOne(Long id) {
        log.debug("Request to get Operator : {}", id);
        return operatorRepository.findById(id).map(operatorMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Operator : {}", id);
        operatorRepository.deleteById(id);
        operatorSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OperatorDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Operators for query {}", query);
        return operatorSearchRepository.search(query, pageable).map(operatorMapper::toDto);
    }
}
