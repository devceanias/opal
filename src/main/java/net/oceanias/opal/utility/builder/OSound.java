package net.oceanias.opal.utility.builder;

import net.oceanias.opal.OPlugin;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
@Accessors(fluent = true)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OSound {
    @Setter
    private Sound sound;

    @Setter
    @Builder.Default
    private float volume = 1.0f;

    @Setter
    @Builder.Default
    private float pitch = 1.0f;

    @Setter
    private Location location;

    @Setter
    @Builder.Default
    private SoundCategory category = SoundCategory.MASTER;

    public void play(final @NotNull CommandSender sender) {
        if (!(sender instanceof final Player player)) {
            return;
        }

        final Location effective = location != null
            ? location
            : player.getLocation();

        player.playSound(effective, sound, category, volume, pitch);
    }

    public void play(final @NotNull Iterable<? extends CommandSender> players) {
        for (final CommandSender player : players) {
            play(player);
        }
    }

    public void broadcast() {
        play(OPlugin.get().getServer().getOnlinePlayers());
    }

    public static final class OSoundBuilder {
        @Contract("_ -> this")
        public OSoundBuilder sound(final @NotNull OSound.Preset sound) {
            this.sound = sound.getDelegate();

            return this;
        }
    }

    @Getter
    @Accessors(fluent = false)
    @RequiredArgsConstructor
    public enum Preset {
        SUCCESS(Sound.BLOCK_NOTE_BLOCK_BELL),
        ERROR(Sound.BLOCK_NOTE_BLOCK_BASS),
        CLICK(Sound.UI_BUTTON_CLICK),
        OPEN(Sound.BLOCK_BASALT_BREAK);

        private final Sound delegate;
    }
}