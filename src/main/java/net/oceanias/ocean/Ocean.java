package net.oceanias.ocean;

import net.oceanias.ocean.configuration.OConfiguration;
import net.oceanias.ocean.module.OModule;
import net.oceanias.ocean.plugin.OPlugin;
import java.util.List;
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

    @Contract(pure = true)
    @Override
    public @NotNull String getPrefix() {
        return "";
    }

    @Override
    protected void loadPlugin() {
        get = this;
    }
}