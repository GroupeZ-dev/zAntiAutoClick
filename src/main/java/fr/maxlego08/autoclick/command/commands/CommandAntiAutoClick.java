package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ZClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;

public class CommandAntiAutoClick extends VCommand {

    public CommandAntiAutoClick(ZClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_USE);
        this.addSubCommand(new CommandAntiAutoClickReload(plugin));

        this.addSubCommand(new CommandAntiAutoClickShow(plugin));
        this.addSubCommand(new CommandAntiAutoClickClean(plugin));
        this.addSubCommand(new CommandAntiAutoClickShowAll(plugin));
        this.addSubCommand(new CommandAntiAutoClickSuspect(plugin));
        this.addSubCommand(new CommandAntiAutoClickShowDuration(plugin));

        this.addSubCommand(new CommandAntiAutoClickOpen(plugin));
        this.addSubCommand(new CommandAntiAutoClickInvalidSessions(plugin));
        this.addSubCommand(new CommandAntiAutoClickVerifiedInvalidSessions(plugin));
        this.addSubCommand(new CommandAntiAutoClickPlayers(plugin));
    }

    @Override
    protected CommandType perform(ZClickPlugin plugin) {
        syntaxMessage();
        return CommandType.SUCCESS;
    }

}
