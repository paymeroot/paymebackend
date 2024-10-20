package com.payme.backend.service.impl;

import com.payme.backend.domain.Profile;
import com.payme.backend.repository.ProfileRepository;
import com.payme.backend.repository.search.ProfileSearchRepository;
import com.payme.backend.service.ProfileService;
import com.payme.backend.service.dto.ProfileDTO;
import com.payme.backend.service.mapper.ProfileMapper;
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
 * Service Implementation for managing {@link com.payme.backend.domain.Profile}.
 */
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final ProfileRepository profileRepository;

    private final ProfileMapper profileMapper;

    private final ProfileSearchRepository profileSearchRepository;

    public ProfileServiceImpl(
        ProfileRepository profileRepository,
        ProfileMapper profileMapper,
        ProfileSearchRepository profileSearchRepository
    ) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.profileSearchRepository = profileSearchRepository;
    }

    @Override
    public ProfileDTO save(ProfileDTO profileDTO) {
        log.debug("Request to save Profile : {}", profileDTO);
        Profile profile = profileMapper.toEntity(profileDTO);
        profile = profileRepository.save(profile);
        profileSearchRepository.index(profile);
        return profileMapper.toDto(profile);
    }

    @Override
    public ProfileDTO update(ProfileDTO profileDTO) {
        log.debug("Request to update Profile : {}", profileDTO);
        Profile profile = profileMapper.toEntity(profileDTO);
        profile = profileRepository.save(profile);
        profileSearchRepository.index(profile);
        return profileMapper.toDto(profile);
    }

    @Override
    public Optional<ProfileDTO> partialUpdate(ProfileDTO profileDTO) {
        log.debug("Request to partially update Profile : {}", profileDTO);

        return profileRepository
            .findById(profileDTO.getId())
            .map(existingProfile -> {
                profileMapper.partialUpdate(existingProfile, profileDTO);

                return existingProfile;
            })
            .map(profileRepository::save)
            .map(savedProfile -> {
                profileSearchRepository.index(savedProfile);
                return savedProfile;
            })
            .map(profileMapper::toDto);
    }

    /**
     *  Get all the profiles where Customer is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProfileDTO> findAllWhereCustomerIsNull() {
        log.debug("Request to get all profiles where Customer is null");
        return StreamSupport.stream(profileRepository.findAll().spliterator(), false)
            .filter(profile -> profile.getCustomer() == null)
            .map(profileMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfileDTO> findOne(Long id) {
        log.debug("Request to get Profile : {}", id);
        return profileRepository.findById(id).map(profileMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Profile : {}", id);
        profileRepository.deleteById(id);
        profileSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Profiles for query {}", query);
        return profileSearchRepository.search(query, pageable).map(profileMapper::toDto);
    }
}
