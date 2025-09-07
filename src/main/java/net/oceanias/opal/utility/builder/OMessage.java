package net.oceanias.opal.utility.builder;

import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
@Accessors(fluent = true)
@ExtensionMethod(OStringExtension.class)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OMessage {
    private final OPlugin plugin;

    private final List<String> lines;

    @Setter
    private boolean prefix;

    @Setter
    private boolean dividers;

    @Setter
    private boolean blanks;

    public OMessage line(final String line) {
        lines.add(line);

        return this;
    }

    public OMessage lines(final List<String> lines) {
        this.lines.addAll(lines);

        return this;
    }

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

    public void send(final @NotNull CommandSender sender) {
        sender.sendMessage(component());
    }

    public void send(final @NotNull Iterable<? extends CommandSender> senders) {
        final Component message = component();

        for (final CommandSender sender : senders) {
            sender.sendMessage(message);
        }
    }

    public void broadcast() {
        for (final Player sender : plugin.getServer().getOnlinePlayers()) {
            send(sender);
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull OMessageBuilder builder(final OPlugin plugin) {
        return new OMessageBuilder(plugin);
    }

    @Getter
    public static class OMessageBuilder {
        private final OPlugin plugin;

        private final List<String> lines = new ArrayList<>();

        @Setter
        private boolean prefix = true;

        @Setter
        private boolean dividers = false;

        @Setter
        private boolean blanks = false;

        public OMessageBuilder(final OPlugin plugin) {
            this.plugin = plugin;
        }

        public OMessageBuilder line(final String line) {
            lines.add(line);

            return this;
        }

        public OMessageBuilder lines(final List<String> lines) {
            this.lines.addAll(lines);

            return this;
        }

        public OMessage build() {
            return new OMessage(plugin, lines, prefix, dividers, blanks);
        }
    }
}