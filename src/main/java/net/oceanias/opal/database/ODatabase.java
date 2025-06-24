package net.oceanias.opal.database;

import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import org.bukkit.scheduler.BukkitRunnable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class ODatabase implements OProvider {
    private AutosaveTask autosave;

    public abstract void openConnection();

    public abstract void closeConnection();

    public abstract boolean isConnected();

    public abstract Duration getAutosave();

    @Override
    public final void registerInternally() {
        final Duration interval = getAutosave();

        if (interval != null && !interval.isZero() && !interval.isNegative()) {
            autosave = new AutosaveTask();

            OTaskHelper.runTaskTimerAsync(autosave, interval, interval);
        }

        if (isConnected()) {
            return;
        }

        openConnection();

        OProvider.super.registerInternally();
    }

    @Override
    public final void unregisterInternally() {
        if (autosave != null) {
            autosave.cancel();
            autosave = null;
        }

        if (!isConnected()) {
            return;
        }

        OProvider.super.unregisterInternally();

        closeConnection();
    }

    protected void onAutosave() {}

    @RequiredArgsConstructor
    private final class AutosaveTask extends BukkitRunnable {
        @Override
        public void run() {
            if (!isConnected()) {
                return;
            }

            onAutosave();
        }
    }
}