package fr.maxlego08.autoclick;

import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.autoclick.api.result.SessionResult;
import fr.maxlego08.autoclick.api.storage.dto.SessionDTO;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.utils.Config;
import fr.maxlego08.autoclick.zcore.utils.ZUtils;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager extends ZUtils implements Listener {

    private final ClickPlugin plugin;
    private final ConcurrentMap<UUID, Session> sessions = new ConcurrentHashMap<>();

    public SessionManager(ClickPlugin plugin) {
        this.plugin = plugin;
    }

    public void onClick(UUID uuid) {

        long now = System.currentTimeMillis();
        var session = this.sessions.computeIfAbsent(uuid, k -> new Session(uuid, now));

        if (session.getLastClickAt() != 0) {

            var difference = (int) ((int) now - session.getLastClickAt());

            // Si la différence est trop courte, alors le joueur spam click
            if (difference >= Config.minimumDelay) {
                session.addDifferences(difference);
            }
        }

        var task = session.getTask();
        if (task != null) {
            task.cancel();
        }

        var scheduler = this.plugin.getServer().getScheduler();
        session.setTask(scheduler.runTaskLater(this.plugin, () -> this.endSession(uuid, session), Config.sessionEndAfter));
        session.setLastClickAt(now);
    }

    private void endSession(UUID uuid, Session session) {

        var player = Bukkit.getPlayer(uuid);
        session.setFinishedAt(System.currentTimeMillis());
        sessions.remove(uuid);

        var storage = this.plugin.getStorageManager();

        // Si la session est valide, on va l'enregistrer
        if (session.isValid()) {
            storage.insertSession(uuid, session, id -> {
                session.setId(id);
                this.plugin.getLogger().info(uuid + " vient de terminer une session de " + session.count() + " cliques. Durée: " + (session.getDuration() / 1000) + "s.");

                var scheduler = this.plugin.getServer().getScheduler();
                scheduler.runTaskAsynchronously(this.plugin, () -> {

                    var analyzeResult = ClickAnalyzer.analyzeSession(session.getDifferences());

                    if (analyzeResult.isCheat()) {

                        var sessionResult = this.verifySession(session);
                        this.plugin.getLogger().info("La session de " + uuid + " (id: " + session.getId() + ") est considéré comme une session d'auto-click.");
                        storage.insertInvalidSession(session, sessionResult, analyzeResult, invalidSession -> {
                            session.setInvalidSession(invalidSession);
                            this.sendActions(Config.endCheatSessionActions, player, session);
                        });
                    }
                });
            });
        }

        var task = session.getTask();
        if (task != null) task.cancel();
        session.setTask(null);

    }

    /**
     * Verifies a session against the configured limits.
     * <p>
     * This method analyzes the click intervals of the provided ClickSession, calculates
     * statistical data such as average, median, and standard deviation, and formats this
     * information into a SessionResult object. The analysis excludes a percentage of
     * extreme values based on the configuration settings.
     * </p>
     *
     * @param session The ClickSession containing the click interval data to analyze.
     * @return A SessionResult object containing the calculated statistical data.
     */
    private SessionResult verifySession(ClickSession session) {

        List<Integer> intervals = session.getDifferences();
        List<Integer> sorted = new ArrayList<>(intervals);
        Collections.sort(sorted);

        int size = sorted.size();
        int removeCount = (int) (size * Config.sessionTrimmed);
        List<Integer> trimmed = sorted.subList(removeCount, size - removeCount);

        double average = trimmed.stream().mapToInt(Integer::intValue).average().orElse(0);

        double variance = trimmed.stream().mapToDouble(i -> Math.pow(i - average, 2)).average().orElse(0);
        double standardDeviation = Math.sqrt(variance);
        double median = calculateMedian(trimmed);

        return new SessionResult(average, median, standardDeviation);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        var uuid = event.getPlayer().getUniqueId();
        if (sessions.containsKey(uuid)) {
            this.endSession(uuid, sessions.get(uuid));
        }
    }

    /**
     * Provides information about a ClickSession to the specified CommandSender.
     *
     * <p>This method analyzes the click intervals of the provided ClickSession, calculates
     * statistical data such as average, median, and standard deviation, and formats this
     * information into a message sent to the CommandSender. The analysis excludes a
     * percentage of extreme values based on the configuration settings. If the standard
     * deviation is above a configured threshold and the 'sendIfClean' flag is false,
     * the method will not send any information.</p>
     *
     * @param sender      the CommandSender to whom the session information will be sent
     * @param session     the ClickSession containing the click interval data to analyze
     * @param sendIfClean a flag indicating whether to send information even if the session
     *                    is considered clean (i.e., within normal statistical bounds)
     */
    public void information(CommandSender sender, ClickSession session, boolean sendIfClean) {

        long duration = session.getDuration();
        var intervals = session.getDifferences();
        var sessionResult = verifySession(session);
        var average = sessionResult.average();
        var median = sessionResult.median();
        var standardDeviation = sessionResult.standardDeviation();

        if (standardDeviation >= Config.standardDeviation && !sendIfClean) return;

        DecimalFormat format = new DecimalFormat("#.##");

        long hours = duration / 3600000;
        long minutes = (duration % 3600000) / 60000;
        long seconds = (duration % 60000) / 1000;

        var result = ClickAnalyzer.analyzeSession(intervals);
        var percents = result.percent();

        message(sender, Message.SESSION_INFORMATION, "%uuid%", session.getUniqueId().toString(), "%id%", session.getId(), "%average%", format.format(average), "%median%", format.format(median), "%color%", standardDeviation < 10 ? "§4" : standardDeviation < 20 ? "§4" : standardDeviation < 30 ? "§6" : standardDeviation < 40 ? "§e" : "§a", "%standard-deviation%", format.format(standardDeviation), "%duration%", String.format("%02d:%02d:%02d", hours, minutes, seconds), "%percent%", format.format(percents), "%color-percent%", percents >= 90 ? "§4" : percents >= 80 ? "§c" : percents >= 70 ? "§6" : percents >= 60 ? "§e" : "§a", "%cheat%", result.isCheat() ? "Oui" : "Non", "%color-cheat%", result.isCheat() ? "§2" : "§4");
    }


    public void showAll(CommandSender sender) {
        this.plugin.getStorageManager().select(sessions -> sessions.forEach(e -> this.information(sender, e, true)));
    }

    public void sendSuspect(CommandSender sender, long seconds, boolean sendIfClean) {
        this.plugin.getStorageManager().select(sessions -> {
            for (SessionDTO session : sessions) {
                if (session.getDuration() >= seconds * 1000) {
                    this.information(sender, session, sendIfClean);
                }
            }
        });
    }

    private double calculateMedian(List<Integer> data) {
        List<Integer> sorted = new ArrayList<>(data);
        Collections.sort(sorted);
        int middle = sorted.size() / 2;

        if (sorted.size() % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
        } else {
            return sorted.get(middle);
        }
    }

    private void sendActions(List<Action> actions, Player player, ClickSession session) {

        var fakeInventory = this.plugin.getInventoryManager().getFakeInventory();
        var format = new DecimalFormat("#.##");

        long duration = session.getDuration();
        var intervals = session.getDifferences();
        var sessionResult = verifySession(session);
        var average = sessionResult.average();
        var median = sessionResult.median();
        var standardDeviation = sessionResult.standardDeviation();
        long hours = duration / 3600000;
        long minutes = (duration % 3600000) / 60000;
        long seconds = (duration % 60000) / 1000;
        var result = ClickAnalyzer.analyzeSession(intervals);
        var percents = result.percent();

        var placeholders = new Placeholders();
        placeholders.register("player", player.getName());
        placeholders.register("uuid", player.getUniqueId().toString());
        placeholders.register("average", format.format(average));
        placeholders.register("median", format.format(median));
        placeholders.register("standard-deviation", format.format(standardDeviation));
        placeholders.register("hours", String.valueOf(hours));
        placeholders.register("minutes", String.valueOf(minutes));
        placeholders.register("seconds", String.valueOf(seconds));
        placeholders.register("duration", String.format("%02d:%02d:%02d", hours, minutes, seconds));
        placeholders.register("percent", format.format(percents));

        var sessions = this.plugin.getStorageManager().getSessions(player.getUniqueId());
        placeholders.register("sessions", String.valueOf(sessions.size()));
        placeholders.register("s", sessions.size() == 1 ? "" : "s");
        placeholders.register("invalid-sessions", String.valueOf(sessions.stream().filter(ClickSession::isCheat).count()));

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            for (Action action : actions) {
                action.preExecute(player, null, fakeInventory, placeholders);
            }
        });
    }
}
