package dbServer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLHandler {

    private String user;
    private String password;
    private Map<String, String> databaseUrls = new HashMap<>();
    private Connection conn;

    public SQLHandler(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public void registerDatabase(String dbName, String dbUrl) {
        databaseUrls.put(dbName, dbUrl);
    }

    public Map<String, String> getDatabaseUrls() {
        return databaseUrls;
    }

    public void connect(String db) throws Exception {
        String dbUrl = databaseUrls.get(db);

        if (dbUrl != null) {
            try {
                conn = DriverManager.getConnection(dbUrl, user, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            throw new Exception("Unregistered database " + db);
        }
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
