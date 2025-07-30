package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.repository.rowmapper.OffreEmploiRowMapper;
import com.mycompany.myapp.repository.rowmapper.TypeContratRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the OffreEmploi entity.
 */
@SuppressWarnings("unused")
class OffreEmploiRepositoryInternalImpl extends SimpleR2dbcRepository<OffreEmploi, Long> implements OffreEmploiRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TypeContratRowMapper typecontratMapper;
    private final UtilisateurRowMapper utilisateurMapper;
    private final OffreEmploiRowMapper offreemploiMapper;

    private static final Table entityTable = Table.aliased("offre_emploi", EntityManager.ENTITY_ALIAS);
    private static final Table typeContratTable = Table.aliased("type_contrat", "typeContrat");
    private static final Table recruteurTable = Table.aliased("utilisateur", "recruteur");

    public OffreEmploiRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TypeContratRowMapper typecontratMapper,
        UtilisateurRowMapper utilisateurMapper,
        OffreEmploiRowMapper offreemploiMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(OffreEmploi.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.typecontratMapper = typecontratMapper;
        this.utilisateurMapper = utilisateurMapper;
        this.offreemploiMapper = offreemploiMapper;
    }

    @Override
    public Flux<OffreEmploi> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<OffreEmploi> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = OffreEmploiSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TypeContratSqlHelper.getColumns(typeContratTable, "typeContrat"));
        columns.addAll(UtilisateurSqlHelper.getColumns(recruteurTable, "recruteur"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(typeContratTable)
            .on(Column.create("type_contrat_id", entityTable))
            .equals(Column.create("id", typeContratTable))
            .leftOuterJoin(recruteurTable)
            .on(Column.create("recruteur_id", entityTable))
            .equals(Column.create("id", recruteurTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, OffreEmploi.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<OffreEmploi> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<OffreEmploi> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private OffreEmploi process(Row row, RowMetadata metadata) {
        OffreEmploi entity = offreemploiMapper.apply(row, "e");
        entity.setTypeContrat(typecontratMapper.apply(row, "typeContrat"));
        entity.setRecruteur(utilisateurMapper.apply(row, "recruteur"));
        return entity;
    }

    @Override
    public <S extends OffreEmploi> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
