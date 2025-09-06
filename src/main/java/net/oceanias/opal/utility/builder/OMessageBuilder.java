package net.oceanias.opal.utility.builder;

import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
@Setter
@Accessors(chain = true)
@ExtensionMethod(OStringExtension.class)
public final class OMessageBuilder {
    private final OPlugin plugin;

    private String text;

    private boolean prefixed = true;
    private boolean lines = false;
    private boolean blanks = false;

    public OMessageBuilder(final OPlugin plugin, @NotNull final String message) {
        this.plugin = plugin;
        this.text = message;
    }

    public OMessageBuilder(final OPlugin plugin, @NotNull final List<String> lines) {
        this.plugin = plugin;
        this.text = String.join("\n", lines);
    }

    public @NotNull Component getComponent() {
        final String divider = lines ? OStringExtension.CHAT_DIVIDER_LONG : null;
        final String blank = blanks ? "" : null;
        final String prefix = prefixed ? plugin.getPrefix() : "";

        return Stream.of(divider, blank, prefix + text, blank, divider)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"))
            .deserialize();
    }

    @Contract("_ -> this")
    public OMessageBuilder sendMessage(final @NotNull CommandSender sender) {
        sender.sendMessage(getComponent());

        return this;
    }

    @Contract("_ -> this")
    public OMessageBuilder sendMessage(final @NotNull Iterable<CommandSender> senders) {
        final Component message = getComponent();

        for (final CommandSender sender : senders) {
            sender.sendMessage(message);
        }

        return this;
    }
}