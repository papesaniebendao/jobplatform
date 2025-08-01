package com.mycompany.myapp.service;

import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.mapper.OffreEmploiMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.OffreEmploi}.
 */
@Service
@Transactional
public class OffreEmploiService {

    private static final Logger LOG = LoggerFactory.getLogger(OffreEmploiService.class);

    private final OffreEmploiRepository offreEmploiRepository;

    private final OffreEmploiMapper offreEmploiMapper;

    public OffreEmploiService(OffreEmploiRepository offreEmploiRepository, OffreEmploiMapper offreEmploiMapper) {
        this.offreEmploiRepository = offreEmploiRepository;
        this.offreEmploiMapper = offreEmploiMapper;
    }

    /**
     * Save a offreEmploi.
     *
     * @param offreEmploiDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OffreEmploiDTO> save(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to save OffreEmploi : {}", offreEmploiDTO);
        return offreEmploiRepository.save(offreEmploiMapper.toEntity(offreEmploiDTO)).map(offreEmploiMapper::toDto);
    }

    /**
     * Update a offreEmploi.
     *
     * @param offreEmploiDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<OffreEmploiDTO> update(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to update OffreEmploi : {}", offreEmploiDTO);
        return offreEmploiRepository.save(offreEmploiMapper.toEntity(offreEmploiDTO)).map(offreEmploiMapper::toDto);
    }

    /**
     * Partially update a offreEmploi.
     *
     * @param offreEmploiDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<OffreEmploiDTO> partialUpdate(OffreEmploiDTO offreEmploiDTO) {
        LOG.debug("Request to partially update OffreEmploi : {}", offreEmploiDTO);

        return offreEmploiRepository
            .findById(offreEmploiDTO.getId())
            .map(existingOffreEmploi -> {
                offreEmploiMapper.partialUpdate(existingOffreEmploi, offreEmploiDTO);

                return existingOffreEmploi;
            })
            .flatMap(offreEmploiRepository::save)
            .map(offreEmploiMapper::toDto);
    }

    /**
     * Get all the offreEmplois.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<OffreEmploiDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all OffreEmplois");
        return offreEmploiRepository.findAllBy(pageable).map(offreEmploiMapper::toDto);
    }

    /**
     * Returns the number of offreEmplois available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return offreEmploiRepository.count();
    }

    /**
     * Get one offreEmploi by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<OffreEmploiDTO> findOne(Long id) {
        LOG.debug("Request to get OffreEmploi : {}", id);
        return offreEmploiRepository.findById(id).map(offreEmploiMapper::toDto);
    }

    /**
     * Delete the offreEmploi by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete OffreEmploi : {}", id);
        return offreEmploiRepository.deleteById(id);
    }


    public Flux<OffreEmploiDTO> searchOffresMotParMot(
        String keyword,
        Long typeContratId,
        String localisation,
        Double salaireMin
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return offreEmploiRepository
                .findAllByFilters(typeContratId, localisation, salaireMin)
                .map(offreEmploiMapper::toDto);
        }
    
        String[] mots = keyword.trim().toLowerCase().split("\\s+");
    
        return offreEmploiRepository
            .findAllByFilters(typeContratId, localisation, salaireMin)
            .filter(offre -> {
                String contenu = (offre.getTitre() + " " + offre.getDescription()).toLowerCase();
                for (String mot : mots) {
                    if (contenu.contains(mot)) {
                        return true; // On accepte dès qu’un mot correspond
                    }
                }
                return false; // Aucun mot ne correspond
            })            
            .map(offreEmploiMapper::toDto);
    }
    
}
