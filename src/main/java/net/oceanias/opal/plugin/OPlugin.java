package net.oceanias.opal.plugin;

import net.oceanias.opal.Opal;
import net.oceanias.opal.configuration.OConfiguration;
import net.oceanias.opal.configuration.impl.OPrimaryConfig;
import net.oceanias.opal.database.ODatabase;
import net.oceanias.opal.component.impl.OModule;
import net.oceanias.opal.menu.OMenu;
import net.oceanias.opal.utility.builder.OItemBuilder;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.structure.Structure;
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

    public abstract List<OConfiguration<?>> getConfigurations();

    public abstract ODatabase getDatabase();

    public abstract List<OModule> getModules();

    public abstract String getColour();

    public abstract String getPrefix();

    protected void loadPlugin() {}

    protected void enablePlugin() {}

    protected void disablePlugin() {}

    @Override
    public final void onLoad() {
        if (getClass() == Opal.class) {
            CommandAPI.onLoad(new CommandAPIBukkitConfig(this).setNamespace(getLabel()));
        }

        scheduler = getServer().getScheduler();

        loadPlugin();
    }

    @Override
    public final void onEnable() {
        if (getClass() != Opal.class &&
            !getServer().getPluginManager().isPluginEnabled(Opal.get().getDescription().getName())
        ) {
            getLogger().severe("Opal is missing or not enabled — this plugin will be disabled.");
            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        if (getClass() == Opal.class) {
            CommandAPI.onEnable();

            InvUI.getInstance().setPlugin(this);

            OMenu.addIngredients();
        }

        for (final OConfiguration<?> config : getConfigurations()) {
            config.registerInternally();
        }

        if (getDatabase() != null) {
            getDatabase().registerInternally();
        }

        for (final OModule module : getModules()) {
            module.registerInternally();
        }

        enablePlugin();
    }

    @Override
    public final void onDisable() {
        disablePlugin();

        for (final OModule module : getModules()) {
            module.unregisterInternally();
        }

        if (getDatabase() != null) {
            getDatabase().unregisterInternally();
        }

        for (final OConfiguration<?> config : getConfigurations()) {
            config.unregisterInternally();
        }

        if (getClass() == Opal.class) {
            CommandAPI.onDisable();
        }
    }

    public String getPermission(final String permission) {
        return getLabel() + "." + permission;
    }
}