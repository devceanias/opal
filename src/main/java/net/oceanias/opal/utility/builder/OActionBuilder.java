package net.oceanias.opal.utility.builder;

import net.oceanias.opal.utility.extension.OStringExtension;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
@Getter
@Accessors(chain = true)
@ExtensionMethod(OStringExtension.class)
public class OActionBuilder {
    @Setter
    private String text;

    @Setter
    private Duration duration;

    private PersistenceTask persistenceTask;

    public OActionBuilder(@NotNull final String text) {
        this.text = text;
    }

    public OActionBuilder showText(@NotNull final CommandSender sender) {
        sender.sendActionBar(text.deserialize());

        return this;
    }

    public OActionBuilder clearText(@NotNull final CommandSender sender) {
        sender.sendActionBar(Component.empty());

        return this;
    }

    public OActionBuilder showPersistent(final CommandSender sender, final Duration duration) {
        if (persistenceTask != null) {
            persistenceTask.cancel();
        }

        if (sender instanceof final Player player) {
            this.duration = duration;

            persistenceTask = new PersistenceTask(player, duration);

            OTaskHelper.runTaskTimer(persistenceTask, Duration.ZERO, Duration.ofSeconds(1));
        }

        return this;
    }

    public OActionBuilder cancelText() {
        if (persistenceTask != null) {
            persistenceTask.cancel();
            persistenceTask = null;
        }

        return this;
    }

    @RequiredArgsConstructor
    public final class PersistenceTask extends BukkitRunnable {
        private final Player player;
        private Duration remaining;

        public PersistenceTask(final Player player, final Duration duration) {
            this.player = player;
            this.remaining = duration;
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                cancel();

                return;
            }

            if (remaining.isZero() || remaining.isNegative()) {
                cancel();

                return;
            }

            showText(player);

            remaining = remaining.minusSeconds(1);
        }

        @Override
        public void cancel() {
            super.cancel();

            clearText(player);
        }
    }
}