package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.TypeContratRepository;
import com.mycompany.myapp.service.dto.TypeContratDTO;
import com.mycompany.myapp.service.mapper.TypeContratMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TypeContrat}.
 */
@Service
@Transactional
public class TypeContratService {

    private static final Logger LOG = LoggerFactory.getLogger(TypeContratService.class);

    private final TypeContratRepository typeContratRepository;

    private final TypeContratMapper typeContratMapper;

    public TypeContratService(TypeContratRepository typeContratRepository, TypeContratMapper typeContratMapper) {
        this.typeContratRepository = typeContratRepository;
        this.typeContratMapper = typeContratMapper;
    }

    /**
     * Save a typeContrat.
     *
     * @param typeContratDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TypeContratDTO> save(TypeContratDTO typeContratDTO) {
        LOG.debug("Request to save TypeContrat : {}", typeContratDTO);
        return typeContratRepository.save(typeContratMapper.toEntity(typeContratDTO)).map(typeContratMapper::toDto);
    }

    /**
     * Update a typeContrat.
     *
     * @param typeContratDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<TypeContratDTO> update(TypeContratDTO typeContratDTO) {
        LOG.debug("Request to update TypeContrat : {}", typeContratDTO);
        return typeContratRepository.save(typeContratMapper.toEntity(typeContratDTO)).map(typeContratMapper::toDto);
    }

    /**
     * Partially update a typeContrat.
     *
     * @param typeContratDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TypeContratDTO> partialUpdate(TypeContratDTO typeContratDTO) {
        LOG.debug("Request to partially update TypeContrat : {}", typeContratDTO);

        return typeContratRepository
            .findById(typeContratDTO.getId())
            .map(existingTypeContrat -> {
                typeContratMapper.partialUpdate(existingTypeContrat, typeContratDTO);

                return existingTypeContrat;
            })
            .flatMap(typeContratRepository::save)
            .map(typeContratMapper::toDto);
    }

    /**
     * Get all the typeContrats.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TypeContratDTO> findAll() {
        LOG.debug("Request to get all TypeContrats");
        return typeContratRepository.findAll().map(typeContratMapper::toDto);
    }

    /**
     * Returns the number of typeContrats available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return typeContratRepository.count();
    }

    /**
     * Get one typeContrat by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TypeContratDTO> findOne(Long id) {
        LOG.debug("Request to get TypeContrat : {}", id);
        return typeContratRepository.findById(id).map(typeContratMapper::toDto);
    }

    /**
     * Delete the typeContrat by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete TypeContrat : {}", id);
        return typeContratRepository.deleteById(id);
    }
}
