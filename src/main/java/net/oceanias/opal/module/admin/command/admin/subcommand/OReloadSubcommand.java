package net.oceanias.opal.module.admin.command.admin.subcommand;

import net.oceanias.opal.Opal;
import net.oceanias.opal.command.OCommand;
import net.oceanias.opal.command.OSubcommand;
import net.oceanias.opal.configuration.OConfiguration;
import net.oceanias.opal.module.admin.command.admin.OAdminCommand;
import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import java.util.List;
import org.bukkit.Sound;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@SuppressWarnings("deprecation")
@ExtensionMethod(OCommandSenderExtension.class)
@RequiredArgsConstructor
public final class OReloadSubcommand extends OSubcommand {
    private final Opal plugin;
    private final OAdminCommand parent;

    @Override
    protected OPlugin getPlugin() {
        return plugin;
    }

    @Override
    public OCommand getParent() {
        return parent;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getLabel() {
        return "reload";
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> getDescription() {
        return List.of("Reloads all configurations or a specific one.");
    }

    @Override
    public Argument<String> getSubcommand() {
        final String[] configurations = Opal.get().getConfigurations().stream()
            .map(OConfiguration::getLabel)
            .toArray(String[]::new);

        return getBase()
            .executesPlayer((player, arguments) -> {
                for (final OConfiguration<?> configuration : Opal.get().getConfigurations()) {
                    configuration.loadConfiguration();
                }

                player.actionDSR(
                    "<white>All <gold>" +
                    plugin.getDescription().getName() +
                    " configurations <white>have been <green>reloaded<white>."
                );

                player.soundDSR(Sound.BLOCK_NOTE_BLOCK_BELL);
            })
            .thenNested(
                new MultiLiteralArgument("type", configurations)
                    .executesPlayer((player, arguments) -> {
                        final String type = (String) arguments.get("type");

                        final OConfiguration<?> configuration = Opal.get().getConfigurations().stream()
                            .filter(entry -> entry.getLabel().equals(type))
                            .findFirst()
                            .orElse(null);

                        if (configuration == null) {
                            return;
                        }

                        configuration.loadConfiguration();

                        player.actionDSR(
                            "<white>The <gold>" +
                            configuration.getLabel() +
                            " configuration <white>has been <green>reloaded<white>."
                        );

                        player.soundDSR(Sound.BLOCK_NOTE_BLOCK_BELL);
                    })
            );
    }
}