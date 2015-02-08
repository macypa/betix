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
    private final static File CONFIG_SENSITIVE_FILE = new File("config_sensitive.yml");

    private final static Map<String, Object> config;
    private final static Map<String, Object> configSensitive;

    static {
        Map<String, Object> configTemp;
        Map<String, Object> configSensitiveTemp;
        try {
            configTemp = Yaml.loadType(CONFIG_FILE, LinkedHashMap.class);
            configSensitiveTemp = Yaml.loadType(CONFIG_SENSITIVE_FILE, LinkedHashMap.class);
        } catch (Exception e) {
            configTemp = new HashMap<>();
            configSensitiveTemp = new HashMap<>();
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
        configSensitive = configSensitiveTemp;
    }

    public void addConfig(ConfigKey key, Object value) {
        addConfig(key, value, false);
    }

    public void addConfig(ConfigKey key, Object value, boolean persist) {
        addConfig(key, value, persist, false);
    }

    public void addConfig(ConfigKey key, Object value, boolean persist, boolean sensitive) {
        if (sensitive) {
            configSensitive.put(key.name(), value);
        } else {
            config.put(key.name(), value);
        }
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

    public String getConfigAsString(ConfigKey key, boolean sensitive) {
        if (sensitive) {
            return (String) configSensitive.get(key.name());
        }
        return getConfigAsString(key);
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
        try {
            Yaml.dump(configSensitive, CONFIG_SENSITIVE_FILE);
        } catch (FileNotFoundException e) {
            logger.warn("Error saving config", e);
        }
    }
}
