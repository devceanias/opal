package net.oceanias.ocean.plugin;

import net.oceanias.ocean.Ocean;
import net.oceanias.ocean.configuration.OConfiguration;
import net.oceanias.ocean.module.OModule;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import lombok.Getter;

@SuppressWarnings({ "unused", "deprecation" })
@Getter
public abstract class OPlugin extends JavaPlugin {
    private BukkitScheduler scheduler;

    public String getLabel() {
        return getDescription().getName().toLowerCase();
    }

    public List<String> getAuthors() {
        return getDescription().getAuthors();
    }

    public abstract List<OModule> getModules();

    public abstract List<OConfiguration<?>> getConfigurations();

    public abstract String getColour();

    public abstract String getPrefix();

    protected void loadPlugin() {}

    protected void enablePlugin() {}

    protected void disablePlugin() {}

    @Override
    public final void onLoad() {
        ORegistry.registerPlugin(this);

        scheduler = getServer().getScheduler();

        for (final OConfiguration<?> config : getConfigurations()) {
            config.onRegister();
        }

        loadPlugin();
    }

    @Override
    public final void onEnable() {
        if (getClass() != Ocean.class &&
            !getServer().getPluginManager().isPluginEnabled(Ocean.get().getDescription().getName())
        ) {
            getLogger().severe("Oceanilib is missing or not enabled — this plugin will be disabled.");
            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        for (final OModule module : getModules()) {
            module.onRegister();
        }

        enablePlugin();
    }

    @Override
    public final void onDisable() {
        for (final OModule module : getModules()) {
            module.onUnregister();
        }

        for (final OConfiguration<?> config : getConfigurations()) {
            config.onUnregister();
        }

        disablePlugin();

        ORegistry.unregisterPlugin(this);
    }

    public String getPermission(final String permission) {
        return getLabel() + "." + permission;
    }
}