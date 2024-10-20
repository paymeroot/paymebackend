package com.payme.backend.web.rest;

import static com.payme.backend.domain.TransactionAsserts.*;
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
import com.payme.backend.domain.Transaction;
import com.payme.backend.domain.enumeration.Status;
import com.payme.backend.domain.enumeration.Status;
import com.payme.backend.domain.enumeration.Status;
import com.payme.backend.repository.TransactionRepository;
import com.payme.backend.repository.search.TransactionSearchRepository;
import com.payme.backend.service.dto.TransactionDTO;
import com.payme.backend.service.mapper.TransactionMapper;
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
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final Instant DEFAULT_TRANSACTION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TRANSACTION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_SENDER_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_WALLET = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_WALLET = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_WALLET = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_WALLET = "BBBBBBBBBB";

    private static final Status DEFAULT_TRANSACTION_STATUS = Status.FAILURE;
    private static final Status UPDATED_TRANSACTION_STATUS = Status.PENDING;

    private static final Status DEFAULT_PAY_IN_STATUS = Status.FAILURE;
    private static final Status UPDATED_PAY_IN_STATUS = Status.PENDING;

    private static final Status DEFAULT_PAY_OUT_STATUS = Status.FAILURE;
    private static final Status UPDATED_PAY_OUT_STATUS = Status.PENDING;

    private static final Float DEFAULT_AMOUNT = 1F;
    private static final Float UPDATED_AMOUNT = 2F;
    private static final Float SMALLER_AMOUNT = 1F - 1F;

    private static final String DEFAULT_OBJECT = "AAAAAAAAAA";
    private static final String UPDATED_OBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_PAY_IN_FAILURE_REASON = "AAAAAAAAAA";
    private static final String UPDATED_PAY_IN_FAILURE_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_PAY_OUT_FAILURE_REASON = "AAAAAAAAAA";
    private static final String UPDATED_PAY_OUT_FAILURE_REASON = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER_COUNTRY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_COUNTRY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_COUNTRY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_COUNTRY_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/transactions/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TransactionSearchRepository transactionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    private Transaction insertedTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .reference(DEFAULT_REFERENCE)
            .transactionDate(DEFAULT_TRANSACTION_DATE)
            .senderNumber(DEFAULT_SENDER_NUMBER)
            .senderWallet(DEFAULT_SENDER_WALLET)
            .receiverNumber(DEFAULT_RECEIVER_NUMBER)
            .receiverWallet(DEFAULT_RECEIVER_WALLET)
            .transactionStatus(DEFAULT_TRANSACTION_STATUS)
            .payInStatus(DEFAULT_PAY_IN_STATUS)
            .payOutStatus(DEFAULT_PAY_OUT_STATUS)
            .amount(DEFAULT_AMOUNT)
            .object(DEFAULT_OBJECT)
            .payInFailureReason(DEFAULT_PAY_IN_FAILURE_REASON)
            .payOutFailureReason(DEFAULT_PAY_OUT_FAILURE_REASON)
            .senderCountryName(DEFAULT_SENDER_COUNTRY_NAME)
            .receiverCountryName(DEFAULT_RECEIVER_COUNTRY_NAME);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .reference(UPDATED_REFERENCE)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .senderNumber(UPDATED_SENDER_NUMBER)
            .senderWallet(UPDATED_SENDER_WALLET)
            .receiverNumber(UPDATED_RECEIVER_NUMBER)
            .receiverWallet(UPDATED_RECEIVER_WALLET)
            .transactionStatus(UPDATED_TRANSACTION_STATUS)
            .payInStatus(UPDATED_PAY_IN_STATUS)
            .payOutStatus(UPDATED_PAY_OUT_STATUS)
            .amount(UPDATED_AMOUNT)
            .object(UPDATED_OBJECT)
            .payInFailureReason(UPDATED_PAY_IN_FAILURE_REASON)
            .payOutFailureReason(UPDATED_PAY_OUT_FAILURE_REASON)
            .senderCountryName(UPDATED_SENDER_COUNTRY_NAME)
            .receiverCountryName(UPDATED_RECEIVER_COUNTRY_NAME);
        return transaction;
    }

    @BeforeEach
    public void initTest() {
        transaction = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedTransaction != null) {
            transactionRepository.delete(insertedTransaction);
            transactionSearchRepository.delete(insertedTransaction);
            insertedTransaction = null;
        }
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        var returnedTransactionDTO = om.readValue(
            restTransactionMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransactionDTO.class
        );

        // Validate the Transaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransaction = transactionMapper.toEntity(returnedTransactionDTO);
        assertTransactionUpdatableFieldsEquals(returnedTransaction, getPersistedTransaction(returnedTransaction));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTransaction = returnedTransaction;
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setReference(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTransactionDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setTransactionDate(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSenderNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setSenderNumber(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSenderWalletIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setSenderWallet(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReceiverNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setReceiverNumber(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReceiverWalletIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setReceiverWallet(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTransactionStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setTransactionStatus(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPayInStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setPayInStatus(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPayOutStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setPayOutStatus(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        // set the field null
        transaction.setAmount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].senderNumber").value(hasItem(DEFAULT_SENDER_NUMBER)))
            .andExpect(jsonPath("$.[*].senderWallet").value(hasItem(DEFAULT_SENDER_WALLET)))
            .andExpect(jsonPath("$.[*].receiverNumber").value(hasItem(DEFAULT_RECEIVER_NUMBER)))
            .andExpect(jsonPath("$.[*].receiverWallet").value(hasItem(DEFAULT_RECEIVER_WALLET)))
            .andExpect(jsonPath("$.[*].transactionStatus").value(hasItem(DEFAULT_TRANSACTION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payInStatus").value(hasItem(DEFAULT_PAY_IN_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payOutStatus").value(hasItem(DEFAULT_PAY_OUT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].object").value(hasItem(DEFAULT_OBJECT)))
            .andExpect(jsonPath("$.[*].payInFailureReason").value(hasItem(DEFAULT_PAY_IN_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].payOutFailureReason").value(hasItem(DEFAULT_PAY_OUT_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].senderCountryName").value(hasItem(DEFAULT_SENDER_COUNTRY_NAME)))
            .andExpect(jsonPath("$.[*].receiverCountryName").value(hasItem(DEFAULT_RECEIVER_COUNTRY_NAME)));
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.transactionDate").value(DEFAULT_TRANSACTION_DATE.toString()))
            .andExpect(jsonPath("$.senderNumber").value(DEFAULT_SENDER_NUMBER))
            .andExpect(jsonPath("$.senderWallet").value(DEFAULT_SENDER_WALLET))
            .andExpect(jsonPath("$.receiverNumber").value(DEFAULT_RECEIVER_NUMBER))
            .andExpect(jsonPath("$.receiverWallet").value(DEFAULT_RECEIVER_WALLET))
            .andExpect(jsonPath("$.transactionStatus").value(DEFAULT_TRANSACTION_STATUS.toString()))
            .andExpect(jsonPath("$.payInStatus").value(DEFAULT_PAY_IN_STATUS.toString()))
            .andExpect(jsonPath("$.payOutStatus").value(DEFAULT_PAY_OUT_STATUS.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.object").value(DEFAULT_OBJECT))
            .andExpect(jsonPath("$.payInFailureReason").value(DEFAULT_PAY_IN_FAILURE_REASON))
            .andExpect(jsonPath("$.payOutFailureReason").value(DEFAULT_PAY_OUT_FAILURE_REASON))
            .andExpect(jsonPath("$.senderCountryName").value(DEFAULT_SENDER_COUNTRY_NAME))
            .andExpect(jsonPath("$.receiverCountryName").value(DEFAULT_RECEIVER_COUNTRY_NAME));
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTransactionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTransactionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference equals to
        defaultTransactionFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference in
        defaultTransactionFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference is not null
        defaultTransactionFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference contains
        defaultTransactionFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reference does not contain
        defaultTransactionFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionDate equals to
        defaultTransactionFiltering(
            "transactionDate.equals=" + DEFAULT_TRANSACTION_DATE,
            "transactionDate.equals=" + UPDATED_TRANSACTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionDate in
        defaultTransactionFiltering(
            "transactionDate.in=" + DEFAULT_TRANSACTION_DATE + "," + UPDATED_TRANSACTION_DATE,
            "transactionDate.in=" + UPDATED_TRANSACTION_DATE
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionDate is not null
        defaultTransactionFiltering("transactionDate.specified=true", "transactionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderNumber equals to
        defaultTransactionFiltering("senderNumber.equals=" + DEFAULT_SENDER_NUMBER, "senderNumber.equals=" + UPDATED_SENDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderNumber in
        defaultTransactionFiltering(
            "senderNumber.in=" + DEFAULT_SENDER_NUMBER + "," + UPDATED_SENDER_NUMBER,
            "senderNumber.in=" + UPDATED_SENDER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderNumber is not null
        defaultTransactionFiltering("senderNumber.specified=true", "senderNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderNumber contains
        defaultTransactionFiltering("senderNumber.contains=" + DEFAULT_SENDER_NUMBER, "senderNumber.contains=" + UPDATED_SENDER_NUMBER);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderNumber does not contain
        defaultTransactionFiltering(
            "senderNumber.doesNotContain=" + UPDATED_SENDER_NUMBER,
            "senderNumber.doesNotContain=" + DEFAULT_SENDER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderWalletIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderWallet equals to
        defaultTransactionFiltering("senderWallet.equals=" + DEFAULT_SENDER_WALLET, "senderWallet.equals=" + UPDATED_SENDER_WALLET);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderWalletIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderWallet in
        defaultTransactionFiltering(
            "senderWallet.in=" + DEFAULT_SENDER_WALLET + "," + UPDATED_SENDER_WALLET,
            "senderWallet.in=" + UPDATED_SENDER_WALLET
        );
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderWalletIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderWallet is not null
        defaultTransactionFiltering("senderWallet.specified=true", "senderWallet.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderWalletContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderWallet contains
        defaultTransactionFiltering("senderWallet.contains=" + DEFAULT_SENDER_WALLET, "senderWallet.contains=" + UPDATED_SENDER_WALLET);
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderWalletNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderWallet does not contain
        defaultTransactionFiltering(
            "senderWallet.doesNotContain=" + UPDATED_SENDER_WALLET,
            "senderWallet.doesNotContain=" + DEFAULT_SENDER_WALLET
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverNumber equals to
        defaultTransactionFiltering("receiverNumber.equals=" + DEFAULT_RECEIVER_NUMBER, "receiverNumber.equals=" + UPDATED_RECEIVER_NUMBER);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverNumber in
        defaultTransactionFiltering(
            "receiverNumber.in=" + DEFAULT_RECEIVER_NUMBER + "," + UPDATED_RECEIVER_NUMBER,
            "receiverNumber.in=" + UPDATED_RECEIVER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverNumber is not null
        defaultTransactionFiltering("receiverNumber.specified=true", "receiverNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverNumber contains
        defaultTransactionFiltering(
            "receiverNumber.contains=" + DEFAULT_RECEIVER_NUMBER,
            "receiverNumber.contains=" + UPDATED_RECEIVER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverNumber does not contain
        defaultTransactionFiltering(
            "receiverNumber.doesNotContain=" + UPDATED_RECEIVER_NUMBER,
            "receiverNumber.doesNotContain=" + DEFAULT_RECEIVER_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverWalletIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverWallet equals to
        defaultTransactionFiltering("receiverWallet.equals=" + DEFAULT_RECEIVER_WALLET, "receiverWallet.equals=" + UPDATED_RECEIVER_WALLET);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverWalletIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverWallet in
        defaultTransactionFiltering(
            "receiverWallet.in=" + DEFAULT_RECEIVER_WALLET + "," + UPDATED_RECEIVER_WALLET,
            "receiverWallet.in=" + UPDATED_RECEIVER_WALLET
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverWalletIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverWallet is not null
        defaultTransactionFiltering("receiverWallet.specified=true", "receiverWallet.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverWalletContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverWallet contains
        defaultTransactionFiltering(
            "receiverWallet.contains=" + DEFAULT_RECEIVER_WALLET,
            "receiverWallet.contains=" + UPDATED_RECEIVER_WALLET
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverWalletNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverWallet does not contain
        defaultTransactionFiltering(
            "receiverWallet.doesNotContain=" + UPDATED_RECEIVER_WALLET,
            "receiverWallet.doesNotContain=" + DEFAULT_RECEIVER_WALLET
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionStatus equals to
        defaultTransactionFiltering(
            "transactionStatus.equals=" + DEFAULT_TRANSACTION_STATUS,
            "transactionStatus.equals=" + UPDATED_TRANSACTION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionStatus in
        defaultTransactionFiltering(
            "transactionStatus.in=" + DEFAULT_TRANSACTION_STATUS + "," + UPDATED_TRANSACTION_STATUS,
            "transactionStatus.in=" + UPDATED_TRANSACTION_STATUS
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where transactionStatus is not null
        defaultTransactionFiltering("transactionStatus.specified=true", "transactionStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByPayInStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payInStatus equals to
        defaultTransactionFiltering("payInStatus.equals=" + DEFAULT_PAY_IN_STATUS, "payInStatus.equals=" + UPDATED_PAY_IN_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByPayInStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payInStatus in
        defaultTransactionFiltering(
            "payInStatus.in=" + DEFAULT_PAY_IN_STATUS + "," + UPDATED_PAY_IN_STATUS,
            "payInStatus.in=" + UPDATED_PAY_IN_STATUS
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayInStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payInStatus is not null
        defaultTransactionFiltering("payInStatus.specified=true", "payInStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByPayOutStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payOutStatus equals to
        defaultTransactionFiltering("payOutStatus.equals=" + DEFAULT_PAY_OUT_STATUS, "payOutStatus.equals=" + UPDATED_PAY_OUT_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByPayOutStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payOutStatus in
        defaultTransactionFiltering(
            "payOutStatus.in=" + DEFAULT_PAY_OUT_STATUS + "," + UPDATED_PAY_OUT_STATUS,
            "payOutStatus.in=" + UPDATED_PAY_OUT_STATUS
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayOutStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payOutStatus is not null
        defaultTransactionFiltering("payOutStatus.specified=true", "payOutStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount equals to
        defaultTransactionFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount in
        defaultTransactionFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is not null
        defaultTransactionFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is greater than or equal to
        defaultTransactionFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is less than or equal to
        defaultTransactionFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is less than
        defaultTransactionFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where amount is greater than
        defaultTransactionFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllTransactionsByObjectIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where object equals to
        defaultTransactionFiltering("object.equals=" + DEFAULT_OBJECT, "object.equals=" + UPDATED_OBJECT);
    }

    @Test
    @Transactional
    void getAllTransactionsByObjectIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where object in
        defaultTransactionFiltering("object.in=" + DEFAULT_OBJECT + "," + UPDATED_OBJECT, "object.in=" + UPDATED_OBJECT);
    }

    @Test
    @Transactional
    void getAllTransactionsByObjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where object is not null
        defaultTransactionFiltering("object.specified=true", "object.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByObjectContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where object contains
        defaultTransactionFiltering("object.contains=" + DEFAULT_OBJECT, "object.contains=" + UPDATED_OBJECT);
    }

    @Test
    @Transactional
    void getAllTransactionsByObjectNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where object does not contain
        defaultTransactionFiltering("object.doesNotContain=" + UPDATED_OBJECT, "object.doesNotContain=" + DEFAULT_OBJECT);
    }

    @Test
    @Transactional
    void getAllTransactionsByPayInFailureReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payInFailureReason equals to
        defaultTransactionFiltering(
            "payInFailureReason.equals=" + DEFAULT_PAY_IN_FAILURE_REASON,
            "payInFailureReason.equals=" + UPDATED_PAY_IN_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayInFailureReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payInFailureReason in
        defaultTransactionFiltering(
            "payInFailureReason.in=" + DEFAULT_PAY_IN_FAILURE_REASON + "," + UPDATED_PAY_IN_FAILURE_REASON,
            "payInFailureReason.in=" + UPDATED_PAY_IN_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayInFailureReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payInFailureReason is not null
        defaultTransactionFiltering("payInFailureReason.specified=true", "payInFailureReason.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByPayInFailureReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payInFailureReason contains
        defaultTransactionFiltering(
            "payInFailureReason.contains=" + DEFAULT_PAY_IN_FAILURE_REASON,
            "payInFailureReason.contains=" + UPDATED_PAY_IN_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayInFailureReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payInFailureReason does not contain
        defaultTransactionFiltering(
            "payInFailureReason.doesNotContain=" + UPDATED_PAY_IN_FAILURE_REASON,
            "payInFailureReason.doesNotContain=" + DEFAULT_PAY_IN_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayOutFailureReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payOutFailureReason equals to
        defaultTransactionFiltering(
            "payOutFailureReason.equals=" + DEFAULT_PAY_OUT_FAILURE_REASON,
            "payOutFailureReason.equals=" + UPDATED_PAY_OUT_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayOutFailureReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payOutFailureReason in
        defaultTransactionFiltering(
            "payOutFailureReason.in=" + DEFAULT_PAY_OUT_FAILURE_REASON + "," + UPDATED_PAY_OUT_FAILURE_REASON,
            "payOutFailureReason.in=" + UPDATED_PAY_OUT_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayOutFailureReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payOutFailureReason is not null
        defaultTransactionFiltering("payOutFailureReason.specified=true", "payOutFailureReason.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByPayOutFailureReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payOutFailureReason contains
        defaultTransactionFiltering(
            "payOutFailureReason.contains=" + DEFAULT_PAY_OUT_FAILURE_REASON,
            "payOutFailureReason.contains=" + UPDATED_PAY_OUT_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByPayOutFailureReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where payOutFailureReason does not contain
        defaultTransactionFiltering(
            "payOutFailureReason.doesNotContain=" + UPDATED_PAY_OUT_FAILURE_REASON,
            "payOutFailureReason.doesNotContain=" + DEFAULT_PAY_OUT_FAILURE_REASON
        );
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderCountryNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderCountryName equals to
        defaultTransactionFiltering(
            "senderCountryName.equals=" + DEFAULT_SENDER_COUNTRY_NAME,
            "senderCountryName.equals=" + UPDATED_SENDER_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderCountryNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderCountryName in
        defaultTransactionFiltering(
            "senderCountryName.in=" + DEFAULT_SENDER_COUNTRY_NAME + "," + UPDATED_SENDER_COUNTRY_NAME,
            "senderCountryName.in=" + UPDATED_SENDER_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderCountryNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderCountryName is not null
        defaultTransactionFiltering("senderCountryName.specified=true", "senderCountryName.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderCountryNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderCountryName contains
        defaultTransactionFiltering(
            "senderCountryName.contains=" + DEFAULT_SENDER_COUNTRY_NAME,
            "senderCountryName.contains=" + UPDATED_SENDER_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllTransactionsBySenderCountryNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where senderCountryName does not contain
        defaultTransactionFiltering(
            "senderCountryName.doesNotContain=" + UPDATED_SENDER_COUNTRY_NAME,
            "senderCountryName.doesNotContain=" + DEFAULT_SENDER_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverCountryNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverCountryName equals to
        defaultTransactionFiltering(
            "receiverCountryName.equals=" + DEFAULT_RECEIVER_COUNTRY_NAME,
            "receiverCountryName.equals=" + UPDATED_RECEIVER_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverCountryNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverCountryName in
        defaultTransactionFiltering(
            "receiverCountryName.in=" + DEFAULT_RECEIVER_COUNTRY_NAME + "," + UPDATED_RECEIVER_COUNTRY_NAME,
            "receiverCountryName.in=" + UPDATED_RECEIVER_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverCountryNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverCountryName is not null
        defaultTransactionFiltering("receiverCountryName.specified=true", "receiverCountryName.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverCountryNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverCountryName contains
        defaultTransactionFiltering(
            "receiverCountryName.contains=" + DEFAULT_RECEIVER_COUNTRY_NAME,
            "receiverCountryName.contains=" + UPDATED_RECEIVER_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverCountryNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where receiverCountryName does not contain
        defaultTransactionFiltering(
            "receiverCountryName.doesNotContain=" + UPDATED_RECEIVER_COUNTRY_NAME,
            "receiverCountryName.doesNotContain=" + DEFAULT_RECEIVER_COUNTRY_NAME
        );
    }

    @Test
    @Transactional
    void getAllTransactionsByCustomerIsEqualToSomething() throws Exception {
        Customer customer;
        if (TestUtil.findAll(em, Customer.class).isEmpty()) {
            transactionRepository.saveAndFlush(transaction);
            customer = CustomerResourceIT.createEntity(em);
        } else {
            customer = TestUtil.findAll(em, Customer.class).get(0);
        }
        em.persist(customer);
        em.flush();
        transaction.setCustomer(customer);
        transactionRepository.saveAndFlush(transaction);
        Long customerId = customer.getId();
        // Get all the transactionList where customer equals to customerId
        defaultTransactionShouldBeFound("customerId.equals=" + customerId);

        // Get all the transactionList where customer equals to (customerId + 1)
        defaultTransactionShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }

    private void defaultTransactionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTransactionShouldBeFound(shouldBeFound);
        defaultTransactionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].senderNumber").value(hasItem(DEFAULT_SENDER_NUMBER)))
            .andExpect(jsonPath("$.[*].senderWallet").value(hasItem(DEFAULT_SENDER_WALLET)))
            .andExpect(jsonPath("$.[*].receiverNumber").value(hasItem(DEFAULT_RECEIVER_NUMBER)))
            .andExpect(jsonPath("$.[*].receiverWallet").value(hasItem(DEFAULT_RECEIVER_WALLET)))
            .andExpect(jsonPath("$.[*].transactionStatus").value(hasItem(DEFAULT_TRANSACTION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payInStatus").value(hasItem(DEFAULT_PAY_IN_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payOutStatus").value(hasItem(DEFAULT_PAY_OUT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].object").value(hasItem(DEFAULT_OBJECT)))
            .andExpect(jsonPath("$.[*].payInFailureReason").value(hasItem(DEFAULT_PAY_IN_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].payOutFailureReason").value(hasItem(DEFAULT_PAY_OUT_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].senderCountryName").value(hasItem(DEFAULT_SENDER_COUNTRY_NAME)))
            .andExpect(jsonPath("$.[*].receiverCountryName").value(hasItem(DEFAULT_RECEIVER_COUNTRY_NAME)));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        transactionSearchRepository.save(transaction);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .reference(UPDATED_REFERENCE)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .senderNumber(UPDATED_SENDER_NUMBER)
            .senderWallet(UPDATED_SENDER_WALLET)
            .receiverNumber(UPDATED_RECEIVER_NUMBER)
            .receiverWallet(UPDATED_RECEIVER_WALLET)
            .transactionStatus(UPDATED_TRANSACTION_STATUS)
            .payInStatus(UPDATED_PAY_IN_STATUS)
            .payOutStatus(UPDATED_PAY_OUT_STATUS)
            .amount(UPDATED_AMOUNT)
            .object(UPDATED_OBJECT)
            .payInFailureReason(UPDATED_PAY_IN_FAILURE_REASON)
            .payOutFailureReason(UPDATED_PAY_OUT_FAILURE_REASON)
            .senderCountryName(UPDATED_SENDER_COUNTRY_NAME)
            .receiverCountryName(UPDATED_RECEIVER_COUNTRY_NAME);
        TransactionDTO transactionDTO = transactionMapper.toDto(updatedTransaction);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransactionToMatchAllProperties(updatedTransaction);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Transaction> transactionSearchList = Streamable.of(transactionSearchRepository.findAll()).toList();
                Transaction testTransactionSearch = transactionSearchList.get(searchDatabaseSizeAfter - 1);

                assertTransactionAllPropertiesEquals(testTransactionSearch, updatedTransaction);
            });
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .reference(UPDATED_REFERENCE)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .senderNumber(UPDATED_SENDER_NUMBER)
            .receiverNumber(UPDATED_RECEIVER_NUMBER)
            .transactionStatus(UPDATED_TRANSACTION_STATUS)
            .payOutStatus(UPDATED_PAY_OUT_STATUS)
            .payOutFailureReason(UPDATED_PAY_OUT_FAILURE_REASON)
            .senderCountryName(UPDATED_SENDER_COUNTRY_NAME)
            .receiverCountryName(UPDATED_RECEIVER_COUNTRY_NAME);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransaction, transaction),
            getPersistedTransaction(transaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .reference(UPDATED_REFERENCE)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .senderNumber(UPDATED_SENDER_NUMBER)
            .senderWallet(UPDATED_SENDER_WALLET)
            .receiverNumber(UPDATED_RECEIVER_NUMBER)
            .receiverWallet(UPDATED_RECEIVER_WALLET)
            .transactionStatus(UPDATED_TRANSACTION_STATUS)
            .payInStatus(UPDATED_PAY_IN_STATUS)
            .payOutStatus(UPDATED_PAY_OUT_STATUS)
            .amount(UPDATED_AMOUNT)
            .object(UPDATED_OBJECT)
            .payInFailureReason(UPDATED_PAY_IN_FAILURE_REASON)
            .payOutFailureReason(UPDATED_PAY_OUT_FAILURE_REASON)
            .senderCountryName(UPDATED_SENDER_COUNTRY_NAME)
            .receiverCountryName(UPDATED_RECEIVER_COUNTRY_NAME);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionUpdatableFieldsEquals(partialUpdatedTransaction, getPersistedTransaction(partialUpdatedTransaction));
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);
        transactionRepository.save(transaction);
        transactionSearchRepository.save(transaction);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);
        transactionSearchRepository.save(transaction);

        // Search the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].senderNumber").value(hasItem(DEFAULT_SENDER_NUMBER)))
            .andExpect(jsonPath("$.[*].senderWallet").value(hasItem(DEFAULT_SENDER_WALLET)))
            .andExpect(jsonPath("$.[*].receiverNumber").value(hasItem(DEFAULT_RECEIVER_NUMBER)))
            .andExpect(jsonPath("$.[*].receiverWallet").value(hasItem(DEFAULT_RECEIVER_WALLET)))
            .andExpect(jsonPath("$.[*].transactionStatus").value(hasItem(DEFAULT_TRANSACTION_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payInStatus").value(hasItem(DEFAULT_PAY_IN_STATUS.toString())))
            .andExpect(jsonPath("$.[*].payOutStatus").value(hasItem(DEFAULT_PAY_OUT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].object").value(hasItem(DEFAULT_OBJECT)))
            .andExpect(jsonPath("$.[*].payInFailureReason").value(hasItem(DEFAULT_PAY_IN_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].payOutFailureReason").value(hasItem(DEFAULT_PAY_OUT_FAILURE_REASON)))
            .andExpect(jsonPath("$.[*].senderCountryName").value(hasItem(DEFAULT_SENDER_COUNTRY_NAME)))
            .andExpect(jsonPath("$.[*].receiverCountryName").value(hasItem(DEFAULT_RECEIVER_COUNTRY_NAME)));
    }

    protected long getRepositoryCount() {
        return transactionRepository.count();
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

    protected Transaction getPersistedTransaction(Transaction transaction) {
        return transactionRepository.findById(transaction.getId()).orElseThrow();
    }

    protected void assertPersistedTransactionToMatchAllProperties(Transaction expectedTransaction) {
        assertTransactionAllPropertiesEquals(expectedTransaction, getPersistedTransaction(expectedTransaction));
    }

    protected void assertPersistedTransactionToMatchUpdatableProperties(Transaction expectedTransaction) {
        assertTransactionAllUpdatablePropertiesEquals(expectedTransaction, getPersistedTransaction(expectedTransaction));
    }
}
