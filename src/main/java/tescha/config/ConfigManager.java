package tescha.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;

public class ConfigManager {
    private static Config config;

    public static void loadConfig() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("config.json")));
            Gson gson = new Gson();
            config = gson.fromJson(content, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
            // Configuración por defecto
            config = new Config();
        }
    }

    public static Config getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    public static class Config {
        public DatabaseConfig database;
        public AppConfig app;

        public Config() {
            this.database = new DatabaseConfig();
            this.app = new AppConfig();
        }
    }

    public static class DatabaseConfig {
        public String path = "data/tescha.db";
        public boolean create_tables = true;
    }

    public static class AppConfig {
        public String title = "TESCHA - Gestión de Inventario";
        public String theme = "light";
        public String language = "es";
        public BackupConfig backup = new BackupConfig();
    }

    public static class BackupConfig {
        public boolean enabled = true;
        public String interval = "daily";
        public String path = "backups/";
    }
}