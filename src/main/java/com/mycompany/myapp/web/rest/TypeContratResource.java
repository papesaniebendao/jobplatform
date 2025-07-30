package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TypeContratRepository;
import com.mycompany.myapp.service.TypeContratService;
import com.mycompany.myapp.service.dto.TypeContratDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.TypeContrat}.
 */
@RestController
@RequestMapping("/api/type-contrats")
public class TypeContratResource {

    private static final Logger LOG = LoggerFactory.getLogger(TypeContratResource.class);

    private static final String ENTITY_NAME = "typeContrat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TypeContratService typeContratService;

    private final TypeContratRepository typeContratRepository;

    public TypeContratResource(TypeContratService typeContratService, TypeContratRepository typeContratRepository) {
        this.typeContratService = typeContratService;
        this.typeContratRepository = typeContratRepository;
    }

    /**
     * {@code POST  /type-contrats} : Create a new typeContrat.
     *
     * @param typeContratDTO the typeContratDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typeContratDTO, or with status {@code 400 (Bad Request)} if the typeContrat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TypeContratDTO>> createTypeContrat(@Valid @RequestBody TypeContratDTO typeContratDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TypeContrat : {}", typeContratDTO);
        if (typeContratDTO.getId() != null) {
            throw new BadRequestAlertException("A new typeContrat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return typeContratService
            .save(typeContratDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/type-contrats/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /type-contrats/:id} : Updates an existing typeContrat.
     *
     * @param id the id of the typeContratDTO to save.
     * @param typeContratDTO the typeContratDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeContratDTO,
     * or with status {@code 400 (Bad Request)} if the typeContratDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the typeContratDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TypeContratDTO>> updateTypeContrat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TypeContratDTO typeContratDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TypeContrat : {}, {}", id, typeContratDTO);
        if (typeContratDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeContratDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return typeContratRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return typeContratService
                    .update(typeContratDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /type-contrats/:id} : Partial updates given fields of an existing typeContrat, field will ignore if it is null
     *
     * @param id the id of the typeContratDTO to save.
     * @param typeContratDTO the typeContratDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeContratDTO,
     * or with status {@code 400 (Bad Request)} if the typeContratDTO is not valid,
     * or with status {@code 404 (Not Found)} if the typeContratDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the typeContratDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TypeContratDTO>> partialUpdateTypeContrat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TypeContratDTO typeContratDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TypeContrat partially : {}, {}", id, typeContratDTO);
        if (typeContratDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeContratDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return typeContratRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TypeContratDTO> result = typeContratService.partialUpdate(typeContratDTO);

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
     * {@code GET  /type-contrats} : get all the typeContrats.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of typeContrats in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<TypeContratDTO>> getAllTypeContrats() {
        LOG.debug("REST request to get all TypeContrats");
        return typeContratService.findAll().collectList();
    }

    /**
     * {@code GET  /type-contrats} : get all the typeContrats as a stream.
     * @return the {@link Flux} of typeContrats.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<TypeContratDTO> getAllTypeContratsAsStream() {
        LOG.debug("REST request to get all TypeContrats as a stream");
        return typeContratService.findAll();
    }

    /**
     * {@code GET  /type-contrats/:id} : get the "id" typeContrat.
     *
     * @param id the id of the typeContratDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typeContratDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TypeContratDTO>> getTypeContrat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TypeContrat : {}", id);
        Mono<TypeContratDTO> typeContratDTO = typeContratService.findOne(id);
        return ResponseUtil.wrapOrNotFound(typeContratDTO);
    }

    /**
     * {@code DELETE  /type-contrats/:id} : delete the "id" typeContrat.
     *
     * @param id the id of the typeContratDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTypeContrat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TypeContrat : {}", id);
        return typeContratService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
