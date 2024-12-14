package dbServer.connectors;

import configLoader.ConfigLoader;
import dbServer.engines.Engine;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DBHandler {
    protected String user;
    protected String password;
    protected Map<String, String> databaseUrls = new HashMap<>();
    protected Map<String, Engine> dbEngine = new HashMap<>();
    protected Connection conn;

    public DBHandler() {

    }

    public void registerDatabases() {

        String dbConfigFilePath = ConfigLoader.get("dbConfigFile.path");
        List<String> keyValueDb = ConfigLoader.getKeys(dbConfigFilePath);

        for (String kv : keyValueDb) {
            String[] kvSplit = kv.split("\\.");

            String connString = "";

            switch (kvSplit[0]) {
                case "fdb":
                    connString = ConfigLoader.getConnectionString(Engine.Firebird) + ConfigLoader.get(kv, dbConfigFilePath);
                    dbEngine.put(kvSplit[1], Engine.Firebird);
                    break;
                case "pg":
                    connString = ConfigLoader.getConnectionString(Engine.PostgreSQL) + ConfigLoader.get(kv, dbConfigFilePath);
                    dbEngine.put(kvSplit[1], Engine.PostgreSQL);
                    break;
                default:
                    connString = ConfigLoader.getConnectionString(Engine.MongoDB) + ConfigLoader.get(kv, dbConfigFilePath);
                    dbEngine.put(kvSplit[1], Engine.MongoDB);
                    break;
            }

            databaseUrls.put(kvSplit[1], connString);

        }
    }

    public Map<String, String> getDatabases() {
        return databaseUrls;
    }

    public String connect(String db, String user, String password) throws Exception {

        String response;
        this.user = user;
        this.password = password;

        String dbUrl = databaseUrls.get(db);

        if (dbUrl != null) {
            try {
                conn = DriverManager.getConnection(dbUrl, user, password);
                response = "Connection successfully.";
            } catch (Exception e) {
                response = "Connection error";
                throw new RuntimeException(e);
            }
        }
        else {
            response = "Unregistered database";
        }

        return response;
    }

    public JSONObject getTables(String dbName) throws Exception {
        String dbUrl = databaseUrls.get(dbName);
        if (dbUrl != null){

            JSONObject result = new JSONObject();
            JSONArray rows = new JSONArray();

            Statement stmt = conn.createStatement();
            String query = "";

            if (dbEngine.get(dbName).equals(Engine.Firebird)) {
                query = "SELECT RDB$RELATION_NAME FROM RDB$RELATIONS WHERE RDB$SYSTEM_FLAG = 0";
            }
            else if (dbEngine.get(dbName).equals(Engine.PostgreSQL)) {
                query = "SELECT * FROM information_schema.tables WHERE table_schema = 'public'";
            }
//            else {
//                  mongo
//            }


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
            return result;
        }
        else {
            throw new Exception("Database " + dbName + " no registered.");
        }
    }

}
