package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.UtilisateurRepository;
import com.mycompany.myapp.service.dto.UtilisateurDTO;
import com.mycompany.myapp.service.mapper.UtilisateurMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Utilisateur}.
 */
@Service
@Transactional
public class UtilisateurService {

    private static final Logger LOG = LoggerFactory.getLogger(UtilisateurService.class);

    private final UtilisateurRepository utilisateurRepository;

    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, UtilisateurMapper utilisateurMapper) {
        this.utilisateurRepository = utilisateurRepository;
        this.utilisateurMapper = utilisateurMapper;
    }

    /**
     * Save a utilisateur.
     *
     * @param utilisateurDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UtilisateurDTO> save(UtilisateurDTO utilisateurDTO) {
        LOG.debug("Request to save Utilisateur : {}", utilisateurDTO);
        return utilisateurRepository.save(utilisateurMapper.toEntity(utilisateurDTO)).map(utilisateurMapper::toDto);
    }

    /**
     * Update a utilisateur.
     *
     * @param utilisateurDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UtilisateurDTO> update(UtilisateurDTO utilisateurDTO) {
        LOG.debug("Request to update Utilisateur : {}", utilisateurDTO);
        return utilisateurRepository.save(utilisateurMapper.toEntity(utilisateurDTO)).map(utilisateurMapper::toDto);
    }

    /**
     * Partially update a utilisateur.
     *
     * @param utilisateurDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UtilisateurDTO> partialUpdate(UtilisateurDTO utilisateurDTO) {
        LOG.debug("Request to partially update Utilisateur : {}", utilisateurDTO);

        return utilisateurRepository
            .findById(utilisateurDTO.getId())
            .map(existingUtilisateur -> {
                utilisateurMapper.partialUpdate(existingUtilisateur, utilisateurDTO);

                return existingUtilisateur;
            })
            .flatMap(utilisateurRepository::save)
            .map(utilisateurMapper::toDto);
    }

    /**
     * Get all the utilisateurs.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UtilisateurDTO> findAll() {
        LOG.debug("Request to get all Utilisateurs");
        return utilisateurRepository.findAll().map(utilisateurMapper::toDto);
    }

    /**
     * Get all the utilisateurs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<UtilisateurDTO> findAllWithEagerRelationships(Pageable pageable) {
        return utilisateurRepository.findAllWithEagerRelationships(pageable).map(utilisateurMapper::toDto);
    }

    /**
     * Returns the number of utilisateurs available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return utilisateurRepository.count();
    }

    /**
     * Get one utilisateur by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<UtilisateurDTO> findOne(Long id) {
        LOG.debug("Request to get Utilisateur : {}", id);
        return utilisateurRepository.findOneWithEagerRelationships(id).map(utilisateurMapper::toDto);
    }

    /**
     * Delete the utilisateur by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Utilisateur : {}", id);
        return utilisateurRepository.deleteById(id);
    }

    
}
