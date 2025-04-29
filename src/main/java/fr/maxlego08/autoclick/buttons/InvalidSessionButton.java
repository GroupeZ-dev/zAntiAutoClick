package fr.maxlego08.autoclick.buttons;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class InvalidSessionButton extends ZButton implements PaginateButton {

    private final ClickPlugin plugin;

    public InvalidSessionButton(Plugin plugin) {
        this.plugin = (ClickPlugin) plugin;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders) {
        return getPaginationSize(player) > 0;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {

        if (!player.hasMetadata("zaac-invalid-sessions")) return;
        if (!(player.getMetadata("zaac-invalid-sessions").getFirst().value() instanceof List<?> list)) return;
        if (list.isEmpty()) return;

        var clickSessions = list.stream().map(e -> (ClickSession) e).collect(Collectors.toList());
        var sessionManager = this.plugin.getSessionManager();

        paginate(clickSessions, inventory, (slot, session) -> {

            Placeholders placeholders = sessionManager.createPlaceholders(session);
            var percent = session.getCheatPercent();
            var standardDeviation = session.getStandardDivision();
            placeholders.register("percent-color", percent >= 90 ? "&4" : percent >= 80 ? "&c" : percent >= 70 ? "&6" : percent >= 60 ? "&e" : "&a");
            placeholders.register("standard-deviation-color", standardDeviation < 10 ? "&4" : standardDeviation < 20 ? "&4" : standardDeviation < 30 ? "&6" : standardDeviation < 40 ? "&e" : "&a");

            var itemStack = getItemStack().build(player, false, placeholders);
            var meta = itemStack.getItemMeta();
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setPlayerProfile(Bukkit.getOfflinePlayer(session.getUniqueId()).getPlayerProfile());
                itemStack.setItemMeta(skullMeta);
            }

            inventory.addItem(slot, itemStack).setRightClick(e -> this.plugin.getSessionManager().validSession(player, session, clickSessions)).setLeftClick(e -> {

            });
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        return player.hasMetadata("zaac-invalid-sessions") && player.getMetadata("zaac-invalid-sessions").getFirst().value() instanceof List<?> list ? list.size() : 0;
    }
}
