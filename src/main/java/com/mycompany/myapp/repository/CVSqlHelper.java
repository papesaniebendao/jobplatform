package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CVSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("url_fichier", table, columnPrefix + "_url_fichier"));
        columns.add(Column.aliased("nom_fichier", table, columnPrefix + "_nom_fichier"));
        columns.add(Column.aliased("date_upload", table, columnPrefix + "_date_upload"));

        return columns;
    }
}
