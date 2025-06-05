package net.oceanias.ocean;

import net.oceanias.ocean.configuration.OConfiguration;
import net.oceanias.ocean.module.OModule;
import net.oceanias.ocean.plugin.OPlugin;
import net.oceanias.ocean.utility.builder.OItemBuilder;
import net.oceanias.ocean.utility.helper.OTeleportHelper;
import java.util.List;
import xyz.xenondevs.invui.gui.structure.Structure;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@SuppressWarnings("unused")
public final class Ocean extends OPlugin {
    @Getter
    @Accessors(fluent = true)
    private static Ocean get;

    @Contract(pure = true)
    @Override
    public @NotNull String getLabel() {
        return "ocean";
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> getAuthors() {
        return List.of("Plantal");
    }

    @Contract(pure = true)
    @Override
    public @Unmodifiable List<OModule> getModules() {
        return List.of();
    }

    @Contract(pure = true)
    @Override
    public @Unmodifiable List<OConfiguration<?>> getConfigurations() {
        return List.of();
    }

    @Override
    public String getColour() {
        return "";
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getPrefix() {
        return "";
    }

    @Override
    protected void loadPlugin() {
        get = this;

        Structure.addGlobalIngredient('#', OItemBuilder.getBorder());
    }

    @Override
    protected void enablePlugin() {
        new OTeleportHelper(this).onRegister();
    }
}