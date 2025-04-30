package fr.maxlego08.autoclick;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import fr.maxlego08.autoclick.buttons.InvalidSessionButton;
import fr.maxlego08.autoclick.buttons.PlayerButton;
import fr.maxlego08.autoclick.buttons.PlayersButton;
import fr.maxlego08.autoclick.buttons.SessionButton;
import fr.maxlego08.autoclick.buttons.VerifiedInvalidSessionButton;
import fr.maxlego08.autoclick.command.CommandManager;
import fr.maxlego08.autoclick.command.commands.CommandAntiAutoClick;
import fr.maxlego08.autoclick.storage.StorageManager;
import fr.maxlego08.autoclick.zcore.ZPlugin;
import fr.maxlego08.autoclick.zcore.utils.Config;
import fr.maxlego08.autoclick.zcore.utils.plugins.Metrics;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.button.loader.NoneLoader;
import fr.maxlego08.menu.exceptions.InventoryException;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

import java.util.List;

public final class ClickPlugin extends ZPlugin {

    private final StorageManager storageManager = new StorageManager(this);
    private final SessionManager sessionManager = new SessionManager(this);
    private final CommandManager commandManager = new CommandManager(this);

    private InventoryManager inventoryManager;
    private ButtonManager buttonManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {

        this.saveDefaultConfig();

        this.inventoryManager = getProvider(InventoryManager.class);
        this.buttonManager = getProvider(ButtonManager.class);

        Config.load(getConfig(), this);

        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new ClickListener(this), PacketListenerPriority.LOW);

        this.storageManager.loadDatabase();
        this.getServer().getPluginManager().registerEvents(this.sessionManager, this);

        this.commandManager.registerCommand(this, "zantiautoclicks", new CommandAntiAutoClick(this), List.of("zaac"));

        new Metrics(this, 25641);

        this.loadButtons();
        this.loadInventories();

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
        Config.load(getConfig(), this);
        this.loadInventories();
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    private void loadButtons() {
        this.buttonManager.register(new NoneLoader(this, InvalidSessionButton.class, "ZANTIAUTOCLICK_INVALID_SESSIONS"));
        this.buttonManager.register(new NoneLoader(this, VerifiedInvalidSessionButton.class, "ZANTIAUTOCLICK_VERIFIED_INVALID_SESSIONS"));
        this.buttonManager.register(new NoneLoader(this, PlayersButton.class, "ZANTIAUTOCLICK_PLAYERS"));
        this.buttonManager.register(new NoneLoader(this, PlayerButton.class, "ZANTIAUTOCLICK_PLAYER"));
        this.buttonManager.register(new NoneLoader(this, SessionButton.class, "ZANTIAUTOCLICK_CLICKS"));
    }

    private void loadInventories() {
        try {
            this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/invalid-sessions.yml");
            this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/main-sessions.yml");
            this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/player-sessions.yml");
            this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/players-sessions.yml");
            this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/verified-invalid-sessions.yml");
            this.inventoryManager.loadInventoryOrSaveResource(this, "inventories/info-session.yml");
        } catch (InventoryException exception) {
            exception.printStackTrace();
        }
    }
}
