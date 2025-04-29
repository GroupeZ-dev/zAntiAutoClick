package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;
import org.bukkit.metadata.FixedMetadataValue;

public class CommandAntiAutoClickInvalidSessions extends VCommand {

    public CommandAntiAutoClickInvalidSessions(ClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_OPEN_INVALID_SESSIONS);
        this.onlyPlayers();
        this.addSubCommand("invalid-sessions");
        this.setDescription(Message.DESCRIPTION_OPEN_INVALID_SESSIONS);
    }

    @Override
    protected CommandType perform(ClickPlugin plugin) {
        plugin.getStorageManager().getInvalidSessions(sessions -> {
            this.player.setMetadata("zaac-invalid-sessions", new FixedMetadataValue(plugin, sessions));
            plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getInventoryManager().openInventory(this.player, plugin, "invalid-sessions"));
        });
        return CommandType.SUCCESS;
    }

}
