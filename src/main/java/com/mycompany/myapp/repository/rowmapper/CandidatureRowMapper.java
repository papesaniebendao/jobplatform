package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.domain.enumeration.StatutCandidature;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Candidature}, with proper type conversions.
 */
@Service
public class CandidatureRowMapper implements BiFunction<Row, String, Candidature> {

    private final ColumnConverter converter;

    public CandidatureRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Candidature} stored in the database.
     */
    @Override
    public Candidature apply(Row row, String prefix) {
        Candidature entity = new Candidature();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDatePostulation(converter.fromRow(row, prefix + "_date_postulation", Instant.class));
        entity.setStatut(converter.fromRow(row, prefix + "_statut", StatutCandidature.class));
        entity.setCandidatId(converter.fromRow(row, prefix + "_candidat_id", Long.class));
        entity.setOffreEmploiId(converter.fromRow(row, prefix + "_offre_emploi_id", Long.class));
        return entity;
    }
}
