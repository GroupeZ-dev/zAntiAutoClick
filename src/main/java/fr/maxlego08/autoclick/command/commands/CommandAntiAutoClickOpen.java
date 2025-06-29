package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ZClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;

public class CommandAntiAutoClickOpen extends VCommand {

    public CommandAntiAutoClickOpen(ZClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_OPEN);
        this.addSubCommand("open");
        this.onlyPlayers();
        this.setDescription(Message.DESCRIPTION_OPEN);
    }

    @Override
    protected CommandType perform(ZClickPlugin plugin) {
        plugin.getInventoryManager().openInventory(player, plugin, "main-sessions");
        return CommandType.SUCCESS;
    }

}
