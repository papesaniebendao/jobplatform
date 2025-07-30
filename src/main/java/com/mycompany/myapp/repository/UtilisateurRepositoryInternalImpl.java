package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Utilisateur;
import com.mycompany.myapp.repository.rowmapper.CVRowMapper;
import com.mycompany.myapp.repository.rowmapper.UserRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Utilisateur entity.
 */
@SuppressWarnings("unused")
class UtilisateurRepositoryInternalImpl extends SimpleR2dbcRepository<Utilisateur, Long> implements UtilisateurRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final CVRowMapper cvMapper;
    private final UtilisateurRowMapper utilisateurMapper;

    private static final Table entityTable = Table.aliased("utilisateur", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table cvTable = Table.aliased("cv", "cv");

    public UtilisateurRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        CVRowMapper cvMapper,
        UtilisateurRowMapper utilisateurMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Utilisateur.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.cvMapper = cvMapper;
        this.utilisateurMapper = utilisateurMapper;
    }

    @Override
    public Flux<Utilisateur> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Utilisateur> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = UtilisateurSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(CVSqlHelper.getColumns(cvTable, "cv"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(cvTable)
            .on(Column.create("cv_id", entityTable))
            .equals(Column.create("id", cvTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Utilisateur.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Utilisateur> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Utilisateur> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Utilisateur> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Utilisateur> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Utilisateur> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Utilisateur process(Row row, RowMetadata metadata) {
        Utilisateur entity = utilisateurMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setCv(cvMapper.apply(row, "cv"));
        return entity;
    }

    @Override
    public <S extends Utilisateur> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
