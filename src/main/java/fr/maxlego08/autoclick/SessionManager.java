package fr.maxlego08.autoclick;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
        this.plugin.getStorageManager().insertSession(uuid, session);

        this.plugin.getLogger().info(uuid + " vient de terminer une session de " + session.count() + " cliques. Durée: " + (session.getDuration() / 1000) + "s.");

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
}
