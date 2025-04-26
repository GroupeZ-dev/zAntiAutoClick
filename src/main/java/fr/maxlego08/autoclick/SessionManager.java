package fr.maxlego08.autoclick;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager implements Listener {

    private final ClickPlugin plugin;
    private final ConcurrentMap<UUID, Session> sessions = new ConcurrentHashMap<>();

    public SessionManager(ClickPlugin plugin) {
        this.plugin = plugin;
    }

    public void onClick(UUID uuid) {

        long now = System.currentTimeMillis();
        var session = this.sessions.computeIfAbsent(uuid, k -> new Session(now));

        if (session.getLastClickAt() != 0) {

            var difference = now - session.getLastClickAt();

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

    public void information(CommandSender sender, List<Integer> intervals) {

        // Moyenne
        double average = intervals.stream().mapToInt(Integer::intValue).average().orElse(0);

        // Écart type
        double variance = intervals.stream()
                .mapToDouble(i -> Math.pow(i - average, 2))
                .average()
                .orElse(0);
        double standardDeviation = Math.sqrt(variance);

        // Médiane
        double median = calculateMedian(intervals);

        // Affichage
        sender.sendMessage(String.format("Moyenne: %.2f ms", average));
        sender.sendMessage(String.format("Médiane: %.2f ms", median));
        sender.sendMessage(String.format("Écart type: %.2f ms", standardDeviation));
    }

    public void showAll(CommandSender sender) {

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
