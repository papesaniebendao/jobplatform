package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CVRepository;
import com.mycompany.myapp.service.CVService;
import com.mycompany.myapp.service.dto.CVDTO;
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

import org.springframework.http.codec.multipart.FilePart;
import com.mycompany.myapp.domain.CV;


/**
 * REST controller for managing {@link com.mycompany.myapp.domain.CV}.
 */
@RestController
@RequestMapping("/api/cvs")
public class CVResource {

    private static final Logger LOG = LoggerFactory.getLogger(CVResource.class);

    private static final String ENTITY_NAME = "cV";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CVService cVService;

    private final CVRepository cVRepository;

    public CVResource(CVService cVService, CVRepository cVRepository) {
        this.cVService = cVService;
        this.cVRepository = cVRepository;
    }

    /**
     * {@code POST  /cvs} : Create a new cV.
     *
     * @param cVDTO the cVDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cVDTO, or with status {@code 400 (Bad Request)} if the cV has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<CVDTO>> createCV(@Valid @RequestBody CVDTO cVDTO) throws URISyntaxException {
        LOG.debug("REST request to save CV : {}", cVDTO);
        if (cVDTO.getId() != null) {
            throw new BadRequestAlertException("A new cV cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return cVService
            .save(cVDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/cvs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /cvs/:id} : Updates an existing cV.
     *
     * @param id the id of the cVDTO to save.
     * @param cVDTO the cVDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cVDTO,
     * or with status {@code 400 (Bad Request)} if the cVDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cVDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CVDTO>> updateCV(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CVDTO cVDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CV : {}, {}", id, cVDTO);
        if (cVDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cVDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cVRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return cVService
                    .update(cVDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /cvs/:id} : Partial updates given fields of an existing cV, field will ignore if it is null
     *
     * @param id the id of the cVDTO to save.
     * @param cVDTO the cVDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cVDTO,
     * or with status {@code 400 (Bad Request)} if the cVDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cVDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cVDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CVDTO>> partialUpdateCV(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CVDTO cVDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CV partially : {}, {}", id, cVDTO);
        if (cVDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cVDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cVRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CVDTO> result = cVService.partialUpdate(cVDTO);

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
     * {@code GET  /cvs} : get all the cVS.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cVS in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<CVDTO>> getAllCVS(@RequestParam(name = "filter", required = false) String filter) {
        if ("utilisateur-is-null".equals(filter)) {
            LOG.debug("REST request to get all CVs where utilisateur is null");
            return cVService.findAllWhereUtilisateurIsNull().collectList();
        }
        LOG.debug("REST request to get all CVS");
        return cVService.findAll().collectList();
    }

    /**
     * {@code GET  /cvs} : get all the cVS as a stream.
     * @return the {@link Flux} of cVS.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<CVDTO> getAllCVSAsStream() {
        LOG.debug("REST request to get all CVS as a stream");
        return cVService.findAll();
    }

    /**
     * {@code GET  /cvs/:id} : get the "id" cV.
     *
     * @param id the id of the cVDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cVDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CVDTO>> getCV(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CV : {}", id);
        Mono<CVDTO> cVDTO = cVService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cVDTO);
    }

    /**
     * {@code DELETE  /cvs/:id} : delete the "id" cV.
     *
     * @param id the id of the cVDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCV(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CV : {}", id);
        return cVService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * POST /cvs/upload : Upload d'un fichier CV.
     * @param file le fichier à uploader
     * @return CV sauvegardé
     */

     @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public Mono<ResponseEntity<CV>> uploadCv(@RequestPart("file") FilePart file) {
         return cVService.uploadCv(file).map(ResponseEntity::ok);  // ✅ Fixed: using cVService
     }

}
