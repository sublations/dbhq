package dbhq.bot.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * A class for managing application configuration, including secrets and other configuration data.
 */
public class Config {
    private static final Logger logger = LogManager.getLogger(Config.class);

    private Map<String, Object> configData = new HashMap<>();

    /**
     * Sets the entire configuration data map.
     *
     * @param configData A map containing the configuration data.
     */
    public void setConfigData(Map<String, Object> configData) {
        this.configData = configData;
    }

    /**
     * Retrieves a generic configuration value based on a hierarchical key, separated by dots.
     *
     * @param keyPath The hierarchical key to the desired value.
     * @return The value as an Object, or null if not found.
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfigValue(String keyPath) {
        try {
            String[] keys = keyPath.split("\\.");
            Object current = configData;
            for (String key : keys) {
                current = ((Map<String, Object>) current).get(key);
                if (current == null) {
                    logger.warn("Configuration key not found: {}", keyPath);
                    return null;
                }
            }
            return (T) current;
        } catch (ClassCastException e) {
            logger.error("Incorrect type access for configuration key: {}", keyPath, e);
            return null;
        } catch (Exception e) {
            logger.error("Error accessing configuration for key: {}", keyPath, e);
            return null;
        }
    }
}
