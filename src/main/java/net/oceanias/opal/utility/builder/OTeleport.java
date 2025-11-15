package net.oceanias.opal.utility.builder;

import net.oceanias.opal.listener.OListener;
import net.oceanias.opal.utility.helper.OLocationHelper;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
@Accessors(fluent = true)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OTeleport {
    private static final Map<UUID, BukkitTask> COUNTDOWNS = new ConcurrentHashMap<>();

    @Setter
    private Player player;

    @Setter
    private Location destination;

    @Setter
    private Duration duration;

    @Setter
    @Builder.Default
    private boolean cancelOnMove = true;

    @Setter
    @Builder.Default
    private boolean cancelOnDamage = true;

    @Builder.Default
    private boolean closeOpenInventory = true;

    public void start() {
        final UUID uuid = player.getUniqueId();

        if (COUNTDOWNS.containsKey(uuid)) {
            OMessage.builder()
                .line("&fYou &ccannot use this &fright now!")
                .sound(OSound.Preset.ERROR)
                .build()
                .send(player);

            return;
        }

        if (closeOpenInventory) {
            player.closeInventory();
        }

        COUNTDOWNS.put(
            uuid,
            OTaskHelper.runTaskTimer(new Countdown((int) duration.getSeconds()), Duration.ZERO, Duration.ofSeconds(1))
        );
    }

    public void cancel() {
        cancelTeleportTask(player);
    }

    private static void cancelTeleportTask(final @NotNull Player player) {
        final UUID uuid = player.getUniqueId();
        final BukkitTask task = COUNTDOWNS.remove(uuid);

        if (task == null) {
            return;
        }

        task.cancel();
    }

    @AllArgsConstructor
    private final class Countdown extends BukkitRunnable {
        // Seconds
        private final int initial;

        // Seconds
        private int remaining;

        public Countdown(final int initial) {
            this.initial = initial;

            remaining = initial;
        }

        @Override
        public void run() {
            if (remaining <= 0) {
                OTeleport.this.cancel();

                player.teleportAsync(destination).thenAccept(success -> {
                    OTaskHelper.runTask(() -> {
                        if (!success) {
                            OActionBar.builder()
                                .text("&fThe teleportation has &cfailed&f!")
                                .sound(OSound.Preset.ERROR)
                                .build()
                                .show(player);

                            return;
                        }

                        OActionBar.builder()
                            .text("&fYou have been &ateleported&f.")
                            .sound(Sound.ENTITY_ENDERMAN_TELEPORT)
                            .build()
                            .show(player);
                        });
                });

                return;
            }

            OActionBar.builder()
                .text("&fTeleporting in " + getSecondsColour(initial, remaining) + " seconds&f!")
                .sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP)
                .build()
                .show(player);

            remaining--;
        }

        private static @NotNull String getSecondsColour(final double initial, final double remaining) {
            if (initial <= 0) {
                return "&b";
            }

            final double fraction = remaining / initial;

            if (fraction > 0.67) {
                return "&c";
            }

            if (fraction > 0.34) {
                return "&6";
            }

            if (fraction > 0.0) {
                return "&a";
            }

            return "&e";
        }
    }

    public static final class Listener extends OListener.Bukkit {
        @EventHandler
        public void onPlayerMove(@NotNull final PlayerMoveEvent event) {
            if (!OLocationHelper.hasMovedExact(event.getFrom(), event.getTo())) {
                return;
            }

            OTeleport.cancelTeleportTask(event.getPlayer());
        }

        @EventHandler
        public void onEntityDamage(@NotNull final EntityDamageEvent event) {
            if (!(event.getEntity() instanceof final Player player)) {
                return;
            }

            OTeleport.cancelTeleportTask(player);
        }

        @EventHandler
        public void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
            OTeleport.cancelTeleportTask(event.getPlayer());
        }
    }
}