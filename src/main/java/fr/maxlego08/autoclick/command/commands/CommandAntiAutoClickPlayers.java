package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ZClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;
import org.bukkit.metadata.FixedMetadataValue;

public class CommandAntiAutoClickPlayers extends VCommand {

    public CommandAntiAutoClickPlayers(ZClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_OPEN_PLAYERS);
        this.onlyPlayers();
        this.addSubCommand("players");
        this.setDescription(Message.DESCRIPTION_OPEN_PLAYERS_SESSIONS);
    }

    @Override
    protected CommandType perform(ZClickPlugin plugin) {
        plugin.getStorageManager().getPlayers(sessions -> {
            this.player.setMetadata("zaac-players", new FixedMetadataValue(plugin, sessions));
            plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getInventoryManager().openInventory(this.player, plugin, "players-sessions"));
        });
        return CommandType.SUCCESS;
    }

}
