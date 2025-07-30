package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TypeContrat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the TypeContrat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeContratRepository extends ReactiveCrudRepository<TypeContrat, Long>, TypeContratRepositoryInternal {
    @Override
    <S extends TypeContrat> Mono<S> save(S entity);

    @Override
    Flux<TypeContrat> findAll();

    @Override
    Mono<TypeContrat> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface TypeContratRepositoryInternal {
    <S extends TypeContrat> Mono<S> save(S entity);

    Flux<TypeContrat> findAllBy(Pageable pageable);

    Flux<TypeContrat> findAll();

    Mono<TypeContrat> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<TypeContrat> findAllBy(Pageable pageable, Criteria criteria);
}
