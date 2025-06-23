package fr.maxlego08.autoclick.api;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import org.bukkit.plugin.Plugin;

public interface ClickPlugin extends Plugin {


    ButtonManager getButtonManager();

    InventoryManager getInventoryManager();


}
