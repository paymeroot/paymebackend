package com.payme.backend.web.rest;

import static com.payme.backend.domain.OperatorAsserts.*;
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
import com.payme.backend.domain.Operator;
import com.payme.backend.repository.OperatorRepository;
import com.payme.backend.repository.search.OperatorSearchRepository;
import com.payme.backend.service.dto.OperatorDTO;
import com.payme.backend.service.mapper.OperatorMapper;
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
 * Integration tests for the {@link OperatorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OperatorResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO_URL = "AAAAAAAAAA";
    private static final String UPDATED_LOGO_URL = "BBBBBBBBBB";

    private static final Float DEFAULT_TAX_PAY_IN = 1F;
    private static final Float UPDATED_TAX_PAY_IN = 2F;

    private static final Float DEFAULT_TAX_PAY_OUT = 1F;
    private static final Float UPDATED_TAX_PAY_OUT = 2F;

    private static final String ENTITY_API_URL = "/api/operators";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/operators/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private OperatorMapper operatorMapper;

    @Autowired
    private OperatorSearchRepository operatorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOperatorMockMvc;

    private Operator operator;

    private Operator insertedOperator;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Operator createEntity(EntityManager em) {
        Operator operator = new Operator()
            .nom(DEFAULT_NOM)
            .code(DEFAULT_CODE)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .logoUrl(DEFAULT_LOGO_URL)
            .taxPayIn(DEFAULT_TAX_PAY_IN)
            .taxPayOut(DEFAULT_TAX_PAY_OUT);
        return operator;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Operator createUpdatedEntity(EntityManager em) {
        Operator operator = new Operator()
            .nom(UPDATED_NOM)
            .code(UPDATED_CODE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .logoUrl(UPDATED_LOGO_URL)
            .taxPayIn(UPDATED_TAX_PAY_IN)
            .taxPayOut(UPDATED_TAX_PAY_OUT);
        return operator;
    }

    @BeforeEach
    public void initTest() {
        operator = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedOperator != null) {
            operatorRepository.delete(insertedOperator);
            operatorSearchRepository.delete(insertedOperator);
            insertedOperator = null;
        }
    }

    @Test
    @Transactional
    void createOperator() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        // Create the Operator
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);
        var returnedOperatorDTO = om.readValue(
            restOperatorMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operatorDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OperatorDTO.class
        );

        // Validate the Operator in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOperator = operatorMapper.toEntity(returnedOperatorDTO);
        assertOperatorUpdatableFieldsEquals(returnedOperator, getPersistedOperator(returnedOperator));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedOperator = returnedOperator;
    }

    @Test
    @Transactional
    void createOperatorWithExistingId() throws Exception {
        // Create the Operator with an existing ID
        operator.setId(1L);
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOperatorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operatorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Operator in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        // set the field null
        operator.setNom(null);

        // Create the Operator, which fails.
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        restOperatorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operatorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        // set the field null
        operator.setCode(null);

        // Create the Operator, which fails.
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        restOperatorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operatorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        // set the field null
        operator.setCountryCode(null);

        // Create the Operator, which fails.
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        restOperatorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operatorDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOperators() throws Exception {
        // Initialize the database
        insertedOperator = operatorRepository.saveAndFlush(operator);

        // Get all the operatorList
        restOperatorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operator.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].taxPayIn").value(hasItem(DEFAULT_TAX_PAY_IN.doubleValue())))
            .andExpect(jsonPath("$.[*].taxPayOut").value(hasItem(DEFAULT_TAX_PAY_OUT.doubleValue())));
    }

    @Test
    @Transactional
    void getOperator() throws Exception {
        // Initialize the database
        insertedOperator = operatorRepository.saveAndFlush(operator);

        // Get the operator
        restOperatorMockMvc
            .perform(get(ENTITY_API_URL_ID, operator.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(operator.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.logoUrl").value(DEFAULT_LOGO_URL))
            .andExpect(jsonPath("$.taxPayIn").value(DEFAULT_TAX_PAY_IN.doubleValue()))
            .andExpect(jsonPath("$.taxPayOut").value(DEFAULT_TAX_PAY_OUT.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingOperator() throws Exception {
        // Get the operator
        restOperatorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOperator() throws Exception {
        // Initialize the database
        insertedOperator = operatorRepository.saveAndFlush(operator);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        operatorSearchRepository.save(operator);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());

        // Update the operator
        Operator updatedOperator = operatorRepository.findById(operator.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedOperator are not directly saved in db
        em.detach(updatedOperator);
        updatedOperator
            .nom(UPDATED_NOM)
            .code(UPDATED_CODE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .logoUrl(UPDATED_LOGO_URL)
            .taxPayIn(UPDATED_TAX_PAY_IN)
            .taxPayOut(UPDATED_TAX_PAY_OUT);
        OperatorDTO operatorDTO = operatorMapper.toDto(updatedOperator);

        restOperatorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, operatorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(operatorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Operator in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOperatorToMatchAllProperties(updatedOperator);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Operator> operatorSearchList = Streamable.of(operatorSearchRepository.findAll()).toList();
                Operator testOperatorSearch = operatorSearchList.get(searchDatabaseSizeAfter - 1);

                assertOperatorAllPropertiesEquals(testOperatorSearch, updatedOperator);
            });
    }

    @Test
    @Transactional
    void putNonExistingOperator() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        operator.setId(longCount.incrementAndGet());

        // Create the Operator
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperatorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, operatorDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(operatorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Operator in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOperator() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        operator.setId(longCount.incrementAndGet());

        // Create the Operator
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperatorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(operatorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Operator in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOperator() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        operator.setId(longCount.incrementAndGet());

        // Create the Operator
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperatorMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operatorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Operator in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOperatorWithPatch() throws Exception {
        // Initialize the database
        insertedOperator = operatorRepository.saveAndFlush(operator);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the operator using partial update
        Operator partialUpdatedOperator = new Operator();
        partialUpdatedOperator.setId(operator.getId());

        partialUpdatedOperator
            .nom(UPDATED_NOM)
            .code(UPDATED_CODE)
            .logoUrl(UPDATED_LOGO_URL)
            .taxPayIn(UPDATED_TAX_PAY_IN)
            .taxPayOut(UPDATED_TAX_PAY_OUT);

        restOperatorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOperator.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOperator))
            )
            .andExpect(status().isOk());

        // Validate the Operator in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOperatorUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedOperator, operator), getPersistedOperator(operator));
    }

    @Test
    @Transactional
    void fullUpdateOperatorWithPatch() throws Exception {
        // Initialize the database
        insertedOperator = operatorRepository.saveAndFlush(operator);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the operator using partial update
        Operator partialUpdatedOperator = new Operator();
        partialUpdatedOperator.setId(operator.getId());

        partialUpdatedOperator
            .nom(UPDATED_NOM)
            .code(UPDATED_CODE)
            .countryCode(UPDATED_COUNTRY_CODE)
            .logoUrl(UPDATED_LOGO_URL)
            .taxPayIn(UPDATED_TAX_PAY_IN)
            .taxPayOut(UPDATED_TAX_PAY_OUT);

        restOperatorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOperator.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOperator))
            )
            .andExpect(status().isOk());

        // Validate the Operator in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOperatorUpdatableFieldsEquals(partialUpdatedOperator, getPersistedOperator(partialUpdatedOperator));
    }

    @Test
    @Transactional
    void patchNonExistingOperator() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        operator.setId(longCount.incrementAndGet());

        // Create the Operator
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperatorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, operatorDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(operatorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Operator in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOperator() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        operator.setId(longCount.incrementAndGet());

        // Create the Operator
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperatorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(operatorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Operator in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOperator() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        operator.setId(longCount.incrementAndGet());

        // Create the Operator
        OperatorDTO operatorDTO = operatorMapper.toDto(operator);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperatorMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(operatorDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Operator in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOperator() throws Exception {
        // Initialize the database
        insertedOperator = operatorRepository.saveAndFlush(operator);
        operatorRepository.save(operator);
        operatorSearchRepository.save(operator);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the operator
        restOperatorMockMvc
            .perform(delete(ENTITY_API_URL_ID, operator.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(operatorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOperator() throws Exception {
        // Initialize the database
        insertedOperator = operatorRepository.saveAndFlush(operator);
        operatorSearchRepository.save(operator);

        // Search the operator
        restOperatorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + operator.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operator.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].logoUrl").value(hasItem(DEFAULT_LOGO_URL)))
            .andExpect(jsonPath("$.[*].taxPayIn").value(hasItem(DEFAULT_TAX_PAY_IN.doubleValue())))
            .andExpect(jsonPath("$.[*].taxPayOut").value(hasItem(DEFAULT_TAX_PAY_OUT.doubleValue())));
    }

    protected long getRepositoryCount() {
        return operatorRepository.count();
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

    protected Operator getPersistedOperator(Operator operator) {
        return operatorRepository.findById(operator.getId()).orElseThrow();
    }

    protected void assertPersistedOperatorToMatchAllProperties(Operator expectedOperator) {
        assertOperatorAllPropertiesEquals(expectedOperator, getPersistedOperator(expectedOperator));
    }

    protected void assertPersistedOperatorToMatchUpdatableProperties(Operator expectedOperator) {
        assertOperatorAllUpdatablePropertiesEquals(expectedOperator, getPersistedOperator(expectedOperator));
    }
}
