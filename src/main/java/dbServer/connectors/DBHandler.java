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
                    connString = ConfigLoader.getConnectionString(Engine.Firebird) + kvSplit[1];
                    break;
                case "pg":
                    connString = ConfigLoader.getConnectionString(Engine.PostgreSQL) + kvSplit[1];
                    break;
                default:
                    connString = ConfigLoader.getConnectionString(Engine.MongoDB) + kvSplit[1];
                    break;
            }

            databaseUrls.put(kvSplit[1], connString);

        }
    }

    public Map<String, String> getDatabaseUrls() {
        return databaseUrls;
    }

    public void connect(String db) throws Exception {

        Scanner sc = new Scanner(System.in);
        System.out.println("Username:");
        this.user = sc.nextLine();
        System.out.println("Password:");
        this.password = sc.nextLine();

        String dbUrl = databaseUrls.get(db);

        if (dbUrl != null) {
            try {
                conn = DriverManager.getConnection(dbUrl, user, password);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            throw new Exception("Unregistered database " + db);
        }
    }

}
