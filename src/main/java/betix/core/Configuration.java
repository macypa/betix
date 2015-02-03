package betix.core;

import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by shlyakls on 2/3/15.
 */
public class Configuration {

    private static Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static File CONFIG_FILE = new File("config.yml");

    private final static Map<String, Object> config;

    static {
        Map<String, Object> configTemp;
        try {
            configTemp = Yaml.loadType(CONFIG_FILE, LinkedHashMap.class);
        } catch (Exception e) {
            configTemp = new HashMap<>();
            logger.error("can't load config", e);
        }
        config = configTemp;
    }

    public static void addConfig(String key, Object value) {
        addConfig(key, value, false);
    }

    public static void addConfig(String key, Object value, boolean persist) {
        config.put(key, value);
        if (persist) {
            saveConfig();
        }
    }

    public static Object getConfig(String key) {
        return config.get(key);
    }

    public static String getConfigAsString(String key) {
        return (String) config.get(key);
    }

    public static Integer getConfigAsInteger(String key) {
        return (Integer) config.get(key);
    }

    public static Boolean getConfigAsBoolean(String key) {
        return (Boolean) config.get(key);
    }

    public static void saveConfig() {
        try {
            Yaml.dump(config, CONFIG_FILE);
        } catch (FileNotFoundException e) {
            logger.warn("Error saving cookies", e);
        }
    }
}
