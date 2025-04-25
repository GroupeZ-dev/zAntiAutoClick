package fr.maxlego08.autoclick.zcore.utils;

import fr.maxlego08.autoclick.zcore.enums.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;

public abstract class ZUtils {

    /**
     * Checks if a permissible entity has a specific permission.
     *
     * @param permissible the entity to check.
     * @param permission  the permission string to check for.
     * @return true if the entity has the permission, false otherwise.
     */
    protected boolean hasPermission(Permissible permissible, String permission) {
        return permissible.hasPermission(permission);
    }

    protected void message(CommandSender sender, Message message, Object... args) {
        sender.sendMessage(message.getMessage());
    }

}
