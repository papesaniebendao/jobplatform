package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CV;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the CV entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CVRepository extends ReactiveCrudRepository<CV, Long>, CVRepositoryInternal {
    @Query("SELECT * FROM cv entity WHERE entity.id not in (select utilisateur_id from utilisateur)")
    Flux<CV> findAllWhereUtilisateurIsNull();

    @Override
    <S extends CV> Mono<S> save(S entity);

    @Override
    Flux<CV> findAll();

    @Override
    Mono<CV> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CVRepositoryInternal {
    <S extends CV> Mono<S> save(S entity);

    Flux<CV> findAllBy(Pageable pageable);

    Flux<CV> findAll();

    Mono<CV> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<CV> findAllBy(Pageable pageable, Criteria criteria);
}
