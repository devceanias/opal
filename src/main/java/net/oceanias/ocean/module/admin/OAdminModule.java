package net.oceanias.ocean.module.admin;

import net.oceanias.ocean.module.OModule;
import net.oceanias.ocean.module.OProvider;
import net.oceanias.ocean.module.admin.command.admin.OAdminCommand;
import net.oceanias.ocean.plugin.OPlugin;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@RequiredArgsConstructor
public final class OAdminModule implements OModule {
    private final OPlugin plugin;

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<OProvider> getProviders() {
        return List.of(
            new OAdminCommand(plugin)
        );
    }
}