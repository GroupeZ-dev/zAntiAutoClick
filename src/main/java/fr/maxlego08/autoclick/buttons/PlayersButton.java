package fr.maxlego08.autoclick.buttons;

import fr.maxlego08.autoclick.ZClickPlugin;
import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.autoclick.zcore.utils.PlayerInfo;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class PlayersButton extends SessionHelper {

    private final ZClickPlugin plugin;

    public PlayersButton(Plugin plugin) {
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

        if (!player.hasMetadata("zaac-players")) return;
        if (!(player.getMetadata("zaac-players").getFirst().value() instanceof List<?> list)) return;
        if (list.isEmpty()) return;

        var players = list.stream().map(e -> (PlayerInfo) e).collect(Collectors.toList());

        paginate(players, inventory, (slot, playerInfo) -> {

            Placeholders placeholders = new Placeholders();
            var offlinePlayer = Bukkit.getOfflinePlayer(playerInfo.getUniqueId());
            placeholders.register("target", offlinePlayer.getName() == null ? "Unknown" : offlinePlayer.getName());
            placeholders.register("sessions", String.valueOf(playerInfo.getClickSessions().size()));
            placeholders.register("invalid-sessions", String.valueOf(playerInfo.getClickSessions().stream().filter(ClickSession::isCheat).count()));

            inventory.addItem(slot, updateSkull(getItemStack().build(player, false, placeholders), playerInfo.getUniqueId())).setClick(e -> {
                player.setMetadata("zaac-player", new FixedMetadataValue(plugin, playerInfo));
                this.plugin.getInventoryManager().openInventory(player, plugin, "player-sessions");
            });
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        return player.hasMetadata("zaac-players") && player.getMetadata("zaac-players").getFirst().value() instanceof List<?> list ? list.size() : 0;
    }
}
