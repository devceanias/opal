package net.oceanias.ocean.database;

import net.oceanias.ocean.module.OProvider;
import net.oceanias.ocean.plugin.OPlugin;
import java.time.Duration;
import org.bukkit.scheduler.BukkitRunnable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ODatabase implements OProvider {
    private final OPlugin plugin;

    private Autosave autosave;

    public abstract void openConnection();

    public abstract void closeConnection();

    public abstract boolean isConnected();

    public abstract Duration getAutosave();

    @Override
    public final void onRegister() {
        final Duration interval = getAutosave();

        if (interval != null && !interval.isZero() && !interval.isNegative()) {
            autosave = new Autosave(this);
            autosave.runTaskTimerAsynchronously(plugin, 0L, interval.toSeconds() * 20L);
        }

        if (isConnected()) {
            return;
        }

        openConnection();
    }

    @Override
    public final void onUnregister() {
        if (autosave != null) {
            autosave.cancel();
            autosave = null;
        }

        if (!isConnected()) {
            return;
        }

        closeConnection();
    }

    protected void onAutosave() {}

    @RequiredArgsConstructor
    private static final class Autosave extends BukkitRunnable {
        private final ODatabase database;

        @Override
        public void run() {
            if (!database.isConnected()) {
                return;
            }

            database.onAutosave();
        }
    }
}