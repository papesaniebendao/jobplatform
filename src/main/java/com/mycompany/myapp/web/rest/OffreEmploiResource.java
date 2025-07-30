package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.service.OffreEmploiService;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.OffreEmploi}.
 */
@RestController
@RequestMapping("/api/offre-emplois")
public class OffreEmploiResource {

    private static final Logger LOG = LoggerFactory.getLogger(OffreEmploiResource.class);

    private static final String ENTITY_NAME = "offreEmploi";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OffreEmploiService offreEmploiService;

    private final OffreEmploiRepository offreEmploiRepository;

    public OffreEmploiResource(OffreEmploiService offreEmploiService, OffreEmploiRepository offreEmploiRepository) {
        this.offreEmploiService = offreEmploiService;
        this.offreEmploiRepository = offreEmploiRepository;
    }

    /**
     * {@code POST  /offre-emplois} : Create a new offreEmploi.
     *
     * @param offreEmploiDTO the offreEmploiDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offreEmploiDTO, or with status {@code 400 (Bad Request)} if the offreEmploi has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<OffreEmploiDTO>> createOffreEmploi(@Valid @RequestBody OffreEmploiDTO offreEmploiDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save OffreEmploi : {}", offreEmploiDTO);
        if (offreEmploiDTO.getId() != null) {
            throw new BadRequestAlertException("A new offreEmploi cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return offreEmploiService
            .save(offreEmploiDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/offre-emplois/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /offre-emplois/:id} : Updates an existing offreEmploi.
     *
     * @param id the id of the offreEmploiDTO to save.
     * @param offreEmploiDTO the offreEmploiDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offreEmploiDTO,
     * or with status {@code 400 (Bad Request)} if the offreEmploiDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offreEmploiDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<OffreEmploiDTO>> updateOffreEmploi(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OffreEmploiDTO offreEmploiDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OffreEmploi : {}, {}", id, offreEmploiDTO);
        if (offreEmploiDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offreEmploiDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offreEmploiRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return offreEmploiService
                    .update(offreEmploiDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /offre-emplois/:id} : Partial updates given fields of an existing offreEmploi, field will ignore if it is null
     *
     * @param id the id of the offreEmploiDTO to save.
     * @param offreEmploiDTO the offreEmploiDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offreEmploiDTO,
     * or with status {@code 400 (Bad Request)} if the offreEmploiDTO is not valid,
     * or with status {@code 404 (Not Found)} if the offreEmploiDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the offreEmploiDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OffreEmploiDTO>> partialUpdateOffreEmploi(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OffreEmploiDTO offreEmploiDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OffreEmploi partially : {}, {}", id, offreEmploiDTO);
        if (offreEmploiDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offreEmploiDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return offreEmploiRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OffreEmploiDTO> result = offreEmploiService.partialUpdate(offreEmploiDTO);

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
     * {@code GET  /offre-emplois} : get all the offreEmplois.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offreEmplois in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<OffreEmploiDTO>>> getAllOffreEmplois(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of OffreEmplois");
        return offreEmploiService
            .countAll()
            .zipWith(offreEmploiService.findAll(pageable).collectList())
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
     * {@code GET  /offre-emplois/:id} : get the "id" offreEmploi.
     *
     * @param id the id of the offreEmploiDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offreEmploiDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OffreEmploiDTO>> getOffreEmploi(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OffreEmploi : {}", id);
        Mono<OffreEmploiDTO> offreEmploiDTO = offreEmploiService.findOne(id);
        return ResponseUtil.wrapOrNotFound(offreEmploiDTO);
    }

    /**
     * {@code DELETE  /offre-emplois/:id} : delete the "id" offreEmploi.
     *
     * @param id the id of the offreEmploiDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteOffreEmploi(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OffreEmploi : {}", id);
        return offreEmploiService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    @GetMapping("/offres/search")
    public Flux<OffreEmploiDTO> searchOffres(
        @RequestParam(required = false) String texte,
        @RequestParam(required = false) Long typeContratId,
        @RequestParam(required = false) String localisation,
        @RequestParam(required = false) Double salaireMin
    ) {
        return offreEmploiService.searchOffresMotParMot(texte, typeContratId, localisation, salaireMin);
    }
}
