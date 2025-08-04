package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.domain.enumeration.RoleUtilisateur;
import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.repository.UtilisateurRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.OffreEmploiCreateDTO;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.mapper.OffreEmploiMapper;


import com.mycompany.myapp.repository.TypeContratRepository;

import com.mycompany.myapp.service.dto.TypeContratDTO;

import com.mycompany.myapp.domain.TypeContrat;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final UtilisateurRepository utilisateurRepository;

    private final TypeContratRepository typeContratRepository;

    public OffreEmploiService(OffreEmploiRepository offreEmploiRepository, UtilisateurRepository utilisateurRepository, OffreEmploiMapper offreEmploiMapper, TypeContratRepository typeContratRepository) {
        this.offreEmploiRepository = offreEmploiRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.offreEmploiMapper = offreEmploiMapper;
        this.typeContratRepository = typeContratRepository;
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


    /*
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
    }*/

    public Flux<OffreEmploiDTO> searchOffresMotParMot(
        String keyword,
        Long typeContratId,
        String localisation,
        Double salaireMin
    ) {
        // Si tous les paramètres sont vides/null, retourner toutes les offres
        if (isAllParametersEmpty(keyword, typeContratId, localisation, salaireMin)) {
            return getAllOffresWithTypeContrat();
        }
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return offreEmploiRepository
                .findAllByFilters(typeContratId, localisation, salaireMin)
                .flatMap(this::mapWithTypeContrat);
        }

        String[] mots = keyword.trim().toLowerCase().split("\\s+");

        return offreEmploiRepository
            .findAllByFilters(typeContratId, localisation, salaireMin)
            .filter(offre -> {
                String contenu = (offre.getTitre() + " " + offre.getDescription()).toLowerCase();
                for (String mot : mots) {
                    if (contenu.contains(mot)) {
                        return true;
                    }
                }
                return false;
            })
            .flatMap(this::mapWithTypeContrat);
    }

    // Nouvelle méthode pour vérifier si tous les paramètres sont vides
    private boolean isAllParametersEmpty(String keyword, Long typeContratId, String localisation, Double salaireMin) {
        return (keyword == null || keyword.trim().isEmpty()) &&
               typeContratId == null &&
               (localisation == null || localisation.trim().isEmpty()) &&
               salaireMin == null;
    }

    private Mono<OffreEmploiDTO> mapWithTypeContrat(com.mycompany.myapp.domain.OffreEmploi offre) {
        return typeContratRepository
            .findById(offre.getTypeContratId())
            .map(typeContrat -> {
                OffreEmploiDTO dto = offreEmploiMapper.toDto(offre);
                TypeContratDTO tcDto = new TypeContratDTO();
                tcDto.setId(typeContrat.getId());
                tcDto.setNom(typeContrat.getNom());
                dto.setTypeContrat(tcDto);
                return dto;
            })
            .switchIfEmpty(Mono.just(offreEmploiMapper.toDto(offre)));
    }
    
    public Mono<OffreEmploiDTO> createOffrePourRecruteur(OffreEmploiCreateDTO dto) {
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non connecté")))
            .flatMap(login -> utilisateurRepository.findByUserLogin(login))
            .switchIfEmpty(Mono.error(new RuntimeException("Recruteur introuvable")))
            .flatMap(recruteur -> {
                OffreEmploi entity = new OffreEmploi();
                entity.setTitre(dto.getTitre());
                entity.setDescription(dto.getDescription());
                entity.setLocalisation(dto.getLocalisation());
                entity.setSalaire(dto.getSalaire());
                entity.setDateExpiration(dto.getDateExpiration());
                entity.setDatePublication(Instant.now());  // automatique
                entity.setIsActive(true);                  // automatique
                entity.setTypeContratId(dto.getTypeContratId());
                entity.setRecruteurId(recruteur.getId());
                return offreEmploiRepository.save(entity);
            })
            .map(offreEmploiMapper::toDto);
    }
    

    public Flux<OffreEmploiDTO> findAllForCurrentRecruteur() {
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non connecté")))
            .flatMap(utilisateurRepository::findByUserLogin)
            .switchIfEmpty(Mono.error(new RuntimeException("Recruteur introuvable")))
            .flatMapMany(recruteur -> 
                offreEmploiRepository.findByRecruteur(recruteur.getId())
            )
            .map(offreEmploiMapper::toDto);
    }

    

    
}
