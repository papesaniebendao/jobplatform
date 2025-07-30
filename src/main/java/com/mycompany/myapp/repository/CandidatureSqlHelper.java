package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CandidatureSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("date_postulation", table, columnPrefix + "_date_postulation"));
        columns.add(Column.aliased("statut", table, columnPrefix + "_statut"));

        columns.add(Column.aliased("candidat_id", table, columnPrefix + "_candidat_id"));
        columns.add(Column.aliased("offre_emploi_id", table, columnPrefix + "_offre_emploi_id"));
        return columns;
    }
}
