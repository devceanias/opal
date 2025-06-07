package net.oceanias.ocean.utility.extension;

import net.oceanias.ocean.plugin.ORegistry;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OStringExtension.class })
public final class OPlayerExtension {
    public static void messageDSR(
        @NotNull final Player player,
        final String message,
        final boolean prefixed,
        final boolean lines,
        final boolean blanks
    ) {
        final String line = lines ? OStringExtension.CHAT_DIVIDER_SHORT : null;
        final String blank = blanks ? "" : null;
        final String beginning = prefixed ? ORegistry.getCaller().getPrefix() : "";

        player.sendMessage(Stream.of(line, blank, beginning + message, blank, line)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"))
            .deserialize()
        );
    }

    public static void messageDSR(
        final Player player, final String message, final boolean prefixed, final boolean lines
    ) {
        messageDSR(player, message, prefixed, lines, false);
    }

    public static void messageDSR(final Player player, final String message, final boolean prefixed) {
        messageDSR(player, message, prefixed, false, false);
    }

    public static void messageDSR(final Player player, final String message) {
        messageDSR(player, message, true, false, false);
    }

    public static void messageDSR(final Player player) {
        messageDSR(player, "", true, false, false);
    }

    public static void actionDSR(@NotNull final Player player, @NotNull final String text) {
        player.sendActionBar(text.deserialize());
    }

    public static void soundDSR(
        @NotNull final Player player,
        @NotNull final org.bukkit.Sound sound,
        final float volume,
        final float pitch,
        final Sound.Emitter emitter,
        final Sound.Source source
    ) {
        player.playSound(Sound.sound(
            Objects.requireNonNull(Registry.SOUNDS.getKey(sound)), source, volume, pitch), emitter
        );
    }

    public static void soundDSR(
        final Player player,
        final org.bukkit.Sound sound,
        final float volume,
        final float pitch,
        final Sound.Emitter emitter
    ) {
        soundDSR(player, sound, volume, pitch, emitter, Sound.Source.MASTER);
    }

    public static void soundDSR(
        final Player player, final org.bukkit.Sound sound, final float volume, final float pitch
    ) {
        soundDSR(player, sound, volume, pitch, Sound.Emitter.self(), Sound.Source.MASTER);
    }

    public static void soundDSR(final Player player, final org.bukkit.Sound sound, final float volume) {
        soundDSR(player, sound, volume, 1f, Sound.Emitter.self(), Sound.Source.MASTER);
    }

    public static void soundDSR(final Player player, final org.bukkit.Sound sound) {
        soundDSR(player, sound, 1f, 1f, Sound.Emitter.self(), Sound.Source.MASTER);
    }

    public static void titleDSR(
        @NotNull final Player player,
        @NotNull final String title,
        @NotNull final String subtitle,
        final long fadeIn,
        final long onScreen,
        final long fadeOut
    ) {
        final Title.Times times = Title.Times.times(
            Duration.ofMillis(fadeIn),
            Duration.ofMillis(onScreen),
            Duration.ofMillis(fadeOut)
        );

        player.showTitle(Title.title(title.deserialize(), subtitle.deserialize(), times));
    }

    public static void titleDSR(
        final Player player, final String title, final String subtitle, final long fadeIn, final long onScreen
    ) {
        titleDSR(player, title, subtitle, fadeIn, onScreen, 1000);
    }

    public static void titleDSR(final Player player, final String title, final String subtitle, final long fadeIn) {
        titleDSR(player, title, subtitle, fadeIn, 3000, 1000);
    }

    public static void titleDSR(final Player player, final String title, final String subtitle) {
        titleDSR(player, title, subtitle, 1000, 3000, 1000);
    }
}