package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.TypeContrat;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link TypeContrat}, with proper type conversions.
 */
@Service
public class TypeContratRowMapper implements BiFunction<Row, String, TypeContrat> {

    private final ColumnConverter converter;

    public TypeContratRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link TypeContrat} stored in the database.
     */
    @Override
    public TypeContrat apply(Row row, String prefix) {
        TypeContrat entity = new TypeContrat();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        return entity;
    }
}
