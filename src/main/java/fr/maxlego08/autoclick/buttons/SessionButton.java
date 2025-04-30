package fr.maxlego08.autoclick.buttons;

import fr.maxlego08.autoclick.ClickPlugin;
import fr.maxlego08.autoclick.api.ClickSession;
import fr.maxlego08.autoclick.zcore.utils.Config;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionButton extends SessionHelper implements PaginateButton {

    private final ClickPlugin plugin;
    private final int CLICKS_PER_ITEM = 20;

    public SessionButton(Plugin plugin) {
        this.plugin = (ClickPlugin) plugin;
    }

    @Override
    public void onInventoryOpen(Player player, InventoryDefault inventory, Placeholders placeholders) {
        super.onInventoryOpen(player, inventory, placeholders);

        if (!player.hasMetadata("zaac-session")) return;
        if (!(player.getMetadata("zaac-session").getFirst().value() instanceof ClickSession clickSession)) return;

        placeholders.register("id", String.valueOf(clickSession.getId()));
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

        if (!player.hasMetadata("zaac-session")) return;
        if (!(player.getMetadata("zaac-session").getFirst().value() instanceof ClickSession session)) return;

        List<List<Integer>> list = new ArrayList<>();
        List<Integer> tempList = new ArrayList<>();
        for (Integer difference : session.getDifferences()) {
            tempList.add(difference);
            if (tempList.size() == CLICKS_PER_ITEM) {
                list.add(new ArrayList<>(tempList));
                tempList.clear();
            }
        }
        if (!tempList.isEmpty()) {
            list.add(new ArrayList<>(tempList));
        }

        paginate(list, inventory, (slot, clicks) -> {
            var placeholders = new Placeholders();
            placeholders.register("clicks", clicks.stream().map(e -> Config.clickLoreLine.replace("%click%", String.valueOf(e))).collect(Collectors.joining("\n")));
            inventory.addItem(slot, getItemStack().build(player, false, placeholders));
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        return player.hasMetadata("zaac-session") && player.getMetadata("zaac-session").getFirst().value() instanceof ClickSession session ? session.getDifferences().size() / CLICKS_PER_ITEM : 0;
    }
}
