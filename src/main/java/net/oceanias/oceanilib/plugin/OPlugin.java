package net.oceanias.oceanilib.plugin;

import net.oceanias.oceanilib.ORegistry;
import net.oceanias.oceanilib.configuration.OConfiguration;
import net.oceanias.oceanilib.module.OModule;
import java.util.ArrayList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import lombok.Getter;

@SuppressWarnings("unused")
@Getter
public abstract class OPlugin extends JavaPlugin {
    private BukkitScheduler scheduler;

    public abstract String getLabel();

    public abstract String getAuthor();

    public abstract String getVersion();

    public abstract ArrayList<OModule> getModules();

    public abstract ArrayList<OConfiguration<?>> getConfigurations();

    public abstract String getPrefix();

    protected abstract void loadPlugin();

    protected abstract void enablePlugin();

    protected abstract void disablePlugin();

    @Override
    public final void onLoad() {
        ORegistry.registerPlugin(this);

        this.scheduler = getServer().getScheduler();

        loadPlugin();
    }

    @Override
    public final void onEnable() {
        for (final OConfiguration<?> config : getConfigurations()) {
            config.loadConfiguration();
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

        disablePlugin();

        ORegistry.unregisterPlugin(this);
    }

    public String getPermission(final String permission) {
        return getLabel() + "." + permission;
    }
}