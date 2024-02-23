package dbhq.bot.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE_PATH = "config.yml";
    private static final Map<String, Object> configMap = new HashMap<>();

    static {
        loadConfig();
    }

    private static void loadConfig() {
        JsonFactory factory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(factory);

        try (JsonParser parser = factory.createParser(new File(CONFIG_FILE_PATH))) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw new IOException("Expected data to start with an Object");
            }

            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.getCurrentName();
                parser.nextToken(); // Move to value
                Object fieldValue = mapper.readValue(parser, Object.class);
                configMap.put(fieldName, fieldValue);
            }
            logger.info("Configuration successfully loaded.");
        } catch (Exception e) {
            logger.error("Failed to load configuration: {}", e.getMessage(), e);
            // Consider re-throwing or handling to ensure the application can react appropriately
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getConfigValue(String keyPath) {
        String[] keys = keyPath.split("\\.");
        Object current = configMap;

        for (String key : keys) {
            if (!(current instanceof Map<?, ?>)) {
                logger.warn("Attempted to access a configuration with an unsupported key structure: {}", keyPath);
                throw new IllegalArgumentException("Configuration does not support the provided key structure: " + keyPath);
            }
            current = ((Map<String, Object>) current).get(key);
            if (current == null) {
                logger.warn("Configuration key not found: {}", keyPath);
                throw new IllegalArgumentException("Configuration key not found: " + keyPath);
            }
        }

        try {
            return (T) current;
        } catch (ClassCastException e) {
            logger.error("Failed to cast configuration value to the expected type for key: {}", keyPath, e);
            throw new IllegalArgumentException("Failed to cast configuration value to the expected type: " + keyPath, e);
        }
    }
}
