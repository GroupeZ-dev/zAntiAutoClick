package fr.maxlego08.autoclick;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClickPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new ClickListener(this), PacketListenerPriority.LOW);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
