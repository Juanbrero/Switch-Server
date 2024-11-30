package configLoader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

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
}
