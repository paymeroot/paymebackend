package com.payme.backend.web.rest;

import static com.payme.backend.domain.RefundAsserts.*;
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
import com.payme.backend.domain.Customer;
import com.payme.backend.domain.Refund;
import com.payme.backend.domain.Transaction;
import com.payme.backend.domain.enumeration.Status;
import com.payme.backend.repository.RefundRepository;
import com.payme.backend.repository.search.RefundSearchRepository;
import com.payme.backend.service.dto.RefundDTO;
import com.payme.backend.service.mapper.RefundMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link RefundResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RefundResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_TRANSACTION_REF = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_REF = "BBBBBBBBBB";

    private static final Instant DEFAULT_REFUND_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REFUND_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Status DEFAULT_REFUND_STATUS = Status.FAILURE;
    private static final Status UPDATED_REFUND_STATUS = Status.PENDING;

    private static final Float DEFAULT_AMOUNT = 1F;
    private static final Float UPDATED_AMOUNT = 2F;
    private static final Float SMALLER_AMOUNT = 1F - 1F;

    private static final String ENTITY_API_URL = "/api/refunds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/refunds/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private RefundSearchRepository refundSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRefundMockMvc;

    private Refund refund;

    private Refund insertedRefund;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refund createEntity(EntityManager em) {
        Refund refund = new Refund()
            .reference(DEFAULT_REFERENCE)
            .transactionRef(DEFAULT_TRANSACTION_REF)
            .refundDate(DEFAULT_REFUND_DATE)
            .refundStatus(DEFAULT_REFUND_STATUS)
            .amount(DEFAULT_AMOUNT);
        return refund;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Refund createUpdatedEntity(EntityManager em) {
        Refund refund = new Refund()
            .reference(UPDATED_REFERENCE)
            .transactionRef(UPDATED_TRANSACTION_REF)
            .refundDate(UPDATED_REFUND_DATE)
            .refundStatus(UPDATED_REFUND_STATUS)
            .amount(UPDATED_AMOUNT);
        return refund;
    }

    @BeforeEach
    public void initTest() {
        refund = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedRefund != null) {
            refundRepository.delete(insertedRefund);
            refundSearchRepository.delete(insertedRefund);
            insertedRefund = null;
        }
    }

    @Test
    @Transactional
    void createRefund() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);
        var returnedRefundDTO = om.readValue(
            restRefundMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RefundDTO.class
        );

        // Validate the Refund in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRefund = refundMapper.toEntity(returnedRefundDTO);
        assertRefundUpdatableFieldsEquals(returnedRefund, getPersistedRefund(returnedRefund));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedRefund = returnedRefund;
    }

    @Test
    @Transactional
    void createRefundWithExistingId() throws Exception {
        // Create the Refund with an existing ID
        refund.setId(1L);
        RefundDTO refundDTO = refundMapper.toDto(refund);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restRefundMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        // set the field null
        refund.setReference(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTransactionRefIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        // set the field null
        refund.setTransactionRef(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkRefundDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        // set the field null
        refund.setRefundDate(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        // set the field null
        refund.setAmount(null);

        // Create the Refund, which fails.
        RefundDTO refundDTO = refundMapper.toDto(refund);

        restRefundMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllRefunds() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(refund.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].transactionRef").value(hasItem(DEFAULT_TRANSACTION_REF)))
            .andExpect(jsonPath("$.[*].refundDate").value(hasItem(DEFAULT_REFUND_DATE.toString())))
            .andExpect(jsonPath("$.[*].refundStatus").value(hasItem(DEFAULT_REFUND_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())));
    }

    @Test
    @Transactional
    void getRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get the refund
        restRefundMockMvc
            .perform(get(ENTITY_API_URL_ID, refund.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(refund.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.transactionRef").value(DEFAULT_TRANSACTION_REF))
            .andExpect(jsonPath("$.refundDate").value(DEFAULT_REFUND_DATE.toString()))
            .andExpect(jsonPath("$.refundStatus").value(DEFAULT_REFUND_STATUS.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()));
    }

    @Test
    @Transactional
    void getRefundsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        Long id = refund.getId();

        defaultRefundFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRefundFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRefundFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference equals to
        defaultRefundFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference in
        defaultRefundFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference is not null
        defaultRefundFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference contains
        defaultRefundFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where reference does not contain
        defaultRefundFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRefundsByTransactionRefIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where transactionRef equals to
        defaultRefundFiltering("transactionRef.equals=" + DEFAULT_TRANSACTION_REF, "transactionRef.equals=" + UPDATED_TRANSACTION_REF);
    }

    @Test
    @Transactional
    void getAllRefundsByTransactionRefIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where transactionRef in
        defaultRefundFiltering(
            "transactionRef.in=" + DEFAULT_TRANSACTION_REF + "," + UPDATED_TRANSACTION_REF,
            "transactionRef.in=" + UPDATED_TRANSACTION_REF
        );
    }

    @Test
    @Transactional
    void getAllRefundsByTransactionRefIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where transactionRef is not null
        defaultRefundFiltering("transactionRef.specified=true", "transactionRef.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByTransactionRefContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where transactionRef contains
        defaultRefundFiltering("transactionRef.contains=" + DEFAULT_TRANSACTION_REF, "transactionRef.contains=" + UPDATED_TRANSACTION_REF);
    }

    @Test
    @Transactional
    void getAllRefundsByTransactionRefNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where transactionRef does not contain
        defaultRefundFiltering(
            "transactionRef.doesNotContain=" + UPDATED_TRANSACTION_REF,
            "transactionRef.doesNotContain=" + DEFAULT_TRANSACTION_REF
        );
    }

    @Test
    @Transactional
    void getAllRefundsByRefundDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundDate equals to
        defaultRefundFiltering("refundDate.equals=" + DEFAULT_REFUND_DATE, "refundDate.equals=" + UPDATED_REFUND_DATE);
    }

    @Test
    @Transactional
    void getAllRefundsByRefundDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundDate in
        defaultRefundFiltering("refundDate.in=" + DEFAULT_REFUND_DATE + "," + UPDATED_REFUND_DATE, "refundDate.in=" + UPDATED_REFUND_DATE);
    }

    @Test
    @Transactional
    void getAllRefundsByRefundDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundDate is not null
        defaultRefundFiltering("refundDate.specified=true", "refundDate.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByRefundStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundStatus equals to
        defaultRefundFiltering("refundStatus.equals=" + DEFAULT_REFUND_STATUS, "refundStatus.equals=" + UPDATED_REFUND_STATUS);
    }

    @Test
    @Transactional
    void getAllRefundsByRefundStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundStatus in
        defaultRefundFiltering(
            "refundStatus.in=" + DEFAULT_REFUND_STATUS + "," + UPDATED_REFUND_STATUS,
            "refundStatus.in=" + UPDATED_REFUND_STATUS
        );
    }

    @Test
    @Transactional
    void getAllRefundsByRefundStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where refundStatus is not null
        defaultRefundFiltering("refundStatus.specified=true", "refundStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount equals to
        defaultRefundFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount in
        defaultRefundFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is not null
        defaultRefundFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is greater than or equal to
        defaultRefundFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is less than or equal to
        defaultRefundFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is less than
        defaultRefundFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        // Get all the refundList where amount is greater than
        defaultRefundFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllRefundsByTransactionIsEqualToSomething() throws Exception {
        Transaction transaction;
        if (TestUtil.findAll(em, Transaction.class).isEmpty()) {
            refundRepository.saveAndFlush(refund);
            transaction = TransactionResourceIT.createEntity(em);
        } else {
            transaction = TestUtil.findAll(em, Transaction.class).get(0);
        }
        em.persist(transaction);
        em.flush();
        refund.setTransaction(transaction);
        refundRepository.saveAndFlush(refund);
        Long transactionId = transaction.getId();
        // Get all the refundList where transaction equals to transactionId
        defaultRefundShouldBeFound("transactionId.equals=" + transactionId);

        // Get all the refundList where transaction equals to (transactionId + 1)
        defaultRefundShouldNotBeFound("transactionId.equals=" + (transactionId + 1));
    }

    @Test
    @Transactional
    void getAllRefundsByCustomerIsEqualToSomething() throws Exception {
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            refundRepository.saveAndFlush(refund);
            customer = CustomerResourceIT.createEntity(em);
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customer);
        em.flush();
        refund.setCustomer(customer);
        refundRepository.saveAndFlush(refund);
        Long customerId = customer.getId();
        // Get all the refundList where customer equals to customerId
        defaultRefundShouldBeFound("customerId.equals=" + customerId);

        // Get all the refundList where customer equals to (customerId + 1)
        defaultRefundShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultRefundFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRefundShouldBeFound(shouldBeFound);
        defaultRefundShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRefundShouldBeFound(String filter) throws Exception {
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(refund.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].transactionRef").value(hasItem(DEFAULT_TRANSACTION_REF)))
            .andExpect(jsonPath("$.[*].refundDate").value(hasItem(DEFAULT_REFUND_DATE.toString())))
            .andExpect(jsonPath("$.[*].refundStatus").value(hasItem(DEFAULT_REFUND_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())));

        // Check, that the count call also returns 1
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRefundShouldNotBeFound(String filter) throws Exception {
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRefundMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRefund() throws Exception {
        // Get the refund
        restRefundMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        refundSearchRepository.save(refund);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());

        // Update the refund
        Refund updatedRefund = refundRepository.findById(refund.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRefund are not directly saved in db
        em.detach(updatedRefund);
        updatedRefund
            .reference(UPDATED_REFERENCE)
            .transactionRef(UPDATED_TRANSACTION_REF)
            .refundDate(UPDATED_REFUND_DATE)
            .refundStatus(UPDATED_REFUND_STATUS)
            .amount(UPDATED_AMOUNT);
        RefundDTO refundDTO = refundMapper.toDto(updatedRefund);

        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, refundDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRefundToMatchAllProperties(updatedRefund);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Refund> refundSearchList = Streamable.of(refundSearchRepository.findAll()).toList();
                Refund testRefundSearch = refundSearchList.get(searchDatabaseSizeAfter - 1);

                assertRefundAllPropertiesEquals(testRefundSearch, updatedRefund);
            });
    }

    @Test
    @Transactional
    void putNonExistingRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, refundDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(refundDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateRefundWithPatch() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the refund using partial update
        Refund partialUpdatedRefund = new Refund();
        partialUpdatedRefund.setId(refund.getId());

        partialUpdatedRefund
            .reference(UPDATED_REFERENCE)
            .transactionRef(UPDATED_TRANSACTION_REF)
            .refundDate(UPDATED_REFUND_DATE)
            .refundStatus(UPDATED_REFUND_STATUS);

        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRefund.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRefund))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRefundUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRefund, refund), getPersistedRefund(refund));
    }

    @Test
    @Transactional
    void fullUpdateRefundWithPatch() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the refund using partial update
        Refund partialUpdatedRefund = new Refund();
        partialUpdatedRefund.setId(refund.getId());

        partialUpdatedRefund
            .reference(UPDATED_REFERENCE)
            .transactionRef(UPDATED_TRANSACTION_REF)
            .refundDate(UPDATED_REFUND_DATE)
            .refundStatus(UPDATED_REFUND_STATUS)
            .amount(UPDATED_AMOUNT);

        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRefund.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRefund))
            )
            .andExpect(status().isOk());

        // Validate the Refund in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRefundUpdatableFieldsEquals(partialUpdatedRefund, getPersistedRefund(partialUpdatedRefund));
    }

    @Test
    @Transactional
    void patchNonExistingRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, refundDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRefund() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        refund.setId(longCount.incrementAndGet());

        // Create the Refund
        RefundDTO refundDTO = refundMapper.toDto(refund);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRefundMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(refundDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Refund in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);
        refundRepository.save(refund);
        refundSearchRepository.save(refund);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the refund
        restRefundMockMvc
            .perform(delete(ENTITY_API_URL_ID, refund.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(refundSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchRefund() throws Exception {
        // Initialize the database
        insertedRefund = refundRepository.saveAndFlush(refund);
        refundSearchRepository.save(refund);

        // Search the refund
        restRefundMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + refund.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(refund.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].transactionRef").value(hasItem(DEFAULT_TRANSACTION_REF)))
            .andExpect(jsonPath("$.[*].refundDate").value(hasItem(DEFAULT_REFUND_DATE.toString())))
            .andExpect(jsonPath("$.[*].refundStatus").value(hasItem(DEFAULT_REFUND_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())));
    }

    protected long getRepositoryCount() {
        return refundRepository.count();
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

    protected Refund getPersistedRefund(Refund refund) {
        return refundRepository.findById(refund.getId()).orElseThrow();
    }

    protected void assertPersistedRefundToMatchAllProperties(Refund expectedRefund) {
        assertRefundAllPropertiesEquals(expectedRefund, getPersistedRefund(expectedRefund));
    }

    protected void assertPersistedRefundToMatchUpdatableProperties(Refund expectedRefund) {
        assertRefundAllUpdatablePropertiesEquals(expectedRefund, getPersistedRefund(expectedRefund));
    }
}
