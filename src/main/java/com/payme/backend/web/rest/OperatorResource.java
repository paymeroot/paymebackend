package com.payme.backend.web.rest;

import com.payme.backend.repository.OperatorRepository;
import com.payme.backend.service.OperatorService;
import com.payme.backend.service.dto.OperatorDTO;
import com.payme.backend.web.rest.errors.BadRequestAlertException;
import com.payme.backend.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.payme.backend.domain.Operator}.
 */
@RestController
@RequestMapping("/api/operators")
public class OperatorResource {

    private static final Logger log = LoggerFactory.getLogger(OperatorResource.class);

    private static final String ENTITY_NAME = "operator";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OperatorService operatorService;

    private final OperatorRepository operatorRepository;

    public OperatorResource(OperatorService operatorService, OperatorRepository operatorRepository) {
        this.operatorService = operatorService;
        this.operatorRepository = operatorRepository;
    }

    /**
     * {@code POST  /operators} : Create a new operator.
     *
     * @param operatorDTO the operatorDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new operatorDTO, or with status {@code 400 (Bad Request)} if the operator has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<OperatorDTO> createOperator(@Valid @RequestBody OperatorDTO operatorDTO) throws URISyntaxException {
        log.debug("REST request to save Operator : {}", operatorDTO);
        if (operatorDTO.getId() != null) {
            throw new BadRequestAlertException("A new operator cannot already have an ID", ENTITY_NAME, "idexists");
        }
        operatorDTO = operatorService.save(operatorDTO);
        return ResponseEntity.created(new URI("/api/operators/" + operatorDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, operatorDTO.getId().toString()))
            .body(operatorDTO);
    }

    /**
     * {@code PUT  /operators/:id} : Updates an existing operator.
     *
     * @param id the id of the operatorDTO to save.
     * @param operatorDTO the operatorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated operatorDTO,
     * or with status {@code 400 (Bad Request)} if the operatorDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the operatorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OperatorDTO> updateOperator(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OperatorDTO operatorDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Operator : {}, {}", id, operatorDTO);
        if (operatorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, operatorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!operatorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        operatorDTO = operatorService.update(operatorDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, operatorDTO.getId().toString()))
            .body(operatorDTO);
    }

    /**
     * {@code PATCH  /operators/:id} : Partial updates given fields of an existing operator, field will ignore if it is null
     *
     * @param id the id of the operatorDTO to save.
     * @param operatorDTO the operatorDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated operatorDTO,
     * or with status {@code 400 (Bad Request)} if the operatorDTO is not valid,
     * or with status {@code 404 (Not Found)} if the operatorDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the operatorDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OperatorDTO> partialUpdateOperator(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OperatorDTO operatorDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Operator partially : {}, {}", id, operatorDTO);
        if (operatorDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, operatorDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!operatorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OperatorDTO> result = operatorService.partialUpdate(operatorDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, operatorDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /operators} : get all the operators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of operators in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OperatorDTO>> getAllOperators(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Operators");
        Page<OperatorDTO> page = operatorService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /operators/:id} : get the "id" operator.
     *
     * @param id the id of the operatorDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the operatorDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OperatorDTO> getOperator(@PathVariable("id") Long id) {
        log.debug("REST request to get Operator : {}", id);
        Optional<OperatorDTO> operatorDTO = operatorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(operatorDTO);
    }

    /**
     * {@code DELETE  /operators/:id} : delete the "id" operator.
     *
     * @param id the id of the operatorDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperator(@PathVariable("id") Long id) {
        log.debug("REST request to delete Operator : {}", id);
        operatorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /operators/_search?query=:query} : search for the operator corresponding
     * to the query.
     *
     * @param query the query of the operator search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<OperatorDTO>> searchOperators(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Operators for query {}", query);
        try {
            Page<OperatorDTO> page = operatorService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
