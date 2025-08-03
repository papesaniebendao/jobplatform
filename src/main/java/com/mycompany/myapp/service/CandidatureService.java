package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import com.mycompany.myapp.repository.CandidatureRepository;
import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.repository.UtilisateurRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.CandidatureDTO;
import com.mycompany.myapp.service.dto.CandidatureRecueDTO;
import com.mycompany.myapp.service.dto.MyCandidatureDTO;
import com.mycompany.myapp.service.dto.PostulerDTO;
import com.mycompany.myapp.service.mapper.CandidatureMapper;

import java.time.Instant;

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

    private final UtilisateurRepository utilisateurRepository;
    private final OffreEmploiRepository offreEmploiRepository;

    public CandidatureService(CandidatureRepository candidatureRepository, CandidatureMapper candidatureMapper, UtilisateurRepository utilisateurRepository,
    OffreEmploiRepository offreEmploiRepository) {
        this.candidatureRepository = candidatureRepository;
        this.candidatureMapper = candidatureMapper;
        this.utilisateurRepository = utilisateurRepository;
        this.offreEmploiRepository = offreEmploiRepository;
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

    public Mono<Candidature> postuler(Long offreId) {
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non connecté")))
            .flatMap(login -> utilisateurRepository.findByUserLogin(login)
                .switchIfEmpty(Mono.error(new RuntimeException("Candidat introuvable")))
                .flatMap(candidat ->
                    candidatureRepository.findByCandidatIdAndOffreEmploiId(candidat.getId(), offreId)
                        .hasElement()
                        .flatMap(dejaPostule -> {
                            if (dejaPostule) {
                                return Mono.error(new RuntimeException("Vous avez déjà postulé à cette offre"));
                            }
                            return offreEmploiRepository.findById(offreId)
                                .switchIfEmpty(Mono.error(new RuntimeException("Offre introuvable")))
                                .flatMap(offre -> {
                                    Candidature candidature = new Candidature();
                                    candidature.setDatePostulation(Instant.now());
                                    candidature.setStatut(StatutCandidature.EN_ATTENTE);
                                    candidature.setCandidat(candidat);
                                    candidature.setOffreEmploi(offre);
                                    return candidatureRepository.save(candidature);
                                });
                        })
                )
            );
    }

    
    public Flux<MyCandidatureDTO> findMyCandidatures(String login) {
        return candidatureRepository.findMyCandidatures(login);
    }


    public Flux<CandidatureRecueDTO> findAllReceivedByCurrentRecruteur() {
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non connecté")))
            .flatMap(utilisateurRepository::findByUserLogin)
            .switchIfEmpty(Mono.error(new RuntimeException("Recruteur introuvable")))
            .flatMapMany(recruteur -> candidatureRepository.findAllByRecruteurId(recruteur.getId()))
            .flatMap(this::mapToRecueDTO);
    }
    
    private Mono<CandidatureRecueDTO> mapToRecueDTO(Candidature candidature) {
        return utilisateurRepository.findById(candidature.getCandidatId())
            .zipWith(offreEmploiRepository.findById(candidature.getOffreEmploiId()))
            .map(tuple -> {
                var candidat = tuple.getT1();
                var offre = tuple.getT2();
    
                CandidatureRecueDTO dto = new CandidatureRecueDTO();
                dto.setCandidatureId(candidature.getId());
                dto.setDatePostulation(candidature.getDatePostulation());
                dto.setStatut(candidature.getStatut().name());
                // --- NOM COMPLET ---
                String nomComplet =
                    (candidat.getPrenom() != null ? candidat.getPrenom() + " " : "") +
                    (candidat.getNom() != null ? candidat.getNom() : "");
                dto.setCandidatNom(nomComplet.trim());
                dto.setCandidatId(candidat.getId());
          
                dto.setCandidatTelephone(candidat.getTelephone());
                dto.setCandidatEmail(candidat.getUser() != null ? candidat.getUser().getEmail() : null);
                dto.setCvUrl(candidat.getCv() != null ? candidat.getCv().getUrlFichier() : null);
    
                dto.setOffreId(offre.getId());
                dto.setOffreTitre(offre.getTitre());
    
                return dto;
            });
    }

    /*
    public Mono<CandidatureDTO> accepterCandidature(Long candidatureId) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(utilisateurRepository::findByUserLogin)
            .switchIfEmpty(Mono.error(new RuntimeException("Recruteur non trouvé")))
            .flatMap(recruteur ->
                candidatureRepository.findById(candidatureId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Candidature introuvable")))
                    .flatMap(candidature -> 
                        offreEmploiRepository.findById(candidature.getOffreEmploiId())
                            .flatMap(offre -> {
                                if (!offre.getRecruteurId().equals(recruteur.getId())) {
                                    return Mono.error(new RuntimeException("Non autorisé"));
                                }
                                candidature.setStatut(StatutCandidature.ACCEPTEE);
                                return candidatureRepository.save(candidature);
                            })
                    )
            )
            .map(candidatureMapper::toDto);
    }
             */

    @Transactional
    public Mono<CandidatureDTO> changerStatut(Long candidatureId, StatutCandidature statut) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(utilisateurRepository::findByUserLogin)
            .switchIfEmpty(Mono.error(new RuntimeException("Recruteur non trouvé")))
            .flatMap(recruteur ->
                candidatureRepository.findById(candidatureId)
                    .switchIfEmpty(Mono.error(new RuntimeException("Candidature introuvable")))
                    .flatMap(candidature ->
                        offreEmploiRepository.findById(candidature.getOffreEmploiId())
                            .flatMap(offre -> {
                                if (!offre.getRecruteurId().equals(recruteur.getId())) {
                                    return Mono.error(new RuntimeException("Non autorisé"));
                                }
                                candidature.setStatut(statut);
                                return candidatureRepository.save(candidature);
                            })
                    )
            )
            .map(candidatureMapper::toDto);
    }
             

}
