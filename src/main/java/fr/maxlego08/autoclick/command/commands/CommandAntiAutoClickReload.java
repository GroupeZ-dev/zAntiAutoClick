package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;

public class CommandAntiAutoClickReload extends VCommand {

    public CommandAntiAutoClickReload(ClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_RELOAD);
        this.addSubCommand("reload", "rl");
        this.setDescription(Message.DESCRIPTION_RELOAD);
    }

    @Override
    protected CommandType perform(ClickPlugin plugin) {

        plugin.reloadFiles();
        message(sender, Message.RELOAD);

        return CommandType.SUCCESS;
    }

}
