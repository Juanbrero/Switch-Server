package dbServer.connectors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

public class SQLHandler extends DBHandler{

    public SQLHandler() {
        super();
    }

    public JSONObject listTables(String dbName) throws Exception {

        JSONObject result = new JSONObject();
        JSONArray rows = new JSONArray();

        Statement stmt = conn.createStatement();

        try (ResultSet rs = stmt.executeQuery("SELECT RDB$RELATION_NAME FROM RDB$RELATIONS WHERE RDB$SYSTEM_FLAG = 0")) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                JSONObject row = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                rows.put(row);
            }
        }

        result.put("data", rows);
        return result;
    }

    public JSONObject executeQuery(String dbName, String query) throws Exception {
        String dbUrl = databaseUrls.get(dbName);
        if (dbUrl == null) throw new Exception("Base de datos no registrada: " + dbName);

        JSONObject result = new JSONObject();
        JSONArray rows = new JSONArray();

        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             Statement stmt = conn.createStatement()) {

            // Comprobar si el query es de tipo SELECT
            boolean isSelect = query.trim().toUpperCase().startsWith("SELECT");

            if (isSelect) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (rs.next()) {
                        JSONObject row = new JSONObject();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        rows.put(row);
                    }
                }
                result.put("data", rows);
            } else {
                // Ejecutar sentencias DDL o DML (INSERT, UPDATE, DELETE) con executeUpdate()
                int affectedRows = stmt.executeUpdate(query);
                result.put("affectedRows", affectedRows);
            }
        }

        System.out.println(result);
        return result;
    }
}
