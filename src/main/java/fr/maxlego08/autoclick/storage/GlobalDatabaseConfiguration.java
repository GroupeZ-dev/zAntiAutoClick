package fr.maxlego08.autoclick.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class GlobalDatabaseConfiguration {

    private static final String CONFIG_PREFIX = "database-configuration.";

    private final FileConfiguration pluginConfiguration;
    private final FileConfiguration globalConfiguration;

    public GlobalDatabaseConfiguration(FileConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
        File file = new File("database-configuration.yml");
        if (file.exists()) {
            this.globalConfiguration = YamlConfiguration.loadConfiguration(file);
        } else {
            this.globalConfiguration = null;
        }
    }

    private String getString(String key, String defaultValue) {
        String fullKey = CONFIG_PREFIX + key;
        if (this.globalConfiguration != null) {
            return this.globalConfiguration.getString(fullKey,
                    this.pluginConfiguration.getString(fullKey, defaultValue));
        }
        return this.pluginConfiguration.getString(fullKey, defaultValue);
    }

    private int getInt(String key, int defaultValue) {
        String fullKey = CONFIG_PREFIX + key;
        if (this.globalConfiguration != null) {
            return this.globalConfiguration.getInt(fullKey,
                    this.pluginConfiguration.getInt(fullKey, defaultValue));
        }
        return this.pluginConfiguration.getInt(fullKey, defaultValue);
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        String fullKey = CONFIG_PREFIX + key;
        if (this.globalConfiguration != null) {
            return this.globalConfiguration.getBoolean(fullKey,
                    this.pluginConfiguration.getBoolean(fullKey, defaultValue));
        }
        return this.pluginConfiguration.getBoolean(fullKey, defaultValue);
    }

    /**
     * Retrieves the database host from the configuration.
     *
     * @return the database host
     */
    public String getHost() {
        return getString("host", "192.168.10.10");
    }

    /**
     * Retrieves the database port from the configuration.
     *
     * @return the database port
     */
    public int getPort() {
        return getInt("port", 3306);
    }

    /**
     * Retrieves the database name from the configuration.
     *
     * @return the name of the database
     */
    public String getDatabase() {
        return getString("database", "homestead");
    }

    /**
     * Retrieves the database user from the configuration.
     *
     * @return the database user
     */
    public String getUser() {
        return getString("user", "homestead");
    }

    /**
     * Retrieves the database password from the configuration.
     *
     * @return the database password
     */
    public String getPassword() {
        return getString("password", "secret");
    }

    /**
     * Retrieves the table prefix from the database configuration.
     *
     * @return the table prefix for database tables
     */
    public String getTablePrefix() {
        return getString("table-prefix", "groupez_");
    }

    /**
     * Determines if debugging is enabled in the database configuration.
     *
     * @return true if debugging is enabled, false otherwise
     */
    public boolean isDebug() {
        return getBoolean("debug", false);
    }
}
