package com.payme.backend.web.rest;

import com.payme.backend.repository.KycRepository;
import com.payme.backend.service.KycQueryService;
import com.payme.backend.service.KycService;
import com.payme.backend.service.criteria.KycCriteria;
import com.payme.backend.service.dto.KycDTO;
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
 * REST controller for managing {@link com.payme.backend.domain.Kyc}.
 */
@RestController
@RequestMapping("/api/kycs")
public class KycResource {

    private static final Logger log = LoggerFactory.getLogger(KycResource.class);

    private static final String ENTITY_NAME = "kyc";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KycService kycService;

    private final KycRepository kycRepository;

    private final KycQueryService kycQueryService;

    public KycResource(KycService kycService, KycRepository kycRepository, KycQueryService kycQueryService) {
        this.kycService = kycService;
        this.kycRepository = kycRepository;
        this.kycQueryService = kycQueryService;
    }

    /**
     * {@code POST  /kycs} : Create a new kyc.
     *
     * @param kycDTO the kycDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new kycDTO, or with status {@code 400 (Bad Request)} if the kyc has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<KycDTO> createKyc(@Valid @RequestBody KycDTO kycDTO) throws URISyntaxException {
        log.debug("REST request to save Kyc : {}", kycDTO);
        if (kycDTO.getId() != null) {
            throw new BadRequestAlertException("A new kyc cannot already have an ID", ENTITY_NAME, "idexists");
        }
        kycDTO = kycService.save(kycDTO);
        return ResponseEntity.created(new URI("/api/kycs/" + kycDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, kycDTO.getId().toString()))
            .body(kycDTO);
    }

    /**
     * {@code PUT  /kycs/:id} : Updates an existing kyc.
     *
     * @param id the id of the kycDTO to save.
     * @param kycDTO the kycDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kycDTO,
     * or with status {@code 400 (Bad Request)} if the kycDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the kycDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<KycDTO> updateKyc(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody KycDTO kycDTO)
        throws URISyntaxException {
        log.debug("REST request to update Kyc : {}, {}", id, kycDTO);
        if (kycDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, kycDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!kycRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        kycDTO = kycService.update(kycDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, kycDTO.getId().toString()))
            .body(kycDTO);
    }

    /**
     * {@code PATCH  /kycs/:id} : Partial updates given fields of an existing kyc, field will ignore if it is null
     *
     * @param id the id of the kycDTO to save.
     * @param kycDTO the kycDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kycDTO,
     * or with status {@code 400 (Bad Request)} if the kycDTO is not valid,
     * or with status {@code 404 (Not Found)} if the kycDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the kycDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<KycDTO> partialUpdateKyc(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody KycDTO kycDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Kyc partially : {}, {}", id, kycDTO);
        if (kycDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, kycDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!kycRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<KycDTO> result = kycService.partialUpdate(kycDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, kycDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /kycs} : get all the kycs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of kycs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<KycDTO>> getAllKycs(
        KycCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Kycs by criteria: {}", criteria);

        Page<KycDTO> page = kycQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /kycs/count} : count all the kycs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countKycs(KycCriteria criteria) {
        log.debug("REST request to count Kycs by criteria: {}", criteria);
        return ResponseEntity.ok().body(kycQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /kycs/:id} : get the "id" kyc.
     *
     * @param id the id of the kycDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the kycDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<KycDTO> getKyc(@PathVariable("id") Long id) {
        log.debug("REST request to get Kyc : {}", id);
        Optional<KycDTO> kycDTO = kycService.findOne(id);
        return ResponseUtil.wrapOrNotFound(kycDTO);
    }

    /**
     * {@code DELETE  /kycs/:id} : delete the "id" kyc.
     *
     * @param id the id of the kycDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKyc(@PathVariable("id") Long id) {
        log.debug("REST request to delete Kyc : {}", id);
        kycService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /kycs/_search?query=:query} : search for the kyc corresponding
     * to the query.
     *
     * @param query the query of the kyc search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<KycDTO>> searchKycs(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Kycs for query {}", query);
        try {
            Page<KycDTO> page = kycService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
