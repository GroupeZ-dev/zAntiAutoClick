package fr.maxlego08.autoclick.storage;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.Session;
import fr.maxlego08.autoclick.migrations.SessionMigration;
import fr.maxlego08.autoclick.storage.dto.SessionDTO;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.MigrationManager;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.logger.JULogger;

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
            var values = select();
            System.out.println(values.size() + " - " + values.getFirst());
            for (SessionDTO value : values) {
                if (!value.isValid()) {
                    System.out.println("Pas valid : " + value.id() + " -> " + value);
                    this.requestHelper.delete(Tables.SESSIONS, table -> table.where("id", value.id()));
                }
            }
        });
    }

    private void async(Runnable runnable) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }
}
