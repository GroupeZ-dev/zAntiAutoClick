package fr.maxlego08.autoclick.zcore.utils;

import fr.maxlego08.autoclick.zcore.enums.Message;
import fr.maxlego08.autoclick.zcore.enums.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Sends a message with prefix to the specified command sender.
     *
     * @param sender  the command sender to send the message to.
     * @param message the message to send.
     * @param args    the arguments for the message.
     */
    protected void message(CommandSender sender, String message, Object... args) {
        sender.sendMessage(Message.PREFIX.msg() + getMessage(message, args));
    }

    /**
     * Sends a message to the specified command sender.
     *
     * @param sender  the command sender to send the message to.
     * @param message the message to send.
     */
    private void message(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    protected void message(CommandSender sender, Message message, Object... args) {
        if (sender instanceof ConsoleCommandSender) {
            if (!message.getMessages().isEmpty()) {
                message.getMessages().forEach(msg -> message(sender, getMessage(msg, args)));
            } else {
                message(sender, Message.PREFIX.msg() + getMessage(message, args));
            }
        } else {
            Player player = (Player) sender;
            switch (message.getType()) {
                case CENTER:
                    if (!message.getMessages().isEmpty()) {
                        message.getMessages().forEach(msg -> sender.sendMessage(this.getCenteredMessage(this.papi(getMessage(msg, args), player))));
                    } else {
                        sender.sendMessage(this.getCenteredMessage(this.papi(getMessage(message, args), player)));
                    }
                    break;
                case ACTION:
                    this.actionMessage(player, message, args);
                    break;
                case TCHAT_AND_ACTION:
                    this.actionMessage(player, message, args);
                    sendTchatMessage(player, message, args);
                    break;
                case TCHAT:
                case WITHOUT_PREFIX:
                    sendTchatMessage(player, message, args);
                    break;
                case TITLE:
                    String title = message.getTitle();
                    String subTitle = message.getSubTitle();
                    int fadeInTime = message.getStart();
                    int showTime = message.getTime();
                    int fadeOutTime = message.getEnd();
                    this.title(player, this.papi(this.getMessage(title, args), player), this.papi(this.getMessage(subTitle, args), player), fadeInTime, showTime, fadeOutTime);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Gets the formatted message with arguments replaced.
     *
     * @param message the message to format.
     * @param args    the arguments for the message.
     * @return the formatted message.
     */
    protected String getMessage(Message message, Object... args) {
        return getMessage(message.getMessage(), args);
    }

    /**
     * Gets the formatted message with arguments replaced.
     *
     * @param message the message to format.
     * @param args    the arguments for the message.
     * @return the formatted message.
     */
    protected String getMessage(String message, Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Number of invalid arguments. Arguments must be in pairs.");
        }

        for (int i = 0; i < args.length; i += 2) {
            if (args[i] == null || args[i + 1] == null) {
                throw new IllegalArgumentException("Keys and replacement values must not be null.");
            }
            message = message.replace(args[i].toString(), args[i + 1].toString());
        }
        return message;
    }

    /**
     * Translates alternate color codes in the message string.
     *
     * @param message the message to color.
     * @return the colored message.
     */
    protected String color(String message) {

        if (message == null) return null;

        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, String.valueOf(net.md_5.bungee.api.ChatColor.of(color)));
            matcher = pattern.matcher(message);
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Sends a chat message to the specified player.
     *
     * @param player  the player to send the message to.
     * @param message the message to send.
     * @param args    the arguments for the message.
     */
    private void sendTchatMessage(Player player, Message message, Object... args) {
        if (message.getMessages().size() > 1) {
            message.getMessages().forEach(msg -> message(player, this.papi(getMessage(msg, args), player)));
        } else {
            message(player, this.papi((message.getType() == MessageType.WITHOUT_PREFIX ? "" : Message.PREFIX.msg()) + getMessage(message, args), player));
        }
    }

    public String papi(String string, Player player) {
        return string;
    }

    protected void actionMessage(Player player, Message message, Object... args) {

    }

    protected void title(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {

    }

    protected String getCenteredMessage(String message) {
        return message;
    }
}
