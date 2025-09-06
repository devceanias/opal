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
@Accessors(fluent = true)
@ExtensionMethod(OStringExtension.class)
public final class OMessageBuilder {
    @Getter
    private final OPlugin plugin;

    private String text;

    @Getter
    @Setter
    private boolean prefixed = true;

    @Getter
    @Setter
    private boolean lines = false;

    @Getter
    @Setter
    private boolean blanks = false;

    public OMessageBuilder(final OPlugin plugin) {
        this.plugin = plugin;
    }

    public OMessageBuilder text(final String message) {
        this.text = message;

        return this;
    }

    public OMessageBuilder text(final List<String> lines) {
        text = String.join("\n", lines);

        return this;
    }

    public @NotNull Component build() {
        final String divider = lines ? OStringExtension.CHAT_DIVIDER_LONG : null;
        final String blank = blanks ? "" : null;
        final String prefix = prefixed ? plugin.getPrefix() : "";

        return Stream.of(divider, blank, prefix + text, blank, divider)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"))
            .deserialize();
    }

    @Contract("_ -> this")
    public OMessageBuilder send(final @NotNull CommandSender sender) {
        sender.sendMessage(build());

        return this;
    }

    @Contract("_ -> this")
    public OMessageBuilder send(final @NotNull Iterable<CommandSender> senders) {
        final Component message = build();

        for (final CommandSender sender : senders) {
            sender.sendMessage(message);
        }

        return this;
    }
}