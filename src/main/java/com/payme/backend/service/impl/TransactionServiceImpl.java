package com.payme.backend.service.impl;

import com.payme.backend.domain.Transaction;
import com.payme.backend.repository.TransactionRepository;
import com.payme.backend.repository.search.TransactionSearchRepository;
import com.payme.backend.service.TransactionService;
import com.payme.backend.service.dto.TransactionDTO;
import com.payme.backend.service.mapper.TransactionMapper;
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
 * Service Implementation for managing {@link com.payme.backend.domain.Transaction}.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionSearchRepository transactionSearchRepository;

    public TransactionServiceImpl(
        TransactionRepository transactionRepository,
        TransactionMapper transactionMapper,
        TransactionSearchRepository transactionSearchRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionSearchRepository = transactionSearchRepository;
    }

    @Override
    public TransactionDTO save(TransactionDTO transactionDTO) {
        log.debug("Request to save Transaction : {}", transactionDTO);
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction = transactionRepository.save(transaction);
        transactionSearchRepository.index(transaction);
        return transactionMapper.toDto(transaction);
    }

    @Override
    public TransactionDTO update(TransactionDTO transactionDTO) {
        log.debug("Request to update Transaction : {}", transactionDTO);
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction = transactionRepository.save(transaction);
        transactionSearchRepository.index(transaction);
        return transactionMapper.toDto(transaction);
    }

    @Override
    public Optional<TransactionDTO> partialUpdate(TransactionDTO transactionDTO) {
        log.debug("Request to partially update Transaction : {}", transactionDTO);

        return transactionRepository
            .findById(transactionDTO.getId())
            .map(existingTransaction -> {
                transactionMapper.partialUpdate(existingTransaction, transactionDTO);

                return existingTransaction;
            })
            .map(transactionRepository::save)
            .map(savedTransaction -> {
                transactionSearchRepository.index(savedTransaction);
                return savedTransaction;
            })
            .map(transactionMapper::toDto);
    }

    /**
     *  Get all the transactions where Refund is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TransactionDTO> findAllWhereRefundIsNull() {
        log.debug("Request to get all transactions where Refund is null");
        return StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
            .filter(transaction -> transaction.getRefund() == null)
            .map(transactionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionDTO> findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        return transactionRepository.findById(id).map(transactionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Transaction : {}", id);
        transactionRepository.deleteById(id);
        transactionSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Transactions for query {}", query);
        return transactionSearchRepository.search(query, pageable).map(transactionMapper::toDto);
    }
}
