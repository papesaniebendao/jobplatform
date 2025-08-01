package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.CV;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CV}, with proper type conversions.
 */
@Service
public class CVRowMapper implements BiFunction<Row, String, CV> {

    private final ColumnConverter converter;

    public CVRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CV} stored in the database.
     */
    @Override
    public CV apply(Row row, String prefix) {
        CV entity = new CV();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUrlFichier(converter.fromRow(row, prefix + "_url_fichier", String.class));
        entity.setNomFichier(converter.fromRow(row, prefix + "_nom_fichier", String.class));
        entity.setDateUpload(converter.fromRow(row, prefix + "_date_upload", Instant.class));
        return entity;
    }
}
