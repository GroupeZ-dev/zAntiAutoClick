package fr.maxlego08.autoclick.buttons;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.SessionManager;
import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.autoclick.zcore.utils.Config;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class VerifiedInvalidSessionButton extends SessionHelper implements PaginateButton {

    private final ClickPlugin plugin;

    public VerifiedInvalidSessionButton(Plugin plugin) {
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

        if (!player.hasMetadata("zaac-verified-invalid-sessions")) return;
        if (!(player.getMetadata("zaac-verified-invalid-sessions").getFirst().value() instanceof List<?> list)) return;
        if (list.isEmpty()) return;

        var clickSessions = list.stream().map(e -> (ClickSession) e).collect(Collectors.toList());
        var sessionManager = this.plugin.getSessionManager();

        paginate(clickSessions, inventory, (slot, session) -> {

            var itemStack = createItemStack(player, session, sessionManager);
            inventory.addItem(slot, itemStack).setClick(e -> {
                player.setMetadata("zaac-session", new FixedMetadataValue(plugin, session));
                var manager = this.plugin.getInventoryManager();
                manager.getInventory(plugin, "info-session").ifPresentOrElse(i -> manager.openInventoryWithOldInventories(player, i, 1), () -> player.sendMessage("§cImpossible to find the inventory info-session !"));
            });
        });
    }

    @Override
    protected void onPlaceholder(Player player, Placeholders placeholders, ClickSession session, SessionManager sessionManager) {

        var offlinePlayer = Bukkit.getOfflinePlayer(session.getUniqueId());
        placeholders.register("verified_by", offlinePlayer.getName() == null ? "Unknown" : offlinePlayer.getName());
        placeholders.register("verified_at", Config.simpleDateFormat.format(session.getVerifiedAt()));
    }

    @Override
    public int getPaginationSize(Player player) {
        return player.hasMetadata("zaac-verified-invalid-sessions") && player.getMetadata("zaac-verified-invalid-sessions").getFirst().value() instanceof List<?> list ? list.size() : 0;
    }
}
