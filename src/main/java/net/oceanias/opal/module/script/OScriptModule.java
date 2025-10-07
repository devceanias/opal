package net.oceanias.opal.module.script;

import net.oceanias.opal.component.impl.OModule;
import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.module.script.command.script.OScriptCommand;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class OScriptModule implements OModule {
    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable List<OProvider> getProviders() {
        return List.of(
            OScriptManager.get(),
            new OScriptCommand()
        );
    }
}