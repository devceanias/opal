package net.oceanias.opal.utility.helper;

import net.oceanias.opal.Opal;
import net.oceanias.opal.listener.OListener;
import net.oceanias.opal.plugin.OPlugin;
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
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OCommandSenderExtension.class })
@RequiredArgsConstructor
public final class OTeleportHelper extends OListener.Bukkit {
    private static final Map<UUID, List<BukkitTask>> pending = new ConcurrentHashMap<>();
    private static final Map<UUID, Location> locations = new ConcurrentHashMap<>();

    private final Opal plugin;

    @Override
    protected OPlugin getPlugin() {
        return plugin;
    }

    public static void createTeleportTimer(
        final @NotNull Player player, final Location destination, final Duration duration
    ) {
        final UUID uuid = player.getUniqueId();

        if (pending.containsKey(uuid)) {
            player.actionDSR("&fA &6teleportation &fis &calready &fpending.");

            return;
        }

        player.closeInventory();

        locations.put(uuid, player.getLocation());

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
            if (!pending.containsKey(uuid)) {
                return;
            }

            player.teleport(destination);

            player.actionDSR(
                Opal.get().getColour() +
                "&fThe &6teleportation &fhas &acommenced&f."
            );

            player.soundDSR(Sound.ENTITY_ENDERMAN_TELEPORT);

            pending.remove(uuid);
        }, duration);

        tasks.add(teleportation);

        pending.put(uuid, tasks);
    }

    public static void cancelTeleportTimer(@NotNull final Player player) {
        final List<BukkitTask> tasks = pending.remove(player.getUniqueId());

        if (tasks != null) {
            tasks.forEach(BukkitTask::cancel);

            player.actionDSR("&fThe &6teleportation &fhas been &ccancelled&f.");
            player.soundDSR(Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
        }

        locations.remove(player.getUniqueId());
    }

    @Contract(pure = true)
    private static @NotNull String getSecondsColour(final double remaining, final double original) {
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

    @EventHandler
    public void onPlayerMove(@NotNull final PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (!OLocationHelper.hasMovedExact(from, to)) {
            return;
        }

        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        if (!pending.containsKey(uuid)) {
            return;
        }

        final Location location = locations.get(uuid);

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

        if (!pending.containsKey(player.getUniqueId())) {
            return;
        }

        cancelTeleportTimer(player);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (!pending.containsKey(player.getUniqueId())) {
            return;
        }

        cancelTeleportTimer(player);
    }
}