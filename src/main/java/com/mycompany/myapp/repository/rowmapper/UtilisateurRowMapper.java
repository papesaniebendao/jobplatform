package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Utilisateur;
import com.mycompany.myapp.domain.enumeration.RoleUtilisateur;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Utilisateur}, with proper type conversions.
 */
@Service
public class UtilisateurRowMapper implements BiFunction<Row, String, Utilisateur> {

    private final ColumnConverter converter;

    public UtilisateurRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Utilisateur} stored in the database.
     */
    @Override
    public Utilisateur apply(Row row, String prefix) {
        Utilisateur entity = new Utilisateur();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPrenom(converter.fromRow(row, prefix + "_prenom", String.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        entity.setNomEntreprise(converter.fromRow(row, prefix + "_nom_entreprise", String.class));
        entity.setSecteurActivite(converter.fromRow(row, prefix + "_secteur_activite", String.class));
        entity.setTelephone(converter.fromRow(row, prefix + "_telephone", String.class));
        entity.setRole(converter.fromRow(row, prefix + "_role", RoleUtilisateur.class));
        entity.setIsActive(converter.fromRow(row, prefix + "_is_active", Boolean.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setCvId(converter.fromRow(row, prefix + "_cv_id", Long.class));
        return entity;
    }
}
