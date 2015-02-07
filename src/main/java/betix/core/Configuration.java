package betix.core;

import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {

    private final static Logger logger = LoggerFactory.getLogger(Configuration.class);
    private final static File CONFIG_FILE = new File("config.yml");

    private final static Map<String, Object> config;

    static {
        Map<String, Object> configTemp;
        try {
            configTemp = Yaml.loadType(CONFIG_FILE, LinkedHashMap.class);
        } catch (Exception e) {
            configTemp = new HashMap<>();
            configTemp.put(ConfigKey.browser.name(), "firefox");
            configTemp.put(ConfigKey.imageDir.name(), "img");
            configTemp.put(ConfigKey.siteName.name(), "bet365.com");
            configTemp.put(ConfigKey.siteUrl.name(), "http://www.bet365.com");
            logger.error("can't load config", e);

            try {
                logger.info("Creating default config");
                Yaml.dump(configTemp, CONFIG_FILE);
            } catch (FileNotFoundException ee) {
                logger.warn("Error saving config", ee);
            }

        }
        config = configTemp;
    }

    public void addConfig(ConfigKey key, Object value) {
        addConfig(key, value, false);
    }

    public void addConfig(ConfigKey key, Object value, boolean persist) {
        config.put(key.name(), value);
        if (persist) {
            saveConfig();
        }
    }

    public Object getConfig(ConfigKey key) {
        return config.get(key.name());
    }

    public String getConfigAsString(ConfigKey key) {
        return (String) config.get(key.name());
    }

    public Integer getConfigAsInteger(ConfigKey key) {
        return (Integer) config.get(key.name());
    }

    public Boolean getConfigAsBoolean(ConfigKey key) {
        return (Boolean) config.get(key.name());
    }

    public void saveConfig() {
        try {
            Yaml.dump(config, CONFIG_FILE);
        } catch (FileNotFoundException e) {
            logger.warn("Error saving config", e);
        }
    }
}
