package com.payme.backend.service.impl;

import com.payme.backend.domain.Country;
import com.payme.backend.repository.CountryRepository;
import com.payme.backend.repository.search.CountrySearchRepository;
import com.payme.backend.service.CountryService;
import com.payme.backend.service.dto.CountryDTO;
import com.payme.backend.service.mapper.CountryMapper;
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
 * Service Implementation for managing {@link com.payme.backend.domain.Country}.
 */
@Service
@Transactional
public class CountryServiceImpl implements CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);

    private final CountryRepository countryRepository;

    private final CountryMapper countryMapper;

    private final CountrySearchRepository countrySearchRepository;

    public CountryServiceImpl(
        CountryRepository countryRepository,
        CountryMapper countryMapper,
        CountrySearchRepository countrySearchRepository
    ) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
        this.countrySearchRepository = countrySearchRepository;
    }

    @Override
    public CountryDTO save(CountryDTO countryDTO) {
        log.debug("Request to save Country : {}", countryDTO);
        Country country = countryMapper.toEntity(countryDTO);
        country = countryRepository.save(country);
        countrySearchRepository.index(country);
        return countryMapper.toDto(country);
    }

    @Override
    public CountryDTO update(CountryDTO countryDTO) {
        log.debug("Request to update Country : {}", countryDTO);
        Country country = countryMapper.toEntity(countryDTO);
        country = countryRepository.save(country);
        countrySearchRepository.index(country);
        return countryMapper.toDto(country);
    }

    @Override
    public Optional<CountryDTO> partialUpdate(CountryDTO countryDTO) {
        log.debug("Request to partially update Country : {}", countryDTO);

        return countryRepository
            .findById(countryDTO.getId())
            .map(existingCountry -> {
                countryMapper.partialUpdate(existingCountry, countryDTO);

                return existingCountry;
            })
            .map(countryRepository::save)
            .map(savedCountry -> {
                countrySearchRepository.index(savedCountry);
                return savedCountry;
            })
            .map(countryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CountryDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Countries");
        return countryRepository.findAll(pageable).map(countryMapper::toDto);
    }

    /**
     *  Get all the countries where Customer is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CountryDTO> findAllWhereCustomerIsNull() {
        log.debug("Request to get all countries where Customer is null");
        return StreamSupport.stream(countryRepository.findAll().spliterator(), false)
            .filter(country -> country.getCustomer() == null)
            .map(countryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the countries where Operator is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<CountryDTO> findAllWhereOperatorIsNull() {
        log.debug("Request to get all countries where Operator is null");
        return StreamSupport.stream(countryRepository.findAll().spliterator(), false)
            .filter(country -> country.getOperator() == null)
            .map(countryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CountryDTO> findOne(Long id) {
        log.debug("Request to get Country : {}", id);
        return countryRepository.findById(id).map(countryMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Country : {}", id);
        countryRepository.deleteById(id);
        countrySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CountryDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Countries for query {}", query);
        return countrySearchRepository.search(query, pageable).map(countryMapper::toDto);
    }
}
