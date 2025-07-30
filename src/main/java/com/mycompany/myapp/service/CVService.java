package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.CVRepository;
import com.mycompany.myapp.service.dto.CVDTO;
import com.mycompany.myapp.service.mapper.CVMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.CV}.
 */
@Service
@Transactional
public class CVService {

    private static final Logger LOG = LoggerFactory.getLogger(CVService.class);

    private final CVRepository cVRepository;

    private final CVMapper cVMapper;

    public CVService(CVRepository cVRepository, CVMapper cVMapper) {
        this.cVRepository = cVRepository;
        this.cVMapper = cVMapper;
    }

    /**
     * Save a cV.
     *
     * @param cVDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CVDTO> save(CVDTO cVDTO) {
        LOG.debug("Request to save CV : {}", cVDTO);
        return cVRepository.save(cVMapper.toEntity(cVDTO)).map(cVMapper::toDto);
    }

    /**
     * Update a cV.
     *
     * @param cVDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CVDTO> update(CVDTO cVDTO) {
        LOG.debug("Request to update CV : {}", cVDTO);
        return cVRepository.save(cVMapper.toEntity(cVDTO)).map(cVMapper::toDto);
    }

    /**
     * Partially update a cV.
     *
     * @param cVDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CVDTO> partialUpdate(CVDTO cVDTO) {
        LOG.debug("Request to partially update CV : {}", cVDTO);

        return cVRepository
            .findById(cVDTO.getId())
            .map(existingCV -> {
                cVMapper.partialUpdate(existingCV, cVDTO);

                return existingCV;
            })
            .flatMap(cVRepository::save)
            .map(cVMapper::toDto);
    }

    /**
     * Get all the cVS.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CVDTO> findAll() {
        LOG.debug("Request to get all CVS");
        return cVRepository.findAll().map(cVMapper::toDto);
    }

    /**
     *  Get all the cVS where Utilisateur is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CVDTO> findAllWhereUtilisateurIsNull() {
        LOG.debug("Request to get all cVS where Utilisateur is null");
        return cVRepository.findAllWhereUtilisateurIsNull().map(cVMapper::toDto);
    }

    /**
     * Returns the number of cVS available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return cVRepository.count();
    }

    /**
     * Get one cV by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CVDTO> findOne(Long id) {
        LOG.debug("Request to get CV : {}", id);
        return cVRepository.findById(id).map(cVMapper::toDto);
    }

    /**
     * Delete the cV by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete CV : {}", id);
        return cVRepository.deleteById(id);
    }
}
