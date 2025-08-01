package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class OffreEmploiSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("titre", table, columnPrefix + "_titre"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("localisation", table, columnPrefix + "_localisation"));
        columns.add(Column.aliased("salaire", table, columnPrefix + "_salaire"));
        columns.add(Column.aliased("date_publication", table, columnPrefix + "_date_publication"));
        columns.add(Column.aliased("date_expiration", table, columnPrefix + "_date_expiration"));
        columns.add(Column.aliased("is_active", table, columnPrefix + "_is_active"));

        columns.add(Column.aliased("type_contrat_id", table, columnPrefix + "_type_contrat_id"));
        columns.add(Column.aliased("recruteur_id", table, columnPrefix + "_recruteur_id"));
        return columns;
    }
}
