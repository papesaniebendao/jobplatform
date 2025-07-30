package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.repository.CandidatureRepository;
import com.mycompany.myapp.service.CandidatureService;
import com.mycompany.myapp.service.dto.CandidatureDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Candidature}.
 */
@RestController
@RequestMapping("/api/candidatures")
public class CandidatureResource {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatureResource.class);

    private static final String ENTITY_NAME = "candidature";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandidatureService candidatureService;

    private final CandidatureRepository candidatureRepository;

    public CandidatureResource(CandidatureService candidatureService, CandidatureRepository candidatureRepository) {
        this.candidatureService = candidatureService;
        this.candidatureRepository = candidatureRepository;
    }

    /**
     * {@code POST  /candidatures} : Create a new candidature.
     *
     * @param candidatureDTO the candidatureDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candidatureDTO, or with status {@code 400 (Bad Request)} if the candidature has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<CandidatureDTO>> createCandidature(@Valid @RequestBody CandidatureDTO candidatureDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Candidature : {}", candidatureDTO);
        if (candidatureDTO.getId() != null) {
            throw new BadRequestAlertException("A new candidature cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return candidatureService
            .save(candidatureDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/candidatures/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /candidatures/:id} : Updates an existing candidature.
     *
     * @param id the id of the candidatureDTO to save.
     * @param candidatureDTO the candidatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidatureDTO,
     * or with status {@code 400 (Bad Request)} if the candidatureDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candidatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CandidatureDTO>> updateCandidature(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CandidatureDTO candidatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Candidature : {}, {}", id, candidatureDTO);
        if (candidatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return candidatureRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return candidatureService
                    .update(candidatureDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /candidatures/:id} : Partial updates given fields of an existing candidature, field will ignore if it is null
     *
     * @param id the id of the candidatureDTO to save.
     * @param candidatureDTO the candidatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidatureDTO,
     * or with status {@code 400 (Bad Request)} if the candidatureDTO is not valid,
     * or with status {@code 404 (Not Found)} if the candidatureDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the candidatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CandidatureDTO>> partialUpdateCandidature(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CandidatureDTO candidatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Candidature partially : {}, {}", id, candidatureDTO);
        if (candidatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return candidatureRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CandidatureDTO> result = candidatureService.partialUpdate(candidatureDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /candidatures} : get all the candidatures.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candidatures in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<CandidatureDTO>>> getAllCandidatures(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Candidatures");
        return candidatureService
            .countAll()
            .zipWith(candidatureService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /candidatures/:id} : get the "id" candidature.
     *
     * @param id the id of the candidatureDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candidatureDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CandidatureDTO>> getCandidature(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Candidature : {}", id);
        Mono<CandidatureDTO> candidatureDTO = candidatureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(candidatureDTO);
    }

    /**
     * {@code DELETE  /candidatures/:id} : delete the "id" candidature.
     *
     * @param id the id of the candidatureDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCandidature(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Candidature : {}", id);
        return candidatureService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    @PostMapping("/offres/{offreId}/postuler")
    public Mono<ResponseEntity<Candidature>> postuler(@PathVariable Long offreId) {
        return candidatureService.postuler(offreId)
            .map(candidature -> ResponseEntity.status(HttpStatus.CREATED).body(candidature))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
    
    

}
