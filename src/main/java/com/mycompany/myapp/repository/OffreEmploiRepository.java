package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OffreEmploi;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the OffreEmploi entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OffreEmploiRepository extends ReactiveCrudRepository<OffreEmploi, Long>, OffreEmploiRepositoryInternal {
    Flux<OffreEmploi> findAllBy(Pageable pageable);

    @Query("SELECT * FROM offre_emploi entity WHERE entity.type_contrat_id = :id")
    Flux<OffreEmploi> findByTypeContrat(Long id);

    @Query("SELECT * FROM offre_emploi entity WHERE entity.type_contrat_id IS NULL")
    Flux<OffreEmploi> findAllWhereTypeContratIsNull();

    @Query("SELECT * FROM offre_emploi entity WHERE entity.recruteur_id = :id")
    Flux<OffreEmploi> findByRecruteur(Long id);

    @Query("SELECT * FROM offre_emploi entity WHERE entity.recruteur_id IS NULL")
    Flux<OffreEmploi> findAllWhereRecruteurIsNull();

    @Override
    <S extends OffreEmploi> Mono<S> save(S entity);

    @Override
    Flux<OffreEmploi> findAll();

    @Override
    Mono<OffreEmploi> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface OffreEmploiRepositoryInternal {
    <S extends OffreEmploi> Mono<S> save(S entity);

    Flux<OffreEmploi> findAllBy(Pageable pageable);

    Flux<OffreEmploi> findAll();

    Mono<OffreEmploi> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<OffreEmploi> findAllBy(Pageable pageable, Criteria criteria);
}
