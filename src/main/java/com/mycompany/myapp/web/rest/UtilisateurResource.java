package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.UtilisateurRepository;
import com.mycompany.myapp.service.UtilisateurService;
import com.mycompany.myapp.service.dto.UtilisateurDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Utilisateur}.
 */
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurResource {

    private static final Logger LOG = LoggerFactory.getLogger(UtilisateurResource.class);

    private static final String ENTITY_NAME = "utilisateur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UtilisateurService utilisateurService;

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurResource(UtilisateurService utilisateurService, UtilisateurRepository utilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * {@code POST  /utilisateurs} : Create a new utilisateur.
     *
     * @param utilisateurDTO the utilisateurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new utilisateurDTO, or with status {@code 400 (Bad Request)} if the utilisateur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<UtilisateurDTO>> createUtilisateur(@Valid @RequestBody UtilisateurDTO utilisateurDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Utilisateur : {}", utilisateurDTO);
        if (utilisateurDTO.getId() != null) {
            throw new BadRequestAlertException("A new utilisateur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return utilisateurService
            .save(utilisateurDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/utilisateurs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /utilisateurs/:id} : Updates an existing utilisateur.
     *
     * @param id the id of the utilisateurDTO to save.
     * @param utilisateurDTO the utilisateurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilisateurDTO,
     * or with status {@code 400 (Bad Request)} if the utilisateurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the utilisateurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UtilisateurDTO>> updateUtilisateur(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UtilisateurDTO utilisateurDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Utilisateur : {}, {}", id, utilisateurDTO);
        if (utilisateurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilisateurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return utilisateurRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return utilisateurService
                    .update(utilisateurDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /utilisateurs/:id} : Partial updates given fields of an existing utilisateur, field will ignore if it is null
     *
     * @param id the id of the utilisateurDTO to save.
     * @param utilisateurDTO the utilisateurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated utilisateurDTO,
     * or with status {@code 400 (Bad Request)} if the utilisateurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the utilisateurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the utilisateurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UtilisateurDTO>> partialUpdateUtilisateur(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UtilisateurDTO utilisateurDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Utilisateur partially : {}, {}", id, utilisateurDTO);
        if (utilisateurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, utilisateurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return utilisateurRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UtilisateurDTO> result = utilisateurService.partialUpdate(utilisateurDTO);

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
     * {@code GET  /utilisateurs} : get all the utilisateurs.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of utilisateurs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<UtilisateurDTO>> getAllUtilisateurs(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all Utilisateurs");
        return utilisateurService.findAll().collectList();
    }

    /**
     * {@code GET  /utilisateurs} : get all the utilisateurs as a stream.
     * @return the {@link Flux} of utilisateurs.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UtilisateurDTO> getAllUtilisateursAsStream() {
        LOG.debug("REST request to get all Utilisateurs as a stream");
        return utilisateurService.findAll();
    }

    /**
     * {@code GET  /utilisateurs/:id} : get the "id" utilisateur.
     *
     * @param id the id of the utilisateurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the utilisateurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UtilisateurDTO>> getUtilisateur(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Utilisateur : {}", id);
        Mono<UtilisateurDTO> utilisateurDTO = utilisateurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(utilisateurDTO);
    }

    /**
     * {@code DELETE  /utilisateurs/:id} : delete the "id" utilisateur.
     *
     * @param id the id of the utilisateurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUtilisateur(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Utilisateur : {}", id);
        return utilisateurService
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
