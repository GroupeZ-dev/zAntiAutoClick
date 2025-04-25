package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;

import java.util.Arrays;

public class CommandAntiAutoClickShow extends VCommand {

    public CommandAntiAutoClickShow(ClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_SHOW);
        this.addSubCommand("show");
        this.setDescription(Message.DESCRIPTION_SHOW);
        this.addRequireArg("id");
    }

    @Override
    protected CommandType perform(ClickPlugin plugin) {

        int id = this.argAsInteger(0);
        plugin.getStorageManager().select(id, session -> {

            if (session == null) {
                message(sender, Message.SESSION_NOT_FOUND, "%id%", id);
                return;
            }

            plugin.getSessionManager().information(sender, Arrays.stream(session.differences().split(",")).map(Integer::parseInt).toList());
        });

        return CommandType.SUCCESS;
    }

}
