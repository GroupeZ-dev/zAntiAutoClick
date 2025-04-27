package fr.maxlego08.autoclick;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import fr.maxlego08.autoclick.command.CommandManager;
import fr.maxlego08.autoclick.command.commands.CommandAntiAutoClick;
import fr.maxlego08.autoclick.storage.StorageManager;
import fr.maxlego08.autoclick.zcore.ZPlugin;
import fr.maxlego08.autoclick.zcore.utils.plugins.Metrics;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

import java.util.List;

public final class ClickPlugin extends ZPlugin {

    private final StorageManager storageManager = new StorageManager(this);
    private final SessionManager sessionManager = new SessionManager(this);
    private final CommandManager commandManager = new CommandManager(this);

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

        this.commandManager.registerCommand(this, "zantiautoclicks", new CommandAntiAutoClick(this), List.of("zaac"));

        new Metrics(this, 25641);

        commandManager.validCommands();
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

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void reloadFiles() {
        this.reloadConfig();
    }
}
