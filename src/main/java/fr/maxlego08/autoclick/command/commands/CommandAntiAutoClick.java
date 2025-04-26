package fr.maxlego08.autoclick.command.commands;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.command.VCommand;
import fr.maxlego08.autoclick.zcore.enums.Permission;
import fr.maxlego08.autoclick.zcore.utils.commands.CommandType;

public class CommandAntiAutoClick extends VCommand {

    public CommandAntiAutoClick(ClickPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZANTIAUTOCLICK_USE);
        this.addSubCommand(new CommandAntiAutoClickReload(plugin));
        this.addSubCommand(new CommandAntiAutoClickShow(plugin));
        this.addSubCommand(new CommandAntiAutoClickClean(plugin));
        this.addSubCommand(new CommandAntiAutoClickShowAll(plugin));
        this.addSubCommand(new CommandAntiAutoClickSuspect(plugin));
        this.addSubCommand(new CommandAntiAutoClickShowDuration(plugin));
    }

    @Override
    protected CommandType perform(ClickPlugin plugin) {
        syntaxMessage();
        return CommandType.SUCCESS;
    }

}
