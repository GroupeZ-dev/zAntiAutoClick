package fr.maxlego08.autoclick.buttons;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.zcore.utils.PlayerInfo;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class PlayerButton extends SessionHelper {

    private final ClickPlugin plugin;

    public PlayerButton(Plugin plugin) {
        this.plugin = (ClickPlugin) plugin;
    }

    @Override
    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);

        if (!player.hasMetadata("zaac-player")) return;
        if (!(player.getMetadata("zaac-player").getFirst().value() instanceof PlayerInfo playerInfo)) return;

        var offlinePlayer = Bukkit.getOfflinePlayer(playerInfo.getUniqueId());
        placeholders.register("target", offlinePlayer.getName() == null ? "Unknown" : offlinePlayer.getName());
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

        if (!player.hasMetadata("zaac-player")) return;
        if (!(player.getMetadata("zaac-player").getFirst().value() instanceof PlayerInfo playerInfo)) return;

        var sessionManager = this.plugin.getSessionManager();
        paginate(playerInfo.getClickSessions(), inventory, (slot, clickSession) -> {

            var itemStack = createItemStack(player, clickSession, sessionManager);
            inventory.addItem(slot, itemStack).setClick(e -> {
                player.setMetadata("zaac-session", new FixedMetadataValue(plugin, clickSession));
                var manager = this.plugin.getInventoryManager();
                manager.getInventory(plugin, "info-session").ifPresentOrElse(i -> manager.openInventoryWithOldInventories(player, i, 1), () -> player.sendMessage("§cImpossible to find the inventory info-session !"));
            });
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        return player.hasMetadata("zaac-player") && player.getMetadata("zaac-player").getFirst().value() instanceof PlayerInfo playerInfo ? playerInfo.getClickSessions().size() : 0;
    }
}
