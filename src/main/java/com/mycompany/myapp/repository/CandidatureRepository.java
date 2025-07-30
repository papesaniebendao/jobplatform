package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Candidature;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
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

    @Override
    Flux<Candidature> findAll();

    Mono<Candidature> findByOffreEmploiIdAndCandidatId(Long offreEmploiId, Long candidatId);  

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

}

