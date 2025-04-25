package fr.maxlego08.autoclick;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import fr.maxlego08.autoclick.storage.StorageManager;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClickPlugin extends JavaPlugin {

    private final StorageManager storageManager = new StorageManager(this);
    private final SessionManager sessionManager = new SessionManager(this);

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        Config.load(getConfig());

        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new ClickListener(this), PacketListenerPriority.LOW);

        this.storageManager.loadDatabase();
        this.getServer().getPluginManager().registerEvents(this.sessionManager, this);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
