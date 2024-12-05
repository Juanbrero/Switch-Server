package dbServer.connectors;

import configLoader.ConfigLoader;
import dbServer.engines.Engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DBHandler {
    protected String user;
    protected String password;
    protected Map<String, String> databaseUrls = new HashMap<>();
    protected Connection conn;

    public DBHandler() {

    }

    public void registerDatabases() {

        List<String> keyValueDb = ConfigLoader.getKeys(ConfigLoader.get("dbConfigFile.path"));

        for (String kv : keyValueDb) {
            String[] kvSplit = kv.split("\\.");

            String connString = "";
            switch (kvSplit[0]) {
                case "fdb":
                    connString = ConfigLoader.getConnectionString(Engine.Firebird) + "/"+ ConfigLoader.get(kv);
                    break;
                case "pg":
                    connString = ConfigLoader.getConnectionString(Engine.PostgreSQL) + "/"+ ConfigLoader.get(kv);
                    break;
                default:
                    connString = ConfigLoader.getConnectionString(Engine.MongoDB) + "/"+ ConfigLoader.get(kv);
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

}
