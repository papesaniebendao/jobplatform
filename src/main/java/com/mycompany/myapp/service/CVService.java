package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.CV;
import com.mycompany.myapp.repository.CVRepository;
import com.mycompany.myapp.repository.UtilisateurRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.dto.CVDTO;
import com.mycompany.myapp.service.mapper.CVMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

import com.mycompany.myapp.config.ApplicationProperties;


/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.CV}.
 */
@Service
@Transactional
public class CVService {

    private static final Logger LOG = LoggerFactory.getLogger(CVService.class);

    private final CVRepository cvRepository;
    private final CVMapper cvMapper;
    private final UtilisateurRepository utilisateurRepository;

    @Value("${application.file-storage.cv-dir:uploads/cv}")
    private String cvDir;

    public CVService(CVRepository cvRepository, CVMapper cvMapper, UtilisateurRepository utilisateurRepository) {
        this.cvRepository = cvRepository;
        this.cvMapper = cvMapper;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Mono<CVDTO> save(CVDTO cvDTO) {
        LOG.debug("Request to save CV : {}", cvDTO);
        return cvRepository.save(cvMapper.toEntity(cvDTO)).map(cvMapper::toDto);
    }

    public Mono<CVDTO> update(CVDTO cvDTO) {
        LOG.debug("Request to update CV : {}", cvDTO);
        return cvRepository.save(cvMapper.toEntity(cvDTO)).map(cvMapper::toDto);
    }

    public Mono<CVDTO> partialUpdate(CVDTO cvDTO) {
        LOG.debug("Request to partially update CV : {}", cvDTO);
        return cvRepository.findById(cvDTO.getId())
            .map(existingCV -> {
                cvMapper.partialUpdate(existingCV, cvDTO);
                return existingCV;
            })
            .flatMap(cvRepository::save)
            .map(cvMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<CVDTO> findAll() {
        LOG.debug("Request to get all CVS");
        return cvRepository.findAll().map(cvMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Flux<CVDTO> findAllWhereUtilisateurIsNull() {
        LOG.debug("Request to get all CVS where utilisateur is null");
        return cvRepository.findAllWhereUtilisateurIsNull().map(cvMapper::toDto);
    }

    public Mono<Long> countAll() {
        return cvRepository.count();
    }

    @Transactional(readOnly = true)
    public Mono<CVDTO> findOne(Long id) {
        LOG.debug("Request to get CV : {}", id);
        return cvRepository.findById(id).map(cvMapper::toDto);
    }

    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete CV : {}", id);
        return cvRepository.deleteById(id);
    }

    /**
     * Upload du CV et association avec l'utilisateur connecté.
     */
    public Mono<CV> uploadCv(FilePart filePart) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(utilisateurRepository::findByUserLogin)
            .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non trouvé")))
            .flatMap(utilisateur -> {
                String uniqueFileName = "cv_" + utilisateur.getId() + "_" + UUID.randomUUID() + "-" + filePart.filename();
                Path targetPath = Path.of(cvDir, uniqueFileName);

                return Mono.fromCallable(() -> {
                        Files.createDirectories(targetPath.getParent());
                        return targetPath;
                    })
                    .flatMap(path -> filePart.transferTo(path).thenReturn(path))
                    .flatMap(path -> {
                        // Supprimer l’ancien CV s'il existe
                        Mono<Void> deleteOld = utilisateur.getCv() != null
                            ? cvRepository.deleteById(utilisateur.getCv().getId())
                            : Mono.empty();

                        // Créer un nouvel objet CV
                        CV newCv = new CV();
                        newCv.setNomFichier(filePart.filename());
                        newCv.setUrlFichier(path.toString());
                        newCv.setDateUpload(Instant.now());

                        return deleteOld
                            .then(cvRepository.save(newCv))
                            .flatMap(savedCv -> {
                                utilisateur.setCv(savedCv);
                                return utilisateurRepository.save(utilisateur).thenReturn(savedCv);
                            });
                    });
            });
    }
}
