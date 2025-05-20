package fr.maxlego08.autoclick.buttons;

import fr.maxlego08.autoclick.SessionManager;
import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public abstract class SessionHelper extends PaginateButton {

    protected ItemStack createItemStack(Player player, ClickSession session, SessionManager sessionManager) {

        Placeholders placeholders = sessionManager.createPlaceholders(session);
        var percent = session.getCheatPercent();
        var standardDeviation = session.getStandardDivision();
        placeholders.register("percent-color", percent >= 90 ? "&4" : percent >= 80 ? "&c" : percent >= 70 ? "&6" : percent >= 60 ? "&e" : "&a");
        placeholders.register("standard-deviation-color", standardDeviation < 10 ? "&4" : standardDeviation < 20 ? "&4" : standardDeviation < 30 ? "&6" : standardDeviation < 40 ? "&e" : "&a");
        onPlaceholder(player, placeholders, session, sessionManager);

        var itemStack = getItemStack().build(player, false, placeholders);
        return updateSkull(itemStack, session.getUniqueId());
    }

    protected ItemStack updateSkull(ItemStack itemStack, UUID uuid) {
        var meta = itemStack.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setPlayerProfile(Bukkit.getOfflinePlayer(uuid).getPlayerProfile());
            itemStack.setItemMeta(skullMeta);
        }
        return itemStack;
    }

    protected void onPlaceholder(Player player, Placeholders placeholders, ClickSession session, SessionManager sessionManager) {

    }

}
