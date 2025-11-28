package net.oceanias.opal.menu.item.update;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.menu.item.OAsyncItem;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.invui.window.AbstractWindow;

@SuppressWarnings("unused")
public abstract class OAsyncAutoUpdateItem extends OAsyncItem {
    private final Duration interval;
    private final boolean autoAsyncExecution;

    private BukkitTask task;

    protected OAsyncAutoUpdateItem(final Duration interval, final boolean autoAsyncExecution) {
        super(autoAsyncExecution);

        this.interval = interval;
        this.autoAsyncExecution = autoAsyncExecution;
    }

    protected abstract void setUpdatingItem();

    @Override
    protected final void setItemProvider() {
        setUpdatingItem();
    }

    @Override
    public void addWindow(final AbstractWindow window) {
        super.addWindow(window);

        if (task != null) {
            return;
        }

        if (autoAsyncExecution && OPlugin.get().getServer().isPrimaryThread()) {
            task = OTaskHelper.runTaskTimerAsync(this::setItemProvider, Duration.ZERO, interval);

            return;
        }

        task = OTaskHelper.runTaskTimer(this::setItemProvider, Duration.ZERO, interval);
    }

    @Override
    public void removeWindow(final AbstractWindow window) {
        super.removeWindow(window);

        if (!getWindows().isEmpty()) {
            return;
        }

        if (task == null) {
            return;
        }

        task.cancel();
        task = null;
    }
}