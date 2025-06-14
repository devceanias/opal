package net.oceanias.opal.utility.helper;

import net.oceanias.opal.plugin.ORegistry;
import java.time.Duration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class OTaskHelper {
    public static @NotNull BukkitTask runTask(final Runnable task) {
        return ORegistry.getCaller().getScheduler().runTask(ORegistry.getCaller(), task);
    }

    public static @NotNull BukkitTask runTaskAsync(final Runnable task) {
        return ORegistry.getCaller().getScheduler().runTaskAsynchronously(ORegistry.getCaller(), task);
    }

    public static @NotNull BukkitTask runTaskLater(final Runnable task, final long delayTicks) {
        return ORegistry.getCaller().getScheduler().runTaskLater(ORegistry.getCaller(), task, delayTicks);
    }

    public static @NotNull BukkitTask runTaskLater(final Runnable task, final @NotNull Duration delay) {
        return runTaskLater(task, delay.toMillis() / 50L);
    }

    public static @NotNull BukkitTask runTaskLaterAsync(final Runnable task, final long delayTicks) {
        return ORegistry.getCaller().getScheduler().runTaskLaterAsynchronously(ORegistry.getCaller(), task, delayTicks);
    }

    public static @NotNull BukkitTask runTaskLaterAsync(final Runnable task, @NotNull final Duration delay) {
        return runTaskLaterAsync(task, delay.toMillis() / 50L);
    }

    public static @NotNull BukkitTask runTaskTimer(
        final Runnable task, final long delayTicks, final long periodTicks
    ) {
        return ORegistry.getCaller().getScheduler().runTaskTimer(ORegistry.getCaller(), task, delayTicks, periodTicks);
    }

    public static @NotNull BukkitTask runTaskTimer(
        final Runnable task, final @NotNull Duration delay, final @NotNull Duration period
    ) {
        return runTaskTimer(task, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public static @NotNull BukkitTask runTaskTimerAsync(
        final Runnable task, final long delayTicks, final long periodTicks
    ) {
        return ORegistry.getCaller().getScheduler().runTaskTimerAsynchronously(ORegistry.getCaller(), task, delayTicks, periodTicks);
    }

    public static @NotNull BukkitTask runTaskTimerAsync(
        final Runnable task, final @NotNull Duration delay, final @NotNull Duration period
    ) {
        return runTaskTimerAsync(task, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public static void runTask(final @NotNull BukkitRunnable runnable) {
        runnable.runTask(ORegistry.getCaller());
    }

    public static void runTaskAsync(final @NotNull BukkitRunnable runnable) {
        runnable.runTaskAsynchronously(ORegistry.getCaller());
    }

    public static void runTaskLater(final @NotNull BukkitRunnable runnable, final long delayTicks) {
        runnable.runTaskLater(ORegistry.getCaller(), delayTicks);
    }

    public static void runTaskLater(final BukkitRunnable runnable, @NotNull final Duration delay) {
        runTaskLater(runnable, delay.toMillis() / 50L);
    }

    public static void runTaskLaterAsync(final @NotNull BukkitRunnable runnable, final long delayTicks) {
        runnable.runTaskLaterAsynchronously(ORegistry.getCaller(), delayTicks);
    }

    public static void runTaskLaterAsync(final BukkitRunnable runnable, @NotNull final Duration delay) {
        runTaskLaterAsync(runnable, delay.toMillis() / 50L);
    }

    public static void runTaskTimer(
        final @NotNull BukkitRunnable runnable, final long delayTicks, final long periodTicks
    ) {
        runnable.runTaskTimer(ORegistry.getCaller(), delayTicks, periodTicks);
    }

    public static void runTaskTimer(
        final BukkitRunnable runnable, @NotNull final Duration delay, @NotNull final Duration period
    ) {
        runTaskTimer(runnable, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public static void runTaskTimerAsync(
        final @NotNull BukkitRunnable runnable, final long delayTicks, final long periodTicks
    ) {
        runnable.runTaskTimerAsynchronously(ORegistry.getCaller(), delayTicks, periodTicks);
    }

    public static void runTaskTimerAsync(
        final BukkitRunnable runnable, @NotNull final Duration delay, @NotNull final Duration period
    ) {
        runTaskTimerAsync(runnable, delay.toMillis() / 50L, period.toMillis() / 50L);
    }
}