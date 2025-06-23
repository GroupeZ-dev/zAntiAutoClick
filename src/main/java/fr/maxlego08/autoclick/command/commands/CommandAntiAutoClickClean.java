package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ZClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;

public class CommandAntiAutoClickClean extends VCommand {

    public CommandAntiAutoClickClean(ZClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_CLEAN);
        this.addSubCommand("clean");
        this.setDescription(Message.DESCRIPTION_CLEAN);
    }

    @Override
    protected CommandType perform(ZClickPlugin plugin) {

        plugin.getStorageManager().clean();
        message(sender, Message.CLEAN);

        return CommandType.SUCCESS;
    }

}
