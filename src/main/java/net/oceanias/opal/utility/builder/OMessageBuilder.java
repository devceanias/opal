package net.oceanias.opal.utility.builder;

import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
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

    public OMessageBuilder(final OPlugin plugin, @NotNull final String text) {
        this.plugin = plugin;
        this.text = text;
    }

    public @NotNull Component getMessage() {
        final String divider = lines ? OStringExtension.CHAT_DIVIDER_LONG : null;
        final String blank = blanks ? "" : null;
        final String prefix = prefixed ? plugin.getPrefix() : "";

        return Stream.of(divider, blank, prefix + text, blank, divider)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"))
            .deserialize();
    }

    public OMessageBuilder sendMessage(final @NotNull CommandSender sender) {
        sender.sendMessage(getMessage());

        return this;
    }
}