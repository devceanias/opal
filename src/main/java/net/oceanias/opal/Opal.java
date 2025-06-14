package net.oceanias.opal;

import net.oceanias.opal.configuration.OConfiguration;
import net.oceanias.opal.configuration.impl.OPrimaryConfig;
import net.oceanias.opal.database.ODatabase;
import net.oceanias.opal.menu.OMenu;
import net.oceanias.opal.component.impl.OModule;
import net.oceanias.opal.module.admin.OAdminModule;
import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.builder.OItemBuilder;
import net.oceanias.opal.utility.helper.OTeleportHelper;
import java.util.List;
import xyz.xenondevs.invui.InvUI;
import xyz.xenondevs.invui.gui.structure.Structure;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@SuppressWarnings("unused")
public final class Opal extends OPlugin {
    @Getter
    @Accessors(fluent = true)
    private static Opal get;

    @Contract(pure = true)
    @Override
    public @Unmodifiable @NotNull List<OModule> getModules() {
        return List.of(
            new OAdminModule(this)
        );
    }

    @Contract(pure = true)
    @Override
    public @Unmodifiable @NotNull List<OConfiguration<?>> getConfigurations() {
        return List.of(
            OPrimaryConfig.get()
        );
    }

    @Contract(pure = true)
    @Override
    public @Nullable ODatabase getDatabase() {
        return null;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getColour() {
        return "<#75F9E7>";
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getPrefix() {
        return "<#75F9E7><bold>OPAL </bold><gray>»";
    }

    @Override
    protected void loadPlugin() {
        get = this;
    }

    @Override
    protected void enablePlugin() {
        new OTeleportHelper(this).registerInternally();
    }
}