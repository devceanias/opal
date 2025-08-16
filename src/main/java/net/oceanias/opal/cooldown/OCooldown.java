package net.oceanias.opal.cooldown;

import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@ExtensionMethod(OCommandSenderExtension.class)
public final class OCooldown {
    private final OPlugin plugin;
    private final String label;
    private final Duration length;

    private final Map<String, Pair<Long, Duration>> cooldowns = new ConcurrentHashMap<>();

    private @NotNull String getIdentifier(final CommandSender sender) {
        if (sender instanceof final Player player) {
            return "player:" + player.getUniqueId();
        }

        if (sender instanceof ConsoleCommandSender) {
            return "console";
        }

        return sender.getClass().getSimpleName().toLowerCase() + ":" + sender.getName();
    }

    private String getBypass() {
        return plugin.getPermission("cooldown." + label.replace(" ", ".") + ".bypass");
    }

    public void setCooldown(@NotNull final CommandSender sender, final boolean cooldown) {
        final String identifier = getIdentifier(sender);

        if (!cooldown) {
            cooldowns.remove(identifier);

            return;
        }

        if (sender.hasPermission(getBypass())) {
            return;
        }

        cooldowns.put(identifier, Pair.of(System.currentTimeMillis(), length));

        OTaskHelper.runTaskLaterAsync(() ->
            cooldowns.remove(identifier), length
        );
    }

    public void showReminder(@NotNull final CommandSender sender) {
        final String message = "&fPlease wait &#FFA500" + formatDuration(getRemaining(getIdentifier(sender))) + "&f.";

        if (sender instanceof ConsoleCommandSender) {
            sender.messageDSR(message);

            return;
        }

        sender.actionDSR(message);
        sender.soundDSR(Sound.BLOCK_NOTE_BLOCK_BASS);
    }

    public Duration getRemaining(@NotNull final String identifier) {
        final Pair<Long, Duration> pair = cooldowns.get(identifier);

        if (pair == null) {
            return Duration.ZERO;
        }

        final long start = pair.getLeft();
        final Duration duration = pair.getRight();
        final Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - start);
        final Duration remaining = duration.minus(elapsed);

        if (remaining.isNegative() || remaining.isZero()) {
            cooldowns.remove(identifier);

            return Duration.ZERO;
        }

        return remaining;
    }

    public boolean isActive(final @NotNull CommandSender sender) {
        return cooldowns.containsKey(getIdentifier(sender));
    }

    public void withCooldown(final CommandSender sender, final Runnable execute) {
        if (!isActive(sender)) {
            execute.run();

            setCooldown(sender, true);

            return;
        }

        showReminder(sender);
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
                final String extension = (unit.getRight() == 1L)
                    ? ""
                    : "s";

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