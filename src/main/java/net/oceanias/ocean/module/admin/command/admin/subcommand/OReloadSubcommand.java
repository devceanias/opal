package net.oceanias.ocean.module.admin.command.admin.subcommand;

import net.oceanias.ocean.Ocean;
import net.oceanias.ocean.command.OCommand;
import net.oceanias.ocean.command.OSubcommand;
import net.oceanias.ocean.configuration.OConfiguration;
import net.oceanias.ocean.plugin.OPlugin;
import net.oceanias.ocean.utility.extension.OPlayerExtension;
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
@ExtensionMethod(OPlayerExtension.class)
@RequiredArgsConstructor
public final class OReloadSubcommand extends OSubcommand {
    private final Ocean plugin;
    private final OCommand parent;

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
        final String[] configurations = Ocean.get().getConfigurations().stream()
            .map(OConfiguration::getLabel)
            .toArray(String[]::new);

        return getBase()
            .executesPlayer((player, arguments) -> {
                for (final OConfiguration<?> configuration : Ocean.get().getConfigurations()) {
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

                        final OConfiguration<?> configuration = Ocean.get().getConfigurations().stream()
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