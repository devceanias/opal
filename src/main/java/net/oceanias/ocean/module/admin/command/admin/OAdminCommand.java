package net.oceanias.ocean.module.admin.command.admin;

import net.oceanias.ocean.Ocean;
import net.oceanias.ocean.command.OCommand;
import net.oceanias.ocean.command.OSubcommand;
import net.oceanias.ocean.module.admin.command.admin.subcommand.OReloadSubcommand;
import net.oceanias.ocean.plugin.OPlugin;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@RequiredArgsConstructor
public final class OAdminCommand extends OCommand {
    private final Ocean plugin;

    @Override
    protected OPlugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull String getLabel() {
        return Ocean.get().getLabel();
    }

    @Contract(" -> new")
    public @NotNull @Unmodifiable List<OSubcommand> getSubcommands() {
        return List.of(
            new OReloadSubcommand(plugin, this)
        );
    }
}