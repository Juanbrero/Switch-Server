package configLoader;

import dbServer.engines.Engine;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

public class ConfigLoader {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/main/resources/conection/config.properties")) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config file", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String getConnectionString(Engine engine) {

        String engineName = engine.name();
        String jdbc = get(engineName + ".path");
        String ip = get(engineName + ".ip");
        String port = get(engineName + ".port");

        return (jdbc + ip + ":" + port + "/");
    }
}
