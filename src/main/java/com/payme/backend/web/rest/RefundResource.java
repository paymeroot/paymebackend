package com.payme.backend.web.rest;

import com.payme.backend.repository.RefundRepository;
import com.payme.backend.service.RefundQueryService;
import com.payme.backend.service.RefundService;
import com.payme.backend.service.criteria.RefundCriteria;
import com.payme.backend.service.dto.RefundDTO;
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
 * REST controller for managing {@link com.payme.backend.domain.Refund}.
 */
@RestController
@RequestMapping("/api/refunds")
public class RefundResource {

    private static final Logger log = LoggerFactory.getLogger(RefundResource.class);

    private static final String ENTITY_NAME = "refund";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RefundService refundService;

    private final RefundRepository refundRepository;

    private final RefundQueryService refundQueryService;

    public RefundResource(RefundService refundService, RefundRepository refundRepository, RefundQueryService refundQueryService) {
        this.refundService = refundService;
        this.refundRepository = refundRepository;
        this.refundQueryService = refundQueryService;
    }

    /**
     * {@code POST  /refunds} : Create a new refund.
     *
     * @param refundDTO the refundDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new refundDTO, or with status {@code 400 (Bad Request)} if the refund has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RefundDTO> createRefund(@Valid @RequestBody RefundDTO refundDTO) throws URISyntaxException {
        log.debug("REST request to save Refund : {}", refundDTO);
        if (refundDTO.getId() != null) {
            throw new BadRequestAlertException("A new refund cannot already have an ID", ENTITY_NAME, "idexists");
        }
        refundDTO = refundService.save(refundDTO);
        return ResponseEntity.created(new URI("/api/refunds/" + refundDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, refundDTO.getId().toString()))
            .body(refundDTO);
    }

    /**
     * {@code PUT  /refunds/:id} : Updates an existing refund.
     *
     * @param id the id of the refundDTO to save.
     * @param refundDTO the refundDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated refundDTO,
     * or with status {@code 400 (Bad Request)} if the refundDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the refundDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RefundDTO> updateRefund(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RefundDTO refundDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Refund : {}, {}", id, refundDTO);
        if (refundDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, refundDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!refundRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        refundDTO = refundService.update(refundDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, refundDTO.getId().toString()))
            .body(refundDTO);
    }

    /**
     * {@code PATCH  /refunds/:id} : Partial updates given fields of an existing refund, field will ignore if it is null
     *
     * @param id the id of the refundDTO to save.
     * @param refundDTO the refundDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated refundDTO,
     * or with status {@code 400 (Bad Request)} if the refundDTO is not valid,
     * or with status {@code 404 (Not Found)} if the refundDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the refundDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RefundDTO> partialUpdateRefund(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RefundDTO refundDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Refund partially : {}, {}", id, refundDTO);
        if (refundDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, refundDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!refundRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RefundDTO> result = refundService.partialUpdate(refundDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, refundDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /refunds} : get all the refunds.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of refunds in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RefundDTO>> getAllRefunds(
        RefundCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Refunds by criteria: {}", criteria);

        Page<RefundDTO> page = refundQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /refunds/count} : count all the refunds.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countRefunds(RefundCriteria criteria) {
        log.debug("REST request to count Refunds by criteria: {}", criteria);
        return ResponseEntity.ok().body(refundQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /refunds/:id} : get the "id" refund.
     *
     * @param id the id of the refundDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the refundDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RefundDTO> getRefund(@PathVariable("id") Long id) {
        log.debug("REST request to get Refund : {}", id);
        Optional<RefundDTO> refundDTO = refundService.findOne(id);
        return ResponseUtil.wrapOrNotFound(refundDTO);
    }

    /**
     * {@code DELETE  /refunds/:id} : delete the "id" refund.
     *
     * @param id the id of the refundDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRefund(@PathVariable("id") Long id) {
        log.debug("REST request to delete Refund : {}", id);
        refundService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /refunds/_search?query=:query} : search for the refund corresponding
     * to the query.
     *
     * @param query the query of the refund search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<RefundDTO>> searchRefunds(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Refunds for query {}", query);
        try {
            Page<RefundDTO> page = refundService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
