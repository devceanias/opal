package net.oceanias.opal.module.admin.command.admin;

import net.oceanias.opal.Opal;
import net.oceanias.opal.command.OCommand;
import net.oceanias.opal.command.OSubcommand;
import net.oceanias.opal.module.admin.command.admin.subcommand.OReloadSubcommand;
import net.oceanias.opal.plugin.OPlugin;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@RequiredArgsConstructor
public final class OAdminCommand extends OCommand {
    private final Opal plugin;

    @Override
    protected OPlugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull String getLabel() {
        return plugin.getLabel();
    }

    @Contract(" -> new")
    public @NotNull @Unmodifiable List<OSubcommand> getSubcommands() {
        return List.of(
            new OReloadSubcommand(plugin, this)
        );
    }
}