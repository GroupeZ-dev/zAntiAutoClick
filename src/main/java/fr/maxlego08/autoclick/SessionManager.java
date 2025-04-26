package fr.maxlego08.autoclick;

import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.autoclick.storage.dto.SessionDTO;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.utils.ZUtils;
import org.bukkit.command.CommandSender;
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

                var task = session.getTask();
                if (task != null) task.cancel();

                session.setTask(this.plugin.getServer().getScheduler().runTaskLater(plugin, () -> this.endSession(uuid, session), Config.sessionEndAfter));
            }
        }

        session.setLastClickAt(now);
    }

    private void endSession(UUID uuid, Session session) {

        session.setFinishedAt(System.currentTimeMillis());
        sessions.remove(uuid);

        // Si la session est valide, on va l'enregistrer
        if (session.isValid()) {
            this.plugin.getStorageManager().insertSession(uuid, session);
            this.plugin.getLogger().info(uuid + " vient de terminer une session de " + session.count() + " cliques. Durée: " + (session.getDuration() / 1000) + "s.");
        }

        var task = session.getTask();
        if (task != null) task.cancel();
        session.setTask(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        var uuid = event.getPlayer().getUniqueId();
        if (sessions.containsKey(uuid)) {
            this.endSession(uuid, sessions.get(uuid));
        }
    }

    public void information(CommandSender sender, ClickSession session, boolean sendIfClean) {

        List<Integer> intervals = session.getDifferences();
        long duration = session.getDuration();
        // Trier les intervalles
        List<Integer> sorted = new ArrayList<>(intervals);
        Collections.sort(sorted);

        // Retirer les 5% les plus bas et les 5% les plus hauts
        int size = sorted.size();
        int removeCount = (int) (size * Config.removePercent);
        List<Integer> trimmed = sorted.subList(removeCount, size - removeCount);

        // Moyenne
        double average = trimmed.stream().mapToInt(Integer::intValue).average().orElse(0);

        // Écart type
        double variance = trimmed.stream()
                .mapToDouble(i -> Math.pow(i - average, 2))
                .average()
                .orElse(0);
        double standardDeviation = Math.sqrt(variance);

        if (standardDeviation >= Config.sessionIsNormal && !sendIfClean) return;

        // Médiane
        double median = calculateMedian(trimmed);

        // Affichage

        DecimalFormat format = new DecimalFormat("#.##");

        long hours = duration / 3600000;
        long minutes = (duration % 3600000) / 60000;
        long seconds = (duration % 60000) / 1000;

        message(sender, Message.SESSION_INFORMATION,
                "%uuid%", session.getUniqueId().toString(),
                "%id%", session.getId(),
                "%average%", format.format(average),
                "%median%", format.format(median),
                "%color%", standardDeviation < 10 ? "§4" : standardDeviation < 20 ? "§4" : standardDeviation < 30 ? "§6" : standardDeviation < 40 ? "§e" : "§a",
                "%standard-deviation%", format.format(standardDeviation),
                "%duration%", String.format("%02d:%02d:%02d", hours, minutes, seconds)
        );
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
}
