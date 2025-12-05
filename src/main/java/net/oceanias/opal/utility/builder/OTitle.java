package net.oceanias.opal.utility.builder;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.time.Duration;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
@Accessors(fluent = true)
@Builder
@ExtensionMethod({ OStringExtension.class })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OTitle {
    @Setter
    private String title;

    @Setter
    private String subtitle;

    @Setter
    @Builder.Default
    private Duration fadeIn = Duration.ofSeconds(1);

    @Setter
    @Builder.Default
    private Duration onScreen = Duration.ofSeconds(3);

    @Setter
    @Builder.Default
    private Duration fadeOut = Duration.ofSeconds(1);

    private Sound sound;

    public @NotNull Title render() {
        final Component upper = title != null
            ? title.deserialize()
            : Component.empty();

        final Component lower = subtitle != null
            ? subtitle.deserialize()
            : Component.empty();

        return Title.title(upper, lower, Title.Times.times(fadeIn, onScreen, fadeOut));
    }

    public void show(final @NotNull CommandSender sender) {
        sender.showTitle(render());

        if (sound == null) {
            return;
        }

        OSound.builder().sound(sound).build().play(sender);
    }

    public void show(final @NotNull Iterable<? extends CommandSender> senders) {
        final Title title = render();

        final OSound sound = this.sound != null
            ? OSound.builder().sound(this.sound).build()
            : null;

        for (final CommandSender sender : senders) {
            sender.showTitle(title);

            if (sound == null) {
                continue;
            }

            sound.play(sender);
        }
    }

    public void broadcast() {
        show(OPlugin.get().getServer().getOnlinePlayers());
    }

    public static final class OTitleBuilder {
        @Contract("_ -> this")
        public OTitleBuilder sound(final @NotNull OSound.Preset preset) {
            sound = preset.getDelegate();

            return this;
        }

        public OTitleBuilder sound(final @NotNull Sound sound) {
            this.sound = sound;

            return this;
        }
    }
}