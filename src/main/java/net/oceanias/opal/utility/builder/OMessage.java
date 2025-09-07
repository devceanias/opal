package net.oceanias.opal.utility.builder;

import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
@Accessors(fluent = true)
@ExtensionMethod(OStringExtension.class)
@Builder
public final class OMessage {
    private final OPlugin plugin;

    @Singular("line")
    private List<String> lines;

    @Builder.Default
    private boolean prefix = true;

    @Builder.Default
    private boolean dividers = false;

    @Builder.Default
    private boolean blanks = false;

    public @NotNull Component component() {
        final String addDividers = dividers ? OStringExtension.CHAT_DIVIDER_LONG : null;
        final String addBlanks = blanks ? "" : null;
        final String addPrefix = prefix ? plugin.getPrefix() : "";
        final String joinedText = lines != null ? String.join("\n", lines) : "";

        return Stream.of(addDividers, addBlanks, addPrefix + " " + joinedText, addBlanks, addDividers)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"))
            .deserialize();
    }

    @Contract("_ -> this")
    public OMessage send(final @NotNull CommandSender sender) {
        sender.sendMessage(component());

        return this;
    }

    @Contract("_ -> this")
    public OMessage send(final @NotNull Iterable<? extends CommandSender> senders) {
        final Component message = component();

        for (final CommandSender sender : senders) {
            sender.sendMessage(message);
        }

        return this;
    }
}