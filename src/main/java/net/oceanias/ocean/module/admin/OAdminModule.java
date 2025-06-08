package net.oceanias.ocean.module.admin;

import net.oceanias.ocean.Ocean;
import net.oceanias.ocean.component.OModule;
import net.oceanias.ocean.component.OProvider;
import net.oceanias.ocean.module.admin.command.admin.OAdminCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@RequiredArgsConstructor
public final class OAdminModule implements OModule {
    private final Ocean plugin;

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<OProvider> getProviders() {
        return List.of(
            new OAdminCommand(plugin)
        );
    }
}