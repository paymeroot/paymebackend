package com.payme.backend.web.rest;

import static com.payme.backend.domain.KycAsserts.*;
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
import com.payme.backend.domain.Kyc;
import com.payme.backend.repository.KycRepository;
import com.payme.backend.repository.search.KycSearchRepository;
import com.payme.backend.service.dto.KycDTO;
import com.payme.backend.service.mapper.KycMapper;
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
 * Integration tests for the {@link KycResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class KycResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE_PIECE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_PIECE = "BBBBBBBBBB";

    private static final String DEFAULT_NUMBER_PIECE = "AAAAAAAAAA";
    private static final String UPDATED_NUMBER_PIECE = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO_PIECE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO_PIECE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO_SELFIE_URL = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO_SELFIE_URL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/kycs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/kycs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KycRepository kycRepository;

    @Autowired
    private KycMapper kycMapper;

    @Autowired
    private KycSearchRepository kycSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restKycMockMvc;

    private Kyc kyc;

    private Kyc insertedKyc;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kyc createEntity(EntityManager em) {
        Kyc kyc = new Kyc()
            .reference(DEFAULT_REFERENCE)
            .typePiece(DEFAULT_TYPE_PIECE)
            .numberPiece(DEFAULT_NUMBER_PIECE)
            .photoPieceUrl(DEFAULT_PHOTO_PIECE_URL)
            .photoSelfieUrl(DEFAULT_PHOTO_SELFIE_URL);
        return kyc;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kyc createUpdatedEntity(EntityManager em) {
        Kyc kyc = new Kyc()
            .reference(UPDATED_REFERENCE)
            .typePiece(UPDATED_TYPE_PIECE)
            .numberPiece(UPDATED_NUMBER_PIECE)
            .photoPieceUrl(UPDATED_PHOTO_PIECE_URL)
            .photoSelfieUrl(UPDATED_PHOTO_SELFIE_URL);
        return kyc;
    }

    @BeforeEach
    public void initTest() {
        kyc = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedKyc != null) {
            kycRepository.delete(insertedKyc);
            kycSearchRepository.delete(insertedKyc);
            insertedKyc = null;
        }
    }

    @Test
    @Transactional
    void createKyc() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        // Create the Kyc
        KycDTO kycDTO = kycMapper.toDto(kyc);
        var returnedKycDTO = om.readValue(
            restKycMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kycDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            KycDTO.class
        );

        // Validate the Kyc in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedKyc = kycMapper.toEntity(returnedKycDTO);
        assertKycUpdatableFieldsEquals(returnedKyc, getPersistedKyc(returnedKyc));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedKyc = returnedKyc;
    }

    @Test
    @Transactional
    void createKycWithExistingId() throws Exception {
        // Create the Kyc with an existing ID
        kyc.setId(1L);
        KycDTO kycDTO = kycMapper.toDto(kyc);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restKycMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kycDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Kyc in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        // set the field null
        kyc.setReference(null);

        // Create the Kyc, which fails.
        KycDTO kycDTO = kycMapper.toDto(kyc);

        restKycMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kycDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTypePieceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        // set the field null
        kyc.setTypePiece(null);

        // Create the Kyc, which fails.
        KycDTO kycDTO = kycMapper.toDto(kyc);

        restKycMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kycDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNumberPieceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        // set the field null
        kyc.setNumberPiece(null);

        // Create the Kyc, which fails.
        KycDTO kycDTO = kycMapper.toDto(kyc);

        restKycMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kycDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhotoPieceUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        // set the field null
        kyc.setPhotoPieceUrl(null);

        // Create the Kyc, which fails.
        KycDTO kycDTO = kycMapper.toDto(kyc);

        restKycMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kycDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPhotoSelfieUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        // set the field null
        kyc.setPhotoSelfieUrl(null);

        // Create the Kyc, which fails.
        KycDTO kycDTO = kycMapper.toDto(kyc);

        restKycMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kycDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllKycs() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList
        restKycMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kyc.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typePiece").value(hasItem(DEFAULT_TYPE_PIECE)))
            .andExpect(jsonPath("$.[*].numberPiece").value(hasItem(DEFAULT_NUMBER_PIECE)))
            .andExpect(jsonPath("$.[*].photoPieceUrl").value(hasItem(DEFAULT_PHOTO_PIECE_URL)))
            .andExpect(jsonPath("$.[*].photoSelfieUrl").value(hasItem(DEFAULT_PHOTO_SELFIE_URL)));
    }

    @Test
    @Transactional
    void getKyc() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get the kyc
        restKycMockMvc
            .perform(get(ENTITY_API_URL_ID, kyc.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(kyc.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.typePiece").value(DEFAULT_TYPE_PIECE))
            .andExpect(jsonPath("$.numberPiece").value(DEFAULT_NUMBER_PIECE))
            .andExpect(jsonPath("$.photoPieceUrl").value(DEFAULT_PHOTO_PIECE_URL))
            .andExpect(jsonPath("$.photoSelfieUrl").value(DEFAULT_PHOTO_SELFIE_URL));
    }

    @Test
    @Transactional
    void getKycsByIdFiltering() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        Long id = kyc.getId();

        defaultKycFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultKycFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultKycFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllKycsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where reference equals to
        defaultKycFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllKycsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where reference in
        defaultKycFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllKycsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where reference is not null
        defaultKycFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllKycsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where reference contains
        defaultKycFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllKycsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where reference does not contain
        defaultKycFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllKycsByTypePieceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where typePiece equals to
        defaultKycFiltering("typePiece.equals=" + DEFAULT_TYPE_PIECE, "typePiece.equals=" + UPDATED_TYPE_PIECE);
    }

    @Test
    @Transactional
    void getAllKycsByTypePieceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where typePiece in
        defaultKycFiltering("typePiece.in=" + DEFAULT_TYPE_PIECE + "," + UPDATED_TYPE_PIECE, "typePiece.in=" + UPDATED_TYPE_PIECE);
    }

    @Test
    @Transactional
    void getAllKycsByTypePieceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where typePiece is not null
        defaultKycFiltering("typePiece.specified=true", "typePiece.specified=false");
    }

    @Test
    @Transactional
    void getAllKycsByTypePieceContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where typePiece contains
        defaultKycFiltering("typePiece.contains=" + DEFAULT_TYPE_PIECE, "typePiece.contains=" + UPDATED_TYPE_PIECE);
    }

    @Test
    @Transactional
    void getAllKycsByTypePieceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where typePiece does not contain
        defaultKycFiltering("typePiece.doesNotContain=" + UPDATED_TYPE_PIECE, "typePiece.doesNotContain=" + DEFAULT_TYPE_PIECE);
    }

    @Test
    @Transactional
    void getAllKycsByNumberPieceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where numberPiece equals to
        defaultKycFiltering("numberPiece.equals=" + DEFAULT_NUMBER_PIECE, "numberPiece.equals=" + UPDATED_NUMBER_PIECE);
    }

    @Test
    @Transactional
    void getAllKycsByNumberPieceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where numberPiece in
        defaultKycFiltering(
            "numberPiece.in=" + DEFAULT_NUMBER_PIECE + "," + UPDATED_NUMBER_PIECE,
            "numberPiece.in=" + UPDATED_NUMBER_PIECE
        );
    }

    @Test
    @Transactional
    void getAllKycsByNumberPieceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where numberPiece is not null
        defaultKycFiltering("numberPiece.specified=true", "numberPiece.specified=false");
    }

    @Test
    @Transactional
    void getAllKycsByNumberPieceContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where numberPiece contains
        defaultKycFiltering("numberPiece.contains=" + DEFAULT_NUMBER_PIECE, "numberPiece.contains=" + UPDATED_NUMBER_PIECE);
    }

    @Test
    @Transactional
    void getAllKycsByNumberPieceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where numberPiece does not contain
        defaultKycFiltering("numberPiece.doesNotContain=" + UPDATED_NUMBER_PIECE, "numberPiece.doesNotContain=" + DEFAULT_NUMBER_PIECE);
    }

    @Test
    @Transactional
    void getAllKycsByPhotoPieceUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoPieceUrl equals to
        defaultKycFiltering("photoPieceUrl.equals=" + DEFAULT_PHOTO_PIECE_URL, "photoPieceUrl.equals=" + UPDATED_PHOTO_PIECE_URL);
    }

    @Test
    @Transactional
    void getAllKycsByPhotoPieceUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoPieceUrl in
        defaultKycFiltering(
            "photoPieceUrl.in=" + DEFAULT_PHOTO_PIECE_URL + "," + UPDATED_PHOTO_PIECE_URL,
            "photoPieceUrl.in=" + UPDATED_PHOTO_PIECE_URL
        );
    }

    @Test
    @Transactional
    void getAllKycsByPhotoPieceUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoPieceUrl is not null
        defaultKycFiltering("photoPieceUrl.specified=true", "photoPieceUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllKycsByPhotoPieceUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoPieceUrl contains
        defaultKycFiltering("photoPieceUrl.contains=" + DEFAULT_PHOTO_PIECE_URL, "photoPieceUrl.contains=" + UPDATED_PHOTO_PIECE_URL);
    }

    @Test
    @Transactional
    void getAllKycsByPhotoPieceUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoPieceUrl does not contain
        defaultKycFiltering(
            "photoPieceUrl.doesNotContain=" + UPDATED_PHOTO_PIECE_URL,
            "photoPieceUrl.doesNotContain=" + DEFAULT_PHOTO_PIECE_URL
        );
    }

    @Test
    @Transactional
    void getAllKycsByPhotoSelfieUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoSelfieUrl equals to
        defaultKycFiltering("photoSelfieUrl.equals=" + DEFAULT_PHOTO_SELFIE_URL, "photoSelfieUrl.equals=" + UPDATED_PHOTO_SELFIE_URL);
    }

    @Test
    @Transactional
    void getAllKycsByPhotoSelfieUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoSelfieUrl in
        defaultKycFiltering(
            "photoSelfieUrl.in=" + DEFAULT_PHOTO_SELFIE_URL + "," + UPDATED_PHOTO_SELFIE_URL,
            "photoSelfieUrl.in=" + UPDATED_PHOTO_SELFIE_URL
        );
    }

    @Test
    @Transactional
    void getAllKycsByPhotoSelfieUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoSelfieUrl is not null
        defaultKycFiltering("photoSelfieUrl.specified=true", "photoSelfieUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllKycsByPhotoSelfieUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoSelfieUrl contains
        defaultKycFiltering("photoSelfieUrl.contains=" + DEFAULT_PHOTO_SELFIE_URL, "photoSelfieUrl.contains=" + UPDATED_PHOTO_SELFIE_URL);
    }

    @Test
    @Transactional
    void getAllKycsByPhotoSelfieUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        // Get all the kycList where photoSelfieUrl does not contain
        defaultKycFiltering(
            "photoSelfieUrl.doesNotContain=" + UPDATED_PHOTO_SELFIE_URL,
            "photoSelfieUrl.doesNotContain=" + DEFAULT_PHOTO_SELFIE_URL
        );
    }

    private void defaultKycFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultKycShouldBeFound(shouldBeFound);
        defaultKycShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultKycShouldBeFound(String filter) throws Exception {
        restKycMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kyc.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typePiece").value(hasItem(DEFAULT_TYPE_PIECE)))
            .andExpect(jsonPath("$.[*].numberPiece").value(hasItem(DEFAULT_NUMBER_PIECE)))
            .andExpect(jsonPath("$.[*].photoPieceUrl").value(hasItem(DEFAULT_PHOTO_PIECE_URL)))
            .andExpect(jsonPath("$.[*].photoSelfieUrl").value(hasItem(DEFAULT_PHOTO_SELFIE_URL)));

        // Check, that the count call also returns 1
        restKycMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultKycShouldNotBeFound(String filter) throws Exception {
        restKycMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restKycMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingKyc() throws Exception {
        // Get the kyc
        restKycMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKyc() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        kycSearchRepository.save(kyc);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());

        // Update the kyc
        Kyc updatedKyc = kycRepository.findById(kyc.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKyc are not directly saved in db
        em.detach(updatedKyc);
        updatedKyc
            .reference(UPDATED_REFERENCE)
            .typePiece(UPDATED_TYPE_PIECE)
            .numberPiece(UPDATED_NUMBER_PIECE)
            .photoPieceUrl(UPDATED_PHOTO_PIECE_URL)
            .photoSelfieUrl(UPDATED_PHOTO_SELFIE_URL);
        KycDTO kycDTO = kycMapper.toDto(updatedKyc);

        restKycMockMvc
            .perform(
                put(ENTITY_API_URL_ID, kycDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(kycDTO))
            )
            .andExpect(status().isOk());

        // Validate the Kyc in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKycToMatchAllProperties(updatedKyc);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Kyc> kycSearchList = Streamable.of(kycSearchRepository.findAll()).toList();
                Kyc testKycSearch = kycSearchList.get(searchDatabaseSizeAfter - 1);

                assertKycAllPropertiesEquals(testKycSearch, updatedKyc);
            });
    }

    @Test
    @Transactional
    void putNonExistingKyc() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        kyc.setId(longCount.incrementAndGet());

        // Create the Kyc
        KycDTO kycDTO = kycMapper.toDto(kyc);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKycMockMvc
            .perform(
                put(ENTITY_API_URL_ID, kycDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(kycDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kyc in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchKyc() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        kyc.setId(longCount.incrementAndGet());

        // Create the Kyc
        KycDTO kycDTO = kycMapper.toDto(kyc);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKycMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(kycDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kyc in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKyc() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        kyc.setId(longCount.incrementAndGet());

        // Create the Kyc
        KycDTO kycDTO = kycMapper.toDto(kyc);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKycMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kycDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Kyc in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateKycWithPatch() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kyc using partial update
        Kyc partialUpdatedKyc = new Kyc();
        partialUpdatedKyc.setId(kyc.getId());

        partialUpdatedKyc.numberPiece(UPDATED_NUMBER_PIECE);

        restKycMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKyc.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKyc))
            )
            .andExpect(status().isOk());

        // Validate the Kyc in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKycUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedKyc, kyc), getPersistedKyc(kyc));
    }

    @Test
    @Transactional
    void fullUpdateKycWithPatch() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kyc using partial update
        Kyc partialUpdatedKyc = new Kyc();
        partialUpdatedKyc.setId(kyc.getId());

        partialUpdatedKyc
            .reference(UPDATED_REFERENCE)
            .typePiece(UPDATED_TYPE_PIECE)
            .numberPiece(UPDATED_NUMBER_PIECE)
            .photoPieceUrl(UPDATED_PHOTO_PIECE_URL)
            .photoSelfieUrl(UPDATED_PHOTO_SELFIE_URL);

        restKycMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKyc.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKyc))
            )
            .andExpect(status().isOk());

        // Validate the Kyc in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKycUpdatableFieldsEquals(partialUpdatedKyc, getPersistedKyc(partialUpdatedKyc));
    }

    @Test
    @Transactional
    void patchNonExistingKyc() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        kyc.setId(longCount.incrementAndGet());

        // Create the Kyc
        KycDTO kycDTO = kycMapper.toDto(kyc);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKycMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, kycDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(kycDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kyc in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKyc() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        kyc.setId(longCount.incrementAndGet());

        // Create the Kyc
        KycDTO kycDTO = kycMapper.toDto(kyc);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKycMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(kycDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kyc in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKyc() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        kyc.setId(longCount.incrementAndGet());

        // Create the Kyc
        KycDTO kycDTO = kycMapper.toDto(kyc);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKycMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(kycDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Kyc in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteKyc() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);
        kycRepository.save(kyc);
        kycSearchRepository.save(kyc);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the kyc
        restKycMockMvc
            .perform(delete(ENTITY_API_URL_ID, kyc.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kycSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchKyc() throws Exception {
        // Initialize the database
        insertedKyc = kycRepository.saveAndFlush(kyc);
        kycSearchRepository.save(kyc);

        // Search the kyc
        restKycMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + kyc.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kyc.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typePiece").value(hasItem(DEFAULT_TYPE_PIECE)))
            .andExpect(jsonPath("$.[*].numberPiece").value(hasItem(DEFAULT_NUMBER_PIECE)))
            .andExpect(jsonPath("$.[*].photoPieceUrl").value(hasItem(DEFAULT_PHOTO_PIECE_URL)))
            .andExpect(jsonPath("$.[*].photoSelfieUrl").value(hasItem(DEFAULT_PHOTO_SELFIE_URL)));
    }

    protected long getRepositoryCount() {
        return kycRepository.count();
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

    protected Kyc getPersistedKyc(Kyc kyc) {
        return kycRepository.findById(kyc.getId()).orElseThrow();
    }

    protected void assertPersistedKycToMatchAllProperties(Kyc expectedKyc) {
        assertKycAllPropertiesEquals(expectedKyc, getPersistedKyc(expectedKyc));
    }

    protected void assertPersistedKycToMatchUpdatableProperties(Kyc expectedKyc) {
        assertKycAllUpdatablePropertiesEquals(expectedKyc, getPersistedKyc(expectedKyc));
    }
}
