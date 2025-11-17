package net.oceanias.opal.utility.builder;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.extension.OStringExtension;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import java.util.List;
import java.util.stream.StreamSupport;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused" })
@Getter
@Accessors(fluent = true)
@ExtensionMethod({ OStringExtension.class })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OActionBar {
    @Setter
    private String text;

    @Setter
    private Duration persist;

    @Setter
    private Sound sound;

    private Persister persister;

    @Builder
    public OActionBar(final String text, final Duration persist, final Sound sound) {
        this.text = text;
        this.persist = persist;
        this.sound = sound;
    }

    public void show(@NotNull final CommandSender sender) {
        if (text == null) {
            return;
        }

        if (persist != null) {
            if (persister != null) {
                persister.cancel();
            }

            if (sender instanceof final Player player) {
                persister = new Persister(List.of(player), persist);

                OTaskHelper.runTaskTimer(persister, Duration.ZERO, Duration.ofSeconds(1));
            }

            return;
        }

        sender.sendActionBar(text.deserialize());

        if (sound == null) {
            return;
        }

        OSound.builder().sound(sound).build().play(sender);
    }

    public void show(final @NotNull Iterable<? extends CommandSender> senders) {
        if (text == null) {
            return;
        }

        if (persist != null) {
            final List<Player> players = StreamSupport.stream(senders.spliterator(), false)
                .filter(sender -> sender instanceof Player)
                .map(sender -> (Player) sender)
                .toList();

            if (persister != null) {
                persister.cancel();
            }

            if (players.isEmpty()) {
                return;
            }

            persister = new Persister(players, persist);

            OTaskHelper.runTaskTimer(persister, Duration.ZERO, Duration.ofSeconds(1));

            return;
        }

        final OSound sound = this.sound != null
            ? OSound.builder().sound(this.sound).build()
            : null;

        for (final CommandSender player : senders) {
            show(player);

            if (sound == null) {
                continue;
            }

            sound.play(player);
        }
    }

    public void broadcast() {
        show(OPlugin.get().getServer().getOnlinePlayers());
    }

    @AllArgsConstructor
    public final class Persister extends BukkitRunnable {
        private final List<Player> players;

        private Duration remaining;
        private Component cached;

        public Persister(final List<Player> players, final Duration remaining) {
            this.players = players;
            this.remaining = remaining;

            cached = text.deserialize();
        }

        @Override
        public void run() {
            if (remaining.isZero() || remaining.isNegative()) {
                cancel();

                return;
            }

            final OSound sound = OActionBar.this.sound != null
                ? OSound.builder().sound(OActionBar.this.sound).build()
                : null;

            for (final Player player : players) {
                player.sendActionBar(cached);

                if (sound == null) {
                    continue;
                }

                sound.play(player);
            }

            remaining = remaining.minusSeconds(1);
        }
    }

    public static final class OActionBarBuilder {
        @Contract("_ -> this")
        public OActionBarBuilder sound(final @NotNull OSound.Preset preset) {
            sound = preset.getDelegate();

            return this;
        }

        public OActionBarBuilder sound(final @NotNull Sound sound) {
            this.sound = sound;

            return this;
        }
    }
}