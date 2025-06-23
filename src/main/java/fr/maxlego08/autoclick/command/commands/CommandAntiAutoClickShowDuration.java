package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ZClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;

import java.util.List;

public class CommandAntiAutoClickShowDuration extends VCommand {

    public CommandAntiAutoClickShowDuration(ZClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_SHOW_DURATION);
        this.addSubCommand("show-duration");
        this.setDescription(Message.DESCRIPTION_SHOW);
        this.addRequireArg("seconds", (a, b) -> List.of("60", "120", "180", "240", "300"));
    }

    @Override
    protected CommandType perform(ZClickPlugin plugin) {

        int seconds = this.argAsInteger(0);
        plugin.getSessionManager().sendSuspect(sender, seconds, true);

        return CommandType.SUCCESS;
    }

}
