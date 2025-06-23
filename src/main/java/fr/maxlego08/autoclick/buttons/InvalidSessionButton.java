package fr.maxlego08.autoclick.buttons;

import fr.maxlego08.autoclick.ZClickPlugin;
import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class InvalidSessionButton extends SessionHelper {

    private final ZClickPlugin plugin;

    public InvalidSessionButton(Plugin plugin) {
        this.plugin = (ZClickPlugin) plugin;
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
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
    public void onRender(Player player, InventoryEngine inventory) {

        if (!player.hasMetadata("zaac-invalid-sessions")) return;
        if (!(player.getMetadata("zaac-invalid-sessions").getFirst().value() instanceof List<?> list)) return;
        if (list.isEmpty()) return;

        var clickSessions = list.stream().map(e -> (ClickSession) e).collect(Collectors.toList());
        var sessionManager = this.plugin.getSessionManager();

        paginate(clickSessions, inventory, (slot, session) -> {

            var itemStack = createItemStack(player, session, sessionManager);
            inventory.addItem(slot, itemStack).setRightClick(e -> this.plugin.getSessionManager().validSession(player, session, clickSessions)).setLeftClick(e -> {
                player.setMetadata("zaac-session", new FixedMetadataValue(plugin, session));
                var manager = this.plugin.getInventoryManager();
                manager.getInventory(plugin, "info-session").ifPresentOrElse(i -> manager.openInventoryWithOldInventories(player, i, 1), () -> player.sendMessage("§cImpossible to find the inventory info-session !"));
            });
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        return player.hasMetadata("zaac-invalid-sessions") && player.getMetadata("zaac-invalid-sessions").getFirst().value() instanceof List<?> list ? list.size() : 0;
    }
}
