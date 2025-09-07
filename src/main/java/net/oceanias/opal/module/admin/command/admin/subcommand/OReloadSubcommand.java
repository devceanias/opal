package net.oceanias.opal.module.admin.command.admin.subcommand;

import net.oceanias.opal.Opal;
import net.oceanias.opal.command.OCommand;
import net.oceanias.opal.command.OSubcommand;
import net.oceanias.opal.configuration.OConfiguration;
import net.oceanias.opal.module.admin.command.admin.OAdminCommand;
import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import java.util.List;
import java.util.Objects;
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
            .executes((sender, arguments) -> {
                for (final OConfiguration<?> configuration : Opal.get().getConfigurations()) {
                    configuration.loadConfiguration();
                }

                sender.actionDSR(
                    "&fAll &6" +
                    plugin.getDescription().getName() +
                    " configurations &fhave been &areloaded&f."
                );

                sender.soundDSR(Sound.BLOCK_NOTE_BLOCK_BELL);
            })
            .thenNested(
                new MultiLiteralArgument("type", configurations)
                    .executes((sender, arguments) -> {
                        final String type = (String) Objects.requireNonNull(arguments.get("type"));

                        final OConfiguration<?> configuration = Opal.get().getConfigurations().stream()
                            .filter(entry -> entry.getLabel().equals(type))
                            .findFirst()
                            .orElseThrow(() ->
                                new IllegalArgumentException("Error finding configuration for type " + type + ".")
                            );

                        configuration.loadConfiguration();

                        sender.actionDSR(
                            "&fThe &6" +
                            configuration.getLabel() +
                            " configuration &fhas been &areloaded&f."
                        );

                        sender.soundDSR(Sound.BLOCK_NOTE_BLOCK_BELL);
                    })
            );
    }
}