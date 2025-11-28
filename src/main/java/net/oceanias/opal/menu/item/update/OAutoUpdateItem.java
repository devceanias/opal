package net.oceanias.opal.menu.item.update;

import net.oceanias.opal.menu.item.OAbstractItem;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.window.AbstractWindow;

@SuppressWarnings("unused")
public abstract class OAutoUpdateItem extends OAbstractItem {
    private volatile ItemProvider provider;
    private final Duration interval;

    private BukkitTask task;

    protected OAutoUpdateItem(final Duration interval) {
        this.interval = interval;

        provider = getUpdatingItem();
    }

    protected abstract ItemProvider getUpdatingItem();

    @Override
    public ItemProvider getItemProvider() {
        return provider;
    }

    @Override
    public void addWindow(final AbstractWindow window) {
        super.addWindow(window);

        if (task != null) {
            return;
        }

        task = OTaskHelper.runTaskTimer(() -> {
            provider = getUpdatingItem();

            notifyWindows();
        }, Duration.ZERO, interval);
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