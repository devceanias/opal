package net.oceanias.opal;

import net.oceanias.opal.configuration.OConfiguration;
import net.oceanias.opal.database.ODatabase;
import net.oceanias.opal.component.impl.OModule;
import net.oceanias.opal.setting.impl.OStringSetting;
import net.oceanias.opal.utility.builder.OItem;
import net.oceanias.opal.utility.helper.OTeleportHelper;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.structure.Structure;
import lombok.Getter;
import lombok.experimental.Accessors;

@SuppressWarnings({ "unused", "deprecation" })
@Getter
//public abstract class OPlugin extends ZapperJavaPlugin {
public abstract class OPlugin extends JavaPlugin {
    @Getter
    @Accessors(fluent = true)
    private static OPlugin get;

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

    // This ensures that the user provides their own loader for paper-plugin.yml.
    public abstract OLoader getLoader();

    protected abstract void setInstance();

    public boolean reloadsDatapacks() {
        return false;
    }

    protected void loadPlugin() {}

    protected void enablePlugin() {}

    protected void disablePlugin() {}

    @Override
    public final void onLoad() {
        if (get != null) {
            throw new IllegalStateException("Error loading plugin; only one OPlugin is allowed.");
        }

        if (getLoader() == null) {
            throw new IllegalStateException("Error loading plugin; getLoader() returned null.");
        }

        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
            .skipReloadDatapacks(!reloadsDatapacks())
            .missingExecutorImplementationMessage("This command does not support %s.")
        );

        get = this;
        scheduler = getServer().getScheduler();

        setInstance();
        loadPlugin();
    }

    @Override
    public final void onEnable() {
        CommandAPI.onEnable();

        InvUI.getInstance().setPlugin(this);

        for (final OConfiguration<?> config : getConfigurations()) {
            config.registerInternally();
        }

        Structure.addGlobalIngredient('#', OItem.FILLER.get());

        if (getDatabase() != null) {
            getDatabase().registerInternally();
        }

        for (final OModule module : getModules()) {
            module.registerInternally();
        }

        new OStringSetting.Listener().registerInternally();
        new OTeleportHelper.Listener().registerInternally();

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

        CommandAPI.onDisable();
    }

    public String getPermission(final String permission) {
        return getLabel() + "." + permission;
    }
}