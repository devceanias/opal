package net.oceanias.opal.module.script.command.script;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.command.OCommand;
import net.oceanias.opal.command.OSubcommand;
import net.oceanias.opal.configuration.impl.OScriptConfig;
import net.oceanias.opal.module.script.command.script.sub.ODisableSubcommand;
import net.oceanias.opal.module.script.command.script.sub.OEnableSubcommand;
import net.oceanias.opal.module.script.command.script.sub.OReloadSubcommand;
import java.util.List;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import dev.jorel.commandapi.CommandTree;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public final class OScriptCommand extends OCommand {
    @Override
    public @NotNull String getLabel() {
        return OPlugin.get().getLabel() + "-script";
    }

    @Contract(pure = true)
    public @NotNull @Unmodifiable List<OSubcommand> getSubcommands() {
        return List.of(
            new ODisableSubcommand(this),
            new OEnableSubcommand(this),
            new OReloadSubcommand(this)
        );
    }

    @Override
    public CommandTree getCommand() {
        return getBase().withRequirement(sender -> {
            if (sender instanceof ConsoleCommandSender) {
                return true;
            }

            if (sender instanceof final Player player) {
                final boolean permission = player.hasPermission(
                    OScriptConfig.get().command().requirements().permission()
                );

                final boolean uuid =
                    OScriptConfig.get().command().requirements().uuids().contains(player.getUniqueId());

                return permission && uuid;
            }

            return false;
        });
    }
}