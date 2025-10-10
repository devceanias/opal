package net.oceanias.opal.utility.builder;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.constant.OFeedbackSound;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import net.oceanias.opal.utility.extension.OStringExtension;
import net.oceanias.opal.utility.helper.OTextHelper;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Sound;
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
@Builder
@ExtensionMethod({ OStringExtension.class, OCommandSenderExtension.class })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OMessage {
    @Singular
    private final List<String> lines;

    @Setter
    @Builder.Default
    private boolean prefix = true;

    @Setter
    @Builder.Default
    private boolean dividers = false;

    @Setter
    @Builder.Default
    private boolean blanks = false;

    private Sound sound;

    public @NotNull Component component() {
        final String addDividers = dividers ? OTextHelper.CHAT_DIVIDER_LONG : null;
        final String addBlanks = blanks ? "" : null;
        final String addPrefix = prefix ? OPlugin.get().getPrefix() + " ": "";
        final String joinedText = lines != null ? String.join("\n", lines) : "";

        return Stream.of(addDividers, addBlanks, addPrefix + joinedText, addBlanks, addDividers)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"))
            .deserialize();
    }

    public void send(final @NotNull CommandSender sender) {
        sender.sendMessage(component());

        if (sound == null) {
            return;
        }

        sender.soundDSR(sound);
    }

    public void send(final @NotNull Iterable<? extends CommandSender> senders) {
        final Component message = component();

        for (final CommandSender sender : senders) {
            sender.sendMessage(message);

            if (sound == null) {
                continue;
            }

            sender.soundDSR(sound);
        }
    }

    public void broadcast() {
        final Component message = component();

        for (final Player sender : OPlugin.get().getServer().getOnlinePlayers()) {
            sender.sendMessage(message);

            if (sound == null) {
                continue;
            }

            sender.soundDSR(sound);
        }
    }

    public static final class OMessageBuilder {
        @Contract("_ -> this")
        public OMessageBuilder sound(final @NotNull OFeedbackSound sound) {
            this.sound = sound.getDelegate();

            return this;
        }
    }
}