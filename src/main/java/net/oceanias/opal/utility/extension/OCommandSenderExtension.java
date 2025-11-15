package net.oceanias.opal.utility.extension;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.helper.OTextHelper;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

// TODO: Add title builder and deprecate remaining methods.
@SuppressWarnings("unused")
@ExtensionMethod({ OStringExtension.class })
@UtilityClass
public final class OCommandSenderExtension {
    @Deprecated
    public void messageDSR(
        @NotNull final CommandSender sender,
        final String message,
        final boolean prefixed,
        final boolean lines,
        final boolean blanks
    ) {
        final String line = lines ? OTextHelper.CHAT_DIVIDER_LONG : null;
        final String blank = blanks ? "" : null;
        final String beginning = prefixed ? OPlugin.get().getPrefix() : "";

        sender.sendMessage(Stream.of(line, blank, beginning + message, blank, line)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n"))
            .deserialize()
        );
    }

    @Deprecated
    public void messageDSR(
        final CommandSender sender, final String message, final boolean prefixed, final boolean lines
    ) {
        messageDSR(sender, message, prefixed, lines, false);
    }

    @Deprecated
    public void messageDSR(final CommandSender sender, final String message, final boolean prefixed) {
        messageDSR(sender, message, prefixed, false, false);
    }

    @Deprecated
    public void messageDSR(final CommandSender sender, final String message) {
        messageDSR(sender, message, true, false, false);
    }

    @Deprecated
    public void messageDSR(final CommandSender sender) {
        messageDSR(sender, "", true, false, false);
    }

    @Deprecated
    public void actionDSR(@NotNull final CommandSender sender, @NotNull final String text) {
        sender.sendActionBar(text.deserialize());
    }

    @Deprecated
    public void soundDSR(
        @NotNull final CommandSender sender,
        @NotNull final org.bukkit.Sound sound,
        final float volume,
        final float pitch,
        final Sound.Emitter emitter,
        final Sound.Source source
    ) {
        sender.playSound(Sound.sound(
            Objects.requireNonNull(Registry.SOUNDS.getKey(sound)), source, volume, pitch), emitter
        );
    }

    @Deprecated
    public void soundDSR(
        final CommandSender sender,
        final org.bukkit.Sound sound,
        final float volume,
        final float pitch,
        final Sound.Emitter emitter
    ) {
        soundDSR(sender, sound, volume, pitch, emitter, Sound.Source.MASTER);
    }

    @Deprecated
    public void soundDSR(
        final CommandSender sender, final org.bukkit.Sound sound, final float volume, final float pitch
    ) {
        soundDSR(sender, sound, volume, pitch, Sound.Emitter.self(), Sound.Source.MASTER);
    }

    @Deprecated
    public void soundDSR(final CommandSender sender, final org.bukkit.Sound sound, final float volume) {
        soundDSR(sender, sound, volume, 1f, Sound.Emitter.self(), Sound.Source.MASTER);
    }

    @Deprecated
    public void soundDSR(final CommandSender sender, final org.bukkit.Sound sound) {
        soundDSR(sender, sound, 1f, 1f, Sound.Emitter.self(), Sound.Source.MASTER);
    }

    public void titleDSR(
        @NotNull final CommandSender sender,
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

        sender.showTitle(Title.title(title.deserialize(), subtitle.deserialize(), times));
    }

    public void titleDSR(
        final CommandSender sender, final String title, final String subtitle, final long fadeIn, final long onScreen
    ) {
        titleDSR(sender, title, subtitle, fadeIn, onScreen, 1000);
    }

    public void titleDSR(final CommandSender sender, final String title, final String subtitle, final long fadeIn) {
        titleDSR(sender, title, subtitle, fadeIn, 3000, 1000);
    }

    public void titleDSR(final CommandSender sender, final String title, final String subtitle) {
        titleDSR(sender, title, subtitle, 1000, 3000, 1000);
    }
}