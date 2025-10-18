package net.oceanias.opal.utility.helper;

import net.oceanias.opal.listener.OListener;
import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OCommandSenderExtension.class })
@UtilityClass
public final class OTeleportHelper {
    private final Map<UUID, List<BukkitTask>> PENDING = new ConcurrentHashMap<>();
    private final Map<UUID, Location> LOCATIONS = new ConcurrentHashMap<>();

    public void createTeleportTimer(
        final @NotNull Player player, final Location destination, final Duration duration
    ) {
        final UUID uuid = player.getUniqueId();

        if (PENDING.containsKey(uuid)) {
            player.actionDSR("&fA &6teleportation &fis &calready &fpending.");

            return;
        }

        player.closeInventory();

        LOCATIONS.put(uuid, player.getLocation());

        final long seconds = duration.getSeconds();
        final List<BukkitTask> tasks = new ArrayList<>();

        for (int index = 0; index < seconds; index++) {
            final int remaining = (int) (seconds - index);

            final BukkitTask task = OTaskHelper.runTaskLater(() -> {
                player.actionDSR(
                    "&fTeleporting in " + getSecondsColour(remaining, seconds) + remaining + " seconds&f."
                );

                player.soundDSR(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            }, index * 20L);

            tasks.add(task);
        }

        final BukkitTask teleportation = OTaskHelper.runTaskLater(() -> {
            if (!PENDING.containsKey(uuid)) {
                return;
            }

            player.teleport(destination);

            player.actionDSR(
                OPlugin.get().getColour() +
                "&fThe &6teleportation &fhas &acommenced&f."
            );

            player.soundDSR(Sound.ENTITY_ENDERMAN_TELEPORT);

            PENDING.remove(uuid);
        }, duration);

        tasks.add(teleportation);

        PENDING.put(uuid, tasks);
    }

    public void cancelTeleportTimer(@NotNull final Player player) {
        final List<BukkitTask> tasks = PENDING.remove(player.getUniqueId());

        if (tasks != null) {
            tasks.forEach(BukkitTask::cancel);

            player.actionDSR("&fThe &6teleportation &fhas been &ccancelled&f.");
            player.soundDSR(Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
        }

        LOCATIONS.remove(player.getUniqueId());
    }

    @Contract(pure = true)
    private @NotNull String getSecondsColour(final double remaining, final double original) {
        final double percentage = (remaining / original) * 100;

        if (percentage > 67) {
            return "&c";
        }

        if (percentage > 34) {
            return "&6";
        }

        if (percentage > 0) {
            return "&a";
        }

        return "&e";
    }

    public static final class Listener extends OListener.Bukkit {
        @EventHandler
        public void onPlayerMove(@NotNull final PlayerMoveEvent event) {
            final Location from = event.getFrom();
            final Location to = event.getTo();

            if (!OLocationHelper.hasMovedExact(from, to)) {
                return;
            }

            final Player player = event.getPlayer();
            final UUID uuid = player.getUniqueId();

            if (!PENDING.containsKey(uuid)) {
                return;
            }

            final Location location = LOCATIONS.get(uuid);

            if (location == null) {
                return;
            }

            if (!OLocationHelper.hasMovedExact(location, to)) {
                return;
            }

            cancelTeleportTimer(player);
        }

        @EventHandler
        public void onEntityDamage(@NotNull final EntityDamageEvent event) {
            if (!(event.getEntity() instanceof final Player player)) {
                return;
            }

            if (!PENDING.containsKey(player.getUniqueId())) {
                return;
            }

            cancelTeleportTimer(player);
        }

        @EventHandler
        public void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
            final Player player = event.getPlayer();

            if (!PENDING.containsKey(player.getUniqueId())) {
                return;
            }

            cancelTeleportTimer(player);
        }
    }
}