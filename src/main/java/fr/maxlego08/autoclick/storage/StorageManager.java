package fr.maxlego08.autoclick.storage;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.Session;
import fr.maxlego08.autoclick.api.storage.StorageType;
import fr.maxlego08.autoclick.api.storage.Tables;
import fr.maxlego08.autoclick.migrations.SessionMigration;
import fr.maxlego08.autoclick.api.storage.dto.SessionDTO;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.HikariDatabaseConnection;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.logger.JULogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StorageManager {

    private final ClickPlugin plugin;
    private RequestHelper requestHelper;

    public StorageManager(ClickPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadDatabase() {

        FileConfiguration configuration = this.plugin.getConfig();
        StorageType storageType = StorageType.valueOf(configuration.getString("storage-type", StorageType.SQLITE.name()).toUpperCase());
        DatabaseConfiguration databaseConfiguration = getDatabaseConfiguration(configuration, storageType);

        DatabaseConnection connection = switch (storageType) {
            case SQLITE -> new SqliteConnection(databaseConfiguration, this.plugin.getDataFolder());
            case HIKARICP, MYSQL -> new HikariDatabaseConnection(databaseConfiguration);
        };
        if (!connection.isValid()) {
            plugin.getLogger().severe("Unable to connect to database!");
            Bukkit.getPluginManager().disablePlugin(plugin);
        } else {
            if (storageType == StorageType.SQLITE) {
                plugin.getLogger().info("The database connection is valid! (SQLITE)");
            } else {
                plugin.getLogger().info("The database connection is valid! (" + connection.getDatabaseConfiguration().getHost() + ")");
            }
        }

        this.requestHelper = new RequestHelper(connection, JULogger.from(plugin.getLogger()));

        MigrationManager.setMigrationTableName("zantiautoclick_migrations");
        MigrationManager.registerMigration(new SessionMigration());

        MigrationManager.execute(connection, JULogger.from(plugin.getLogger()));
    }

    private DatabaseConfiguration getDatabaseConfiguration(FileConfiguration configuration, StorageType storageType) {
        GlobalDatabaseConfiguration globalDatabaseConfiguration = new GlobalDatabaseConfiguration(configuration);
        String tablePrefix = globalDatabaseConfiguration.getTablePrefix();
        String host = globalDatabaseConfiguration.getHost();
        int port = globalDatabaseConfiguration.getPort();
        String user = globalDatabaseConfiguration.getUser();
        String password = globalDatabaseConfiguration.getPassword();
        String database = globalDatabaseConfiguration.getDatabase();
        boolean debug = globalDatabaseConfiguration.isDebug();

        return new DatabaseConfiguration(tablePrefix, user, password, port, host, database, debug, storageType == StorageType.SQLITE ? DatabaseType.SQLITE : DatabaseType.MYSQL);
    }

    public void insertSession(UUID uuid, Session session) {
        this.async(() -> this.requestHelper.insert(Tables.SESSIONS, table -> {
            table.uuid("unique_id", uuid);
            table.string("differences", session.getDifferences().stream().map(String::valueOf).collect(Collectors.joining(",")));
            table.object("started_at", new Date(session.getStartedAt()));
            table.object("finished_at", new Date(session.getFinishedAt()));
        }));
    }

    public void select(int id, Consumer<SessionDTO> consumer) {
        this.async(() -> {
            var values = this.requestHelper.select(Tables.SESSIONS, SessionDTO.class, table -> table.where("id", id));
            consumer.accept(values.isEmpty() ? null : values.getFirst());
        });
    }

    public List<SessionDTO> select() {
        return this.requestHelper.selectAll(Tables.SESSIONS, SessionDTO.class);
    }

    public void clean() {
        this.async(() -> {
            for (SessionDTO value : select()) {
                if (!value.isValid()) {
                    this.requestHelper.delete(Tables.SESSIONS, table -> table.where("id", value.id()));
                }
            }
        });
    }

    private void async(Runnable runnable) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    public void select(Consumer<List<SessionDTO>> consumer) {
        async(() -> consumer.accept(select()));
    }
}
