package net.oceanias.ocean;

import net.oceanias.ocean.configuration.OConfiguration;
import net.oceanias.ocean.configuration.impl.OPrimaryConfig;
import net.oceanias.ocean.database.ODatabase;
import net.oceanias.ocean.module.OModule;
import net.oceanias.ocean.module.admin.OAdminModule;
import net.oceanias.ocean.plugin.OPlugin;
import net.oceanias.ocean.utility.builder.OItemBuilder;
import net.oceanias.ocean.utility.helper.OTeleportHelper;
import java.util.List;
import xyz.xenondevs.invui.gui.structure.Structure;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@SuppressWarnings("unused")
public final class Ocean extends OPlugin {
    @Getter
    @Accessors(fluent = true)
    private static Ocean get;

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
        return List.of(OPrimaryConfig.get());
    }

    @Contract(pure = true)
    @Override
    public @Nullable ODatabase getDatabase() {
        return null;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getColour() {
        return "<#B700FF>";
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getPrefix() {
        return "<#B700FF><bold>OCEAN </bold><gray>»";
    }

    @Override
    protected void loadPlugin() {
        get = this;

        Structure.addGlobalIngredient(
            OPrimaryConfig.get().getMenu().getIngredients().getBorder(),
            OItemBuilder.getBorder()
        );
    }

    @Override
    protected void enablePlugin() {
        new OTeleportHelper(this).onRegister();
    }
}