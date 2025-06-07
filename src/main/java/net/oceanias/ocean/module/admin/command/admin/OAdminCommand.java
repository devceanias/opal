package net.oceanias.ocean.module.admin.command.admin;

import net.oceanias.ocean.Ocean;
import net.oceanias.ocean.command.OCommand;
import net.oceanias.ocean.command.OSubcommand;
import net.oceanias.ocean.module.admin.command.admin.subcommand.OReloadSubcommand;
import net.oceanias.ocean.plugin.OPlugin;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class OAdminCommand extends OCommand {
    public OAdminCommand(final OPlugin plugin) {
        super(plugin);
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