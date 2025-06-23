package net.oceanias.opal.utility.helper;

import net.oceanias.opal.Opal;
import java.time.Duration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class OTaskHelper {
    public static @NotNull BukkitTask runTask(final Runnable task) {
        return Opal.get().getScheduler().runTask(Opal.get(), task);
    }

    public static @NotNull BukkitTask runTaskAsync(final Runnable task) {
        return Opal.get().getScheduler().runTaskAsynchronously(Opal.get(), task);
    }

    public static @NotNull BukkitTask runTaskLater(final Runnable task, final long delayTicks) {
        return Opal.get().getScheduler().runTaskLater(Opal.get(), task, delayTicks);
    }

    public static @NotNull BukkitTask runTaskLater(final Runnable task, final @NotNull Duration delay) {
        return runTaskLater(task, delay.toMillis() / 50L);
    }

    public static @NotNull BukkitTask runTaskLaterAsync(final Runnable task, final long delayTicks) {
        return Opal.get().getScheduler().runTaskLaterAsynchronously(Opal.get(), task, delayTicks);
    }

    public static @NotNull BukkitTask runTaskLaterAsync(final Runnable task, @NotNull final Duration delay) {
        return runTaskLaterAsync(task, delay.toMillis() / 50L);
    }

    public static @NotNull BukkitTask runTaskTimer(
        final Runnable task, final long delayTicks, final long periodTicks
    ) {
        return Opal.get().getScheduler().runTaskTimer(Opal.get(), task, delayTicks, periodTicks);
    }

    public static @NotNull BukkitTask runTaskTimer(
        final Runnable task, final @NotNull Duration delay, final @NotNull Duration period
    ) {
        return runTaskTimer(task, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public static @NotNull BukkitTask runTaskTimerAsync(
        final Runnable task, final long delayTicks, final long periodTicks
    ) {
        return Opal.get().getScheduler().runTaskTimerAsynchronously(Opal.get(), task, delayTicks, periodTicks);
    }

    public static @NotNull BukkitTask runTaskTimerAsync(
        final Runnable task, final @NotNull Duration delay, final @NotNull Duration period
    ) {
        return runTaskTimerAsync(task, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public static void runTask(final @NotNull BukkitRunnable runnable) {
        runnable.runTask(Opal.get());
    }

    public static void runTaskAsync(final @NotNull BukkitRunnable runnable) {
        runnable.runTaskAsynchronously(Opal.get());
    }

    public static void runTaskLater(final @NotNull BukkitRunnable runnable, final long delayTicks) {
        runnable.runTaskLater(Opal.get(), delayTicks);
    }

    public static void runTaskLater(final BukkitRunnable runnable, @NotNull final Duration delay) {
        runTaskLater(runnable, delay.toMillis() / 50L);
    }

    public static void runTaskLaterAsync(final @NotNull BukkitRunnable runnable, final long delayTicks) {
        runnable.runTaskLaterAsynchronously(Opal.get(), delayTicks);
    }

    public static void runTaskLaterAsync(final BukkitRunnable runnable, @NotNull final Duration delay) {
        runTaskLaterAsync(runnable, delay.toMillis() / 50L);
    }

    public static void runTaskTimer(
        final @NotNull BukkitRunnable runnable, final long delayTicks, final long periodTicks
    ) {
        runnable.runTaskTimer(Opal.get(), delayTicks, periodTicks);
    }

    public static void runTaskTimer(
        final BukkitRunnable runnable, @NotNull final Duration delay, @NotNull final Duration period
    ) {
        runTaskTimer(runnable, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public static void runTaskTimerAsync(
        final @NotNull BukkitRunnable runnable, final long delayTicks, final long periodTicks
    ) {
        runnable.runTaskTimerAsynchronously(Opal.get(), delayTicks, periodTicks);
    }

    public static void runTaskTimerAsync(
        final BukkitRunnable runnable, @NotNull final Duration delay, @NotNull final Duration period
    ) {
        runTaskTimerAsync(runnable, delay.toMillis() / 50L, period.toMillis() / 50L);
    }
}