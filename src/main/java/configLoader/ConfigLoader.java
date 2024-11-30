package configLoader;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("File config.properties not found.");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading config file", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
