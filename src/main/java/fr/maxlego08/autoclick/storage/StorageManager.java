package fr.maxlego08.autoclick.storage;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.Session;
import fr.maxlego08.autoclick.migrations.SessionMigration;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.logger.JULogger;
import org.bukkit.Bukkit;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class StorageManager {

    private final ClickPlugin plugin;
    private RequestHelper requestHelper;

    public StorageManager(ClickPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadDatabase() {

        var configuration = new DatabaseConfiguration("zantiautoclick_", "", "", 0, "", "", this.plugin.getConfig().getBoolean("sql-debug"), DatabaseType.SQLITE);
        var logger = JULogger.from(this.plugin.getLogger());
        var database = new SqliteConnection(configuration, this.plugin.getDataFolder());
        if (!database.isValid()) {
            this.plugin.getLogger().severe("Impossible de se connecter à la base de données.");
            return;
        }

        this.requestHelper = new RequestHelper(database, logger);

        MigrationManager.setMigrationTableName("zantiautoclick_migrations");
        MigrationManager.registerMigration(new SessionMigration());

        MigrationManager.execute(database, logger);
    }

    public void insertSession(UUID uuid, Session session) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.requestHelper.insert(Tables.SESSIONS, table -> {
            table.uuid("unique_id", uuid);
            table.string("differences", session.getDifferences().stream().map(String::valueOf).collect(Collectors.joining(",")));
            table.object("started_at", new Date(session.getStartedAt()));
            table.object("finished_at", new Date(session.getFinishedAt()));
        }));
    }

}
