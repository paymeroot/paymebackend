package com.payme.backend.web.rest;

import static com.payme.backend.domain.ProfileAsserts.*;
import static com.payme.backend.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payme.backend.IntegrationTest;
import com.payme.backend.domain.Profile;
import com.payme.backend.domain.enumeration.Gender;
import com.payme.backend.repository.ProfileRepository;
import com.payme.backend.repository.search.ProfileSearchRepository;
import com.payme.backend.service.dto.ProfileDTO;
import com.payme.backend.service.mapper.ProfileMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProfileResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_VILLE = "AAAAAAAAAA";
    private static final String UPDATED_VILLE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_AVATAR_URL = "AAAAAAAAAA";
    private static final String UPDATED_AVATAR_URL = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENRE = Gender.MALE;
    private static final Gender UPDATED_GENRE = Gender.FEMALE;

    private static final String ENTITY_API_URL = "/api/profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/profiles/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private ProfileSearchRepository profileSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfileMockMvc;

    private Profile profile;

    private Profile insertedProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createEntity(EntityManager em) {
        Profile profile = new Profile()
            .email(DEFAULT_EMAIL)
            .address(DEFAULT_ADDRESS)
            .ville(DEFAULT_VILLE)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .avatarUrl(DEFAULT_AVATAR_URL)
            .genre(DEFAULT_GENRE);
        return profile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createUpdatedEntity(EntityManager em) {
        Profile profile = new Profile()
            .email(UPDATED_EMAIL)
            .address(UPDATED_ADDRESS)
            .ville(UPDATED_VILLE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .avatarUrl(UPDATED_AVATAR_URL)
            .genre(UPDATED_GENRE);
        return profile;
    }

    @BeforeEach
    public void initTest() {
        profile = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProfile != null) {
            profileRepository.delete(insertedProfile);
            profileSearchRepository.delete(insertedProfile);
            insertedProfile = null;
        }
    }

    @Test
    @Transactional
    void createProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());
        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);
        var returnedProfileDTO = om.readValue(
            restProfileMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfileDTO.class
        );

        // Validate the Profile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfile = profileMapper.toEntity(returnedProfileDTO);
        assertProfileUpdatableFieldsEquals(returnedProfile, getPersistedProfile(returnedProfile));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedProfile = returnedProfile;
    }

    @Test
    @Transactional
    void createProfileWithExistingId() throws Exception {
        // Create the Profile with an existing ID
        profile.setId(1L);
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllProfiles() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profile.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE.toString())));
    }

    @Test
    @Transactional
    void getProfile() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get the profile
        restProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, profile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profile.getId().intValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.avatarUrl").value(DEFAULT_AVATAR_URL))
            .andExpect(jsonPath("$.genre").value(DEFAULT_GENRE.toString()));
    }

    @Test
    @Transactional
    void getProfilesByIdFiltering() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        Long id = profile.getId();

        defaultProfileFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProfileFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProfileFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProfilesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where email equals to
        defaultProfileFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllProfilesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where email in
        defaultProfileFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllProfilesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where email is not null
        defaultProfileFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where email contains
        defaultProfileFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllProfilesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where email does not contain
        defaultProfileFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllProfilesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where address equals to
        defaultProfileFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllProfilesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where address in
        defaultProfileFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllProfilesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where address is not null
        defaultProfileFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where address contains
        defaultProfileFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllProfilesByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where address does not contain
        defaultProfileFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllProfilesByVilleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where ville equals to
        defaultProfileFiltering("ville.equals=" + DEFAULT_VILLE, "ville.equals=" + UPDATED_VILLE);
    }

    @Test
    @Transactional
    void getAllProfilesByVilleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where ville in
        defaultProfileFiltering("ville.in=" + DEFAULT_VILLE + "," + UPDATED_VILLE, "ville.in=" + UPDATED_VILLE);
    }

    @Test
    @Transactional
    void getAllProfilesByVilleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where ville is not null
        defaultProfileFiltering("ville.specified=true", "ville.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByVilleContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where ville contains
        defaultProfileFiltering("ville.contains=" + DEFAULT_VILLE, "ville.contains=" + UPDATED_VILLE);
    }

    @Test
    @Transactional
    void getAllProfilesByVilleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where ville does not contain
        defaultProfileFiltering("ville.doesNotContain=" + UPDATED_VILLE, "ville.doesNotContain=" + DEFAULT_VILLE);
    }

    @Test
    @Transactional
    void getAllProfilesByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where countryCode equals to
        defaultProfileFiltering("countryCode.equals=" + DEFAULT_COUNTRY_CODE, "countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllProfilesByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where countryCode in
        defaultProfileFiltering(
            "countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE,
            "countryCode.in=" + UPDATED_COUNTRY_CODE
        );
    }

    @Test
    @Transactional
    void getAllProfilesByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where countryCode is not null
        defaultProfileFiltering("countryCode.specified=true", "countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where countryCode contains
        defaultProfileFiltering("countryCode.contains=" + DEFAULT_COUNTRY_CODE, "countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllProfilesByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where countryCode does not contain
        defaultProfileFiltering("countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE, "countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl equals to
        defaultProfileFiltering("avatarUrl.equals=" + DEFAULT_AVATAR_URL, "avatarUrl.equals=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl in
        defaultProfileFiltering("avatarUrl.in=" + DEFAULT_AVATAR_URL + "," + UPDATED_AVATAR_URL, "avatarUrl.in=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl is not null
        defaultProfileFiltering("avatarUrl.specified=true", "avatarUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl contains
        defaultProfileFiltering("avatarUrl.contains=" + DEFAULT_AVATAR_URL, "avatarUrl.contains=" + UPDATED_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByAvatarUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where avatarUrl does not contain
        defaultProfileFiltering("avatarUrl.doesNotContain=" + UPDATED_AVATAR_URL, "avatarUrl.doesNotContain=" + DEFAULT_AVATAR_URL);
    }

    @Test
    @Transactional
    void getAllProfilesByGenreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where genre equals to
        defaultProfileFiltering("genre.equals=" + DEFAULT_GENRE, "genre.equals=" + UPDATED_GENRE);
    }

    @Test
    @Transactional
    void getAllProfilesByGenreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where genre in
        defaultProfileFiltering("genre.in=" + DEFAULT_GENRE + "," + UPDATED_GENRE, "genre.in=" + UPDATED_GENRE);
    }

    @Test
    @Transactional
    void getAllProfilesByGenreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        // Get all the profileList where genre is not null
        defaultProfileFiltering("genre.specified=true", "genre.specified=false");
    }

    private void defaultProfileFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProfileShouldBeFound(shouldBeFound);
        defaultProfileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProfileShouldBeFound(String filter) throws Exception {
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profile.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE.toString())));

        // Check, that the count call also returns 1
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProfileShouldNotBeFound(String filter) throws Exception {
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProfileMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProfile() throws Exception {
        // Get the profile
        restProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfile() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileSearchRepository.save(profile);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());

        // Update the profile
        Profile updatedProfile = profileRepository.findById(profile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfile are not directly saved in db
        em.detach(updatedProfile);
        updatedProfile
            .email(UPDATED_EMAIL)
            .address(UPDATED_ADDRESS)
            .ville(UPDATED_VILLE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .avatarUrl(UPDATED_AVATAR_URL)
            .genre(UPDATED_GENRE);
        ProfileDTO profileDTO = profileMapper.toDto(updatedProfile);

        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfileToMatchAllProperties(updatedProfile);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Profile> profileSearchList = Streamable.of(profileSearchRepository.findAll()).toList();
                Profile testProfileSearch = profileSearchList.get(searchDatabaseSizeAfter - 1);

                assertProfileAllPropertiesEquals(testProfileSearch, updatedProfile);
            });
    }

    @Test
    @Transactional
    void putNonExistingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile.email(UPDATED_EMAIL).avatarUrl(UPDATED_AVATAR_URL);

        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfile))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProfile, profile), getPersistedProfile(profile));
    }

    @Test
    @Transactional
    void fullUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile
            .email(UPDATED_EMAIL)
            .address(UPDATED_ADDRESS)
            .ville(UPDATED_VILLE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .avatarUrl(UPDATED_AVATAR_URL)
            .genre(UPDATED_GENRE);

        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfile))
            )
            .andExpect(status().isOk());

        // Validate the Profile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileUpdatableFieldsEquals(partialUpdatedProfile, getPersistedProfile(partialUpdatedProfile));
    }

    @Test
    @Transactional
    void patchNonExistingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profileDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());
        profile.setId(longCount.incrementAndGet());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(profileDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Profile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteProfile() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);
        profileRepository.save(profile);
        profileSearchRepository.save(profile);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the profile
        restProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, profile.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(profileSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchProfile() throws Exception {
        // Initialize the database
        insertedProfile = profileRepository.saveAndFlush(profile);
        profileSearchRepository.save(profile);

        // Search the profile
        restProfileMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + profile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profile.getId().intValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].avatarUrl").value(hasItem(DEFAULT_AVATAR_URL)))
            .andExpect(jsonPath("$.[*].genre").value(hasItem(DEFAULT_GENRE.toString())));
    }

    protected long getRepositoryCount() {
        return profileRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Profile getPersistedProfile(Profile profile) {
        return profileRepository.findById(profile.getId()).orElseThrow();
    }

    protected void assertPersistedProfileToMatchAllProperties(Profile expectedProfile) {
        assertProfileAllPropertiesEquals(expectedProfile, getPersistedProfile(expectedProfile));
    }

    protected void assertPersistedProfileToMatchUpdatableProperties(Profile expectedProfile) {
        assertProfileAllUpdatablePropertiesEquals(expectedProfile, getPersistedProfile(expectedProfile));
    }
}
