package tescha.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "config.json";
    private static Map<String, Object> config = new HashMap<>();

    public static void loadConfig() throws IOException {
        Path configPath = Paths.get(CONFIG_FILE);

        if (Files.exists(configPath)) {
            ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(configPath.toFile(), Map.class);
            logger.info("Configuración cargada desde {}", CONFIG_FILE);
        } else {
            // Configuración por defecto
            config.put("logoPath", "/logo.png");
            config.put("enableNotifications", true);
            config.put("autoLogin", false);
            config.put("app.title", "TESCHA");
            saveConfig();
            logger.info("Archivo de configuración creado con valores por defecto");
        }
    }

    public static void saveConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(CONFIG_FILE), config);
        logger.info("Configuración guardada en {}", CONFIG_FILE);
    }

    public static Object getConfig(String key) {
        return config.get(key);
    }

    public static void setConfig(String key, Object value) throws IOException {
        config.put(key, value);
        saveConfig();
    }

    public static String getStringConfig(String key) {
        return (String) config.get(key);
    }

    public static boolean getBooleanConfig(String key) {
        return Boolean.parseBoolean(String.valueOf(config.get(key)));
    }
}