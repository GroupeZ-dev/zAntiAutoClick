package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ZClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;

public class CommandAntiAutoClickShowAll extends VCommand {

    public CommandAntiAutoClickShowAll(ZClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_SHOW_ALL);
        this.addSubCommand("showall");
        this.setDescription(Message.DESCRIPTION_SHOW_ALL);
    }

    @Override
    protected CommandType perform(ZClickPlugin plugin) {

        plugin.getSessionManager().showAll(sender);

        return CommandType.SUCCESS;
    }

}
