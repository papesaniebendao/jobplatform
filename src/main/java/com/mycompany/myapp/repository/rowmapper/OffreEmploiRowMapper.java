package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.OffreEmploi;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OffreEmploi}, with proper type conversions.
 */
@Service
public class OffreEmploiRowMapper implements BiFunction<Row, String, OffreEmploi> {

    private final ColumnConverter converter;

    public OffreEmploiRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OffreEmploi} stored in the database.
     */
    @Override
    public OffreEmploi apply(Row row, String prefix) {
        OffreEmploi entity = new OffreEmploi();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitre(converter.fromRow(row, prefix + "_titre", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setLocalisation(converter.fromRow(row, prefix + "_localisation", String.class));
        entity.setSalaire(converter.fromRow(row, prefix + "_salaire", Double.class));
        entity.setDatePublication(converter.fromRow(row, prefix + "_date_publication", Instant.class));
        entity.setDateExpiration(converter.fromRow(row, prefix + "_date_expiration", Instant.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setTypeContratId(converter.fromRow(row, prefix + "_type_contrat_id", Long.class));
        entity.setRecruteurId(converter.fromRow(row, prefix + "_recruteur_id", Long.class));
        return entity;
    }
}
