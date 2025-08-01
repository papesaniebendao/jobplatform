package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.service.dto.MyCandidatureDTO;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Candidature entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CandidatureRepository extends ReactiveCrudRepository<Candidature, Long>, CandidatureRepositoryInternal {
    Flux<Candidature> findAllBy(Pageable pageable);

    @Query("SELECT * FROM candidature entity WHERE entity.candidat_id = :id")
    Flux<Candidature> findByCandidat(Long id);

    @Query("SELECT * FROM candidature entity WHERE entity.candidat_id IS NULL")
    Flux<Candidature> findAllWhereCandidatIsNull();

    @Query("SELECT * FROM candidature entity WHERE entity.offre_emploi_id = :id")
    Flux<Candidature> findByOffreEmploi(Long id);

    @Query("SELECT * FROM candidature entity WHERE entity.offre_emploi_id IS NULL")
    Flux<Candidature> findAllWhereOffreEmploiIsNull();

    @Override
    <S extends Candidature> Mono<S> save(S entity);

    @Query("SELECT * FROM candidature c WHERE c.candidat_id = :candidatId AND c.offre_emploi_id = :offreId")
    Mono<Candidature> findByCandidatIdAndOffreEmploiId(Long candidatId, Long offreId);

    @Query("""
    SELECT 
        c.id AS candidature_id,
        o.titre AS titre_offre,
        o.description AS description_offre,
        t.nom AS type_contrat,
        c.statut AS statut
    FROM candidature c
    JOIN offre_emploi o ON c.offre_emploi_id = o.id
    LEFT JOIN type_contrat t ON o.type_contrat_id = t.id
    JOIN utilisateur u ON u.id = c.candidat_id
    JOIN jhi_user ju ON u.user_id = ju.id
    WHERE ju.login = :login
        """)
    Flux<MyCandidatureDTO> findMyCandidatures(@Param("login") String login);


    @Override       
    Flux<Candidature> findAll();

    @Override
    Mono<Candidature> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CandidatureRepositoryInternal {
    <S extends Candidature> Mono<S> save(S entity);

    Flux<Candidature> findAllBy(Pageable pageable);

    Flux<Candidature> findAll();

    Mono<Candidature> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Candidature> findAllBy(Pageable pageable, Criteria criteria);
}
