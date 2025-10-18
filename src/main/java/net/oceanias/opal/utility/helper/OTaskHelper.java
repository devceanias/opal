package net.oceanias.opal.utility.helper;

import net.oceanias.opal.OPlugin;
import java.time.Duration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
@UtilityClass
public final class OTaskHelper {
    public @NotNull BukkitTask runTask(final Runnable task) {
        return OPlugin.get().getScheduler().runTask(OPlugin.get(), task);
    }

    public @NotNull BukkitTask runTaskAsync(final Runnable task) {
        return OPlugin.get().getScheduler().runTaskAsynchronously(OPlugin.get(), task);
    }

    public @NotNull BukkitTask runTaskLater(final Runnable task, final long delayTicks) {
        return OPlugin.get().getScheduler().runTaskLater(OPlugin.get(), task, delayTicks);
    }

    public @NotNull BukkitTask runTaskLater(final Runnable task, final @NotNull Duration delay) {
        return runTaskLater(task, delay.toMillis() / 50L);
    }

    public @NotNull BukkitTask runTaskLaterAsync(final Runnable task, final long delayTicks) {
        return OPlugin.get().getScheduler().runTaskLaterAsynchronously(OPlugin.get(), task, delayTicks);
    }

    public @NotNull BukkitTask runTaskLaterAsync(final Runnable task, @NotNull final Duration delay) {
        return runTaskLaterAsync(task, delay.toMillis() / 50L);
    }

    public @NotNull BukkitTask runTaskTimer(
        final Runnable task, final long delayTicks, final long periodTicks
    ) {
        return OPlugin.get().getScheduler().runTaskTimer(OPlugin.get(), task, delayTicks, periodTicks);
    }

    public @NotNull BukkitTask runTaskTimer(
        final Runnable task, final @NotNull Duration delay, final @NotNull Duration period
    ) {
        return runTaskTimer(task, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public @NotNull BukkitTask runTaskTimerAsync(
        final Runnable task, final long delayTicks, final long periodTicks
    ) {
        return OPlugin.get().getScheduler().runTaskTimerAsynchronously(OPlugin.get(), task, delayTicks, periodTicks);
    }

    public @NotNull BukkitTask runTaskTimerAsync(
        final Runnable task, final @NotNull Duration delay, final @NotNull Duration period
    ) {
        return runTaskTimerAsync(task, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public void runTask(final @NotNull BukkitRunnable runnable) {
        runnable.runTask(OPlugin.get());
    }

    public void runTaskAsync(final @NotNull BukkitRunnable runnable) {
        runnable.runTaskAsynchronously(OPlugin.get());
    }

    public void runTaskLater(final @NotNull BukkitRunnable runnable, final long delayTicks) {
        runnable.runTaskLater(OPlugin.get(), delayTicks);
    }

    public void runTaskLater(final BukkitRunnable runnable, @NotNull final Duration delay) {
        runTaskLater(runnable, delay.toMillis() / 50L);
    }

    public void runTaskLaterAsync(final @NotNull BukkitRunnable runnable, final long delayTicks) {
        runnable.runTaskLaterAsynchronously(OPlugin.get(), delayTicks);
    }

    public void runTaskLaterAsync(final BukkitRunnable runnable, @NotNull final Duration delay) {
        runTaskLaterAsync(runnable, delay.toMillis() / 50L);
    }

    public void runTaskTimer(
        final @NotNull BukkitRunnable runnable, final long delayTicks, final long periodTicks
    ) {
        runnable.runTaskTimer(OPlugin.get(), delayTicks, periodTicks);
    }

    public void runTaskTimer(
        final BukkitRunnable runnable, @NotNull final Duration delay, @NotNull final Duration period
    ) {
        runTaskTimer(runnable, delay.toMillis() / 50L, period.toMillis() / 50L);
    }

    public void runTaskTimerAsync(
        final @NotNull BukkitRunnable runnable, final long delayTicks, final long periodTicks
    ) {
        runnable.runTaskTimerAsynchronously(OPlugin.get(), delayTicks, periodTicks);
    }

    public void runTaskTimerAsync(
        final BukkitRunnable runnable, @NotNull final Duration delay, @NotNull final Duration period
    ) {
        runTaskTimerAsync(runnable, delay.toMillis() / 50L, period.toMillis() / 50L);
    }
}