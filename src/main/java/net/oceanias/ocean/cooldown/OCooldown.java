package net.oceanias.ocean.cooldown;

import net.oceanias.ocean.plugin.OPlugin;
import net.oceanias.ocean.utility.extension.OPlayerExtension;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@ExtensionMethod(OPlayerExtension.class)
public final class OCooldown {
    private final OPlugin plugin;
    private final String label;
    private final Duration length;

    private final ConcurrentHashMap<UUID, Pair<Long, Duration>> cooldowns = new ConcurrentHashMap<>();

    private String getBypass() {
        return plugin.getPermission("cooldown." + label.replace(" ", ".") + ".bypass");
    }

    public void setCooldown(@NotNull final Player player, final boolean state) {
        final UUID uuid = player.getUniqueId();

        if (!state) {
            cooldowns.remove(uuid);

            return;
        }

        if (player.hasPermission(getBypass())) {
            return;
        }

        cooldowns.put(uuid, Pair.of(System.currentTimeMillis(), length));

        plugin.getScheduler().runTaskLaterAsynchronously(plugin, () -> cooldowns.remove(uuid), length.toMillis() / 50);
    }

    public void showReminder(@NotNull final Player player) {
        final Pair<Long, Duration> pair = cooldowns.get(player.getUniqueId());

        if (pair == null) {
            return;
        }

        final long start = pair.getLeft();
        final Duration duration = pair.getRight();
        final Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - start);
        final Duration remaining = duration.minus(elapsed);

        if (remaining.isNegative() || remaining.isZero()) {
            cooldowns.remove(player.getUniqueId());

            return;
        }

        player.actionDSR("&fPlease wait &6" + formatDuration(remaining) + "&f.");
        player.soundDSR(Sound.BLOCK_NOTE_BLOCK_BASS);
    }

    public boolean isActive(final @NotNull Player player) {
        return cooldowns.containsKey(player.getUniqueId());
    }

    public void withCooldown(final Player player, final Runnable execute) {
        if (!isActive(player)) {
            execute.run();

            setCooldown(player, true);

            return;
        }

        showReminder(player);
    }

    private static String formatDuration(@NotNull final Duration duration) {
        final List<Pair<String, Long>> units = List.of(
            Pair.of("day", duration.toDays()),
            Pair.of("hour", duration.toHours() % 24),
            Pair.of("minute", duration.toMinutes() % 60),
            Pair.of("second", duration.toSeconds() % 60)
        );

        final List<String> parts = units.stream()
            .filter(unit -> unit.getRight() > 0)
            .map(unit -> {
                final String extension = (unit.getRight() == 1L) ? "" : "s";

                return unit.getRight() + " " + unit.getLeft() + extension;
            })
            .toList();

        return switch (parts.size()) {
            case 0 -> "less than a second";
            case 1 -> parts.get(0);
            case 2 -> parts.get(0) + " and " + parts.get(1);
            default -> String.join(", ", parts.subList(0, parts.size() - 1)) + ", and " + parts.getLast();
        };
    }
}