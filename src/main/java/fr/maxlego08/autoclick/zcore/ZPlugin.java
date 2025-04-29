package fr.maxlego08.autoclick.zcore;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ZPlugin extends JavaPlugin {

    protected <T> T getProvider(Class<T> classz) {
        RegisteredServiceProvider<T> provider = getServer().getServicesManager().getRegistration(classz);
        if (provider == null) {
            getLogger().warning("Unable to retrieve the provider " + classz);
            return null;
        }
        return provider.getProvider() != null ? provider.getProvider() : null;
    }

}
