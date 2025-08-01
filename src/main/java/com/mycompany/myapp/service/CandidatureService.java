package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.CandidatureRepository;
import com.mycompany.myapp.service.dto.CandidatureDTO;
import com.mycompany.myapp.service.mapper.CandidatureMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Candidature}.
 */
@Service
@Transactional
public class CandidatureService {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatureService.class);

    private final CandidatureRepository candidatureRepository;

    private final CandidatureMapper candidatureMapper;

    public CandidatureService(CandidatureRepository candidatureRepository, CandidatureMapper candidatureMapper) {
        this.candidatureRepository = candidatureRepository;
        this.candidatureMapper = candidatureMapper;
    }

    /**
     * Save a candidature.
     *
     * @param candidatureDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CandidatureDTO> save(CandidatureDTO candidatureDTO) {
        LOG.debug("Request to save Candidature : {}", candidatureDTO);
        return candidatureRepository.save(candidatureMapper.toEntity(candidatureDTO)).map(candidatureMapper::toDto);
    }

    /**
     * Update a candidature.
     *
     * @param candidatureDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CandidatureDTO> update(CandidatureDTO candidatureDTO) {
        LOG.debug("Request to update Candidature : {}", candidatureDTO);
        return candidatureRepository.save(candidatureMapper.toEntity(candidatureDTO)).map(candidatureMapper::toDto);
    }

    /**
     * Partially update a candidature.
     *
     * @param candidatureDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CandidatureDTO> partialUpdate(CandidatureDTO candidatureDTO) {
        LOG.debug("Request to partially update Candidature : {}", candidatureDTO);

        return candidatureRepository
            .findById(candidatureDTO.getId())
            .map(existingCandidature -> {
                candidatureMapper.partialUpdate(existingCandidature, candidatureDTO);

                return existingCandidature;
            })
            .flatMap(candidatureRepository::save)
            .map(candidatureMapper::toDto);
    }

    /**
     * Get all the candidatures.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CandidatureDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Candidatures");
        return candidatureRepository.findAllBy(pageable).map(candidatureMapper::toDto);
    }

    /**
     * Returns the number of candidatures available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return candidatureRepository.count();
    }

    /**
     * Get one candidature by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CandidatureDTO> findOne(Long id) {
        LOG.debug("Request to get Candidature : {}", id);
        return candidatureRepository.findById(id).map(candidatureMapper::toDto);
    }

    /**
     * Delete the candidature by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Candidature : {}", id);
        return candidatureRepository.deleteById(id);
    }
}
