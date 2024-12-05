package configLoader;

import dbServer.engines.Engine;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigLoader {

    private static Properties gralConfigProps = new Properties();
    private static Properties dbProps = new Properties();

    static {
        try (InputStream input = new FileInputStream("src/main/resources/conection/config/config.properties")) {
            gralConfigProps.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config file", e);
        }
    }

    public static String get (String key) {
        return gralConfigProps.getProperty(key);
    }

    public static String get(String key, String configFile_path) {

        try (InputStream input = new FileInputStream(configFile_path)) {
            dbProps.load(input);
            return dbProps.getProperty(key);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config file", e);
        }
    }

    public static List<String> getKeys(String configFile_path) {

        try (InputStream input = new FileInputStream(configFile_path)) {
            dbProps.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config file", e);
        }

        return new ArrayList<>(dbProps.stringPropertyNames());
    }

    public static String getConnectionString(Engine Engine) {

        String engineName = Engine.name();
        String jdbc = get(engineName + ".path");
        String ip = get(engineName + ".ip");
        String port = get(engineName + ".port");

        return (jdbc + ip + ":" + port);
    }
}
