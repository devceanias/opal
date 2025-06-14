package net.oceanias.opal.module.admin;

import net.oceanias.opal.Opal;
import net.oceanias.opal.component.impl.OModule;
import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.module.admin.command.admin.OAdminCommand;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@RequiredArgsConstructor
public final class OAdminModule implements OModule {
    private final Opal plugin;

    @Contract(" -> new")
    @Override
    public @NotNull @Unmodifiable List<OProvider> getProviders() {
        return List.of(
            new OAdminCommand(plugin)
        );
    }
}