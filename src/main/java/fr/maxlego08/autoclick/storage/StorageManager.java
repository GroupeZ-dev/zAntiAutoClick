package fr.maxlego08.autoclick.storage;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.Session;
import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.autoclick.api.result.AnalyzeResult;
import fr.maxlego08.autoclick.api.result.SessionResult;
import fr.maxlego08.autoclick.api.storage.StorageType;
import fr.maxlego08.autoclick.api.storage.Tables;
import fr.maxlego08.autoclick.api.storage.dto.InvalidSessionDTO;
import fr.maxlego08.autoclick.api.storage.dto.SessionDTO;
import fr.maxlego08.autoclick.migrations.InvalidSessionMigration;
import fr.maxlego08.autoclick.migrations.SessionMigration;
import fr.maxlego08.autoclick.zcore.utils.PlayerInfo;
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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        MigrationManager.registerMigration(new InvalidSessionMigration());

        MigrationManager.execute(connection, JULogger.from(plugin.getLogger()));
    }

    /**
     * Creates a {@link DatabaseConfiguration} from the {@link FileConfiguration}
     * and {@link StorageType} provided.
     *
     * <p>This method will first attempt to retrieve the configuration values
     * from the global database configuration file, falling back to the plugin
     * configuration if the global configuration is unavailable.
     *
     * @param configuration the plugin configuration
     * @param storageType   the type of storage to use
     * @return a database configuration
     */
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

    /**
     * Inserts a session into the database asynchronously.
     * <p>
     * This method will insert a session into the database with the given id and session data.
     * </p>
     *
     * @param uuid    The id of the session to insert.
     * @param session The session to insert.
     */
    public void insertSession(UUID uuid, Session session, Consumer<Integer> consumer) {
        this.async(() -> this.requestHelper.insert(Tables.SESSIONS, table -> {
            table.uuid("unique_id", uuid);
            table.string("differences", session.getDifferences().stream().map(String::valueOf).collect(Collectors.joining(",")));
            table.object("started_at", new Date(session.getStartedAt()));
            table.object("finished_at", new Date(session.getFinishedAt()));
        }, consumer));
    }

    /**
     * Inserts an invalid session into the database asynchronously.
     * <p>
     * This method will insert an invalid session into the database with the given session id and analysis results.
     * </p>
     *
     * @param session       The session to insert.
     * @param sessionResult The session result of the session.
     * @param analyzeResult The analyze result of the session.
     */
    public void insertInvalidSession(Session session, SessionResult sessionResult, AnalyzeResult analyzeResult, Consumer<InvalidSessionDTO> consumer) {
        this.async(() -> this.requestHelper.insert(Tables.INVALID_SESSIONS, table -> {
            table.bigInt("session_id", session.getId());
            table.decimal("result", analyzeResult.percent());
            table.decimal("average", sessionResult.average());
            table.decimal("median", sessionResult.median());
            table.decimal("standard_deviation", sessionResult.standardDeviation());
        }, id -> consumer.accept(new InvalidSessionDTO(id, session.getId(), analyzeResult.percent(), sessionResult.average(), sessionResult.median(), sessionResult.standardDeviation(), null, null))));
    }

    /**
     * Selects a session from the database asynchronously.
     * <p>
     * This method will execute a consumer upon completion with the selected session, or null if no session was found.
     *
     * @param id       The id of the session to select.
     * @param consumer The consumer to execute upon completion.
     */
    public void select(int id, Consumer<SessionDTO> consumer) {
        this.async(() -> {
            var values = this.requestHelper.select(Tables.SESSIONS, SessionDTO.class, table -> table.where("id", id));
            consumer.accept(values.isEmpty() ? null : values.getFirst());
        });
    }

    /**
     * Selects all sessions from the database.
     * <p>
     * This method returns a list of all sessions in the database.
     * </p>
     *
     * @return A list of all sessions in the database.
     */
    public List<SessionDTO> select() {
        return this.requestHelper.selectAll(Tables.SESSIONS, SessionDTO.class);
    }

    /**
     * Cleans up invalid sessions from the database.
     * <p>
     * This method asynchronously iterates over all sessions and removes any
     * session that is deemed invalid based on its validation criteria.
     * </p>
     */
    public void clean() {
        this.async(() -> {
            for (SessionDTO value : select()) {
                if (!value.isValid()) {
                    this.requestHelper.delete(Tables.SESSIONS, table -> table.where("id", value.id()));
                }
            }
        });
    }

    /**
     * Runs a runnable asynchronously on the server's scheduler.
     * <p>
     * This is a convenience method for running database operations asynchronously.
     * </p>
     *
     * @param runnable The runnable to run asynchronously.
     */
    private void async(Runnable runnable) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    /**
     * Asynchronously selects all sessions from the database and passes them to a consumer.
     * <p>
     * The consumer is called on a new thread, and the database query is executed asynchronously.
     * </p>
     *
     * @param consumer The consumer to call with the list of sessions.
     */
    public void select(Consumer<List<SessionDTO>> consumer) {
        async(() -> consumer.accept(select()));
    }

    public List<ClickSession> getSessions(UUID uniqueId) {

        var sessions = this.requestHelper.select(Tables.SESSIONS, SessionDTO.class, table -> table.where("unique_id", uniqueId));
        var ids = sessions.stream().map(e -> String.valueOf(e.id())).toList();

        var invalidSessions = this.requestHelper.select(Tables.INVALID_SESSIONS, InvalidSessionDTO.class, table -> table.whereIn("session_id", ids));

        List<ClickSession> clickSessions = new ArrayList<>();
        for (SessionDTO session : sessions) {
            var clickSession = new Session(session.getUniqueId(), session.started_at().getTime(), session.finished_at().getTime(), session.getDifferences());
            clickSession.setId(session.id());
            invalidSessions.stream().filter(e -> e.session_id() == clickSession.getId()).findFirst().ifPresent(clickSession::setInvalidSession);
            clickSessions.add(clickSession);
        }
        return clickSessions;
    }

    public void getInvalidSessions(Consumer<List<ClickSession>> consumer) {
        async(() -> verified(this.requestHelper.select(Tables.INVALID_SESSIONS, InvalidSessionDTO.class, table -> table.whereNull("verified_by")), consumer));
    }

    public void getVerifiedInvalidSessions(Consumer<List<ClickSession>> consumer) {
        async(() -> verified(this.requestHelper.select(Tables.INVALID_SESSIONS, InvalidSessionDTO.class, table -> table.whereNotNull("verified_by")), consumer));
    }

    public void getPlayers(Consumer<List<PlayerInfo>> consumer) {
        async(() -> {

            Map<UUID, List<ClickSession>> playerSessions = new HashMap<>();

            var sessions = this.requestHelper.selectAll(Tables.SESSIONS, SessionDTO.class);
            var invalidSessions = this.requestHelper.selectAll(Tables.INVALID_SESSIONS, InvalidSessionDTO.class);

            for (SessionDTO session : sessions) {
                var clickSession = new Session(session.getUniqueId(), session.started_at().getTime(), session.finished_at().getTime(), session.getDifferences());
                clickSession.setId(session.id());
                invalidSessions.stream().filter(e -> e.session_id() == clickSession.getId()).findFirst().ifPresent(clickSession::setInvalidSession);
                playerSessions.computeIfAbsent(session.getUniqueId(), k -> new ArrayList<>()).add(clickSession);
            }

            consumer.accept(playerSessions.entrySet().stream().map(e -> new PlayerInfo(e.getKey(), e.getValue())).toList());
        });
    }

    private void verified(List<InvalidSessionDTO> invalidSessions, Consumer<List<ClickSession>> consumer) {
        var ids = invalidSessions.stream().map(e -> String.valueOf(e.session_id())).toList();
        var sessions = this.requestHelper.select(Tables.SESSIONS, SessionDTO.class, table -> table.whereIn("id", ids));

        List<ClickSession> clickSessions = new ArrayList<>();
        for (SessionDTO session : sessions) {
            var clickSession = new Session(session.getUniqueId(), session.started_at().getTime(), session.finished_at().getTime(), session.getDifferences());
            clickSession.setId(session.id());
            invalidSessions.stream().filter(e -> e.session_id() == clickSession.getId()).findFirst().ifPresent(e -> {
                clickSession.setInvalidSession(e);
                clickSessions.add(clickSession);
            });
        }
        consumer.accept(clickSessions.stream().sorted(Comparator.comparingLong(ClickSession::getStartedAt).reversed()).toList());
    }

    public void validSession(ClickSession session, Player player) {
        async(() -> this.requestHelper.update(Tables.INVALID_SESSIONS, table -> {
            table.uuid("verified_by", player.getUniqueId());
            table.object("verified_at", new Date());
            table.where("session_id", session.getId());
        }));
    }
}
