package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.repository.rowmapper.CandidatureRowMapper;
import com.mycompany.myapp.repository.rowmapper.OffreEmploiRowMapper;
import com.mycompany.myapp.repository.rowmapper.UtilisateurRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Candidature entity.
 */
@SuppressWarnings("unused")
class CandidatureRepositoryInternalImpl extends SimpleR2dbcRepository<Candidature, Long> implements CandidatureRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UtilisateurRowMapper utilisateurMapper;
    private final OffreEmploiRowMapper offreemploiMapper;
    private final CandidatureRowMapper candidatureMapper;

    private static final Table entityTable = Table.aliased("candidature", EntityManager.ENTITY_ALIAS);
    private static final Table candidatTable = Table.aliased("utilisateur", "candidat");
    private static final Table offreEmploiTable = Table.aliased("offre_emploi", "offreEmploi");

    public CandidatureRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UtilisateurRowMapper utilisateurMapper,
        OffreEmploiRowMapper offreemploiMapper,
        CandidatureRowMapper candidatureMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Candidature.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.utilisateurMapper = utilisateurMapper;
        this.offreemploiMapper = offreemploiMapper;
        this.candidatureMapper = candidatureMapper;
    }

    @Override
    public Flux<Candidature> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Candidature> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CandidatureSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UtilisateurSqlHelper.getColumns(candidatTable, "candidat"));
        columns.addAll(OffreEmploiSqlHelper.getColumns(offreEmploiTable, "offreEmploi"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(candidatTable)
            .on(Column.create("candidat_id", entityTable))
            .equals(Column.create("id", candidatTable))
            .leftOuterJoin(offreEmploiTable)
            .on(Column.create("offre_emploi_id", entityTable))
            .equals(Column.create("id", offreEmploiTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Candidature.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Candidature> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Candidature> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Candidature process(Row row, RowMetadata metadata) {
        Candidature entity = candidatureMapper.apply(row, "e");
        entity.setCandidat(utilisateurMapper.apply(row, "candidat"));
        entity.setOffreEmploi(offreemploiMapper.apply(row, "offreEmploi"));
        return entity;
    }

    @Override
    public <S extends Candidature> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
