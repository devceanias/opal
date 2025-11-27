package net.oceanias.opal.menu.item;

import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import java.util.List;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.window.AbstractWindow;

@SuppressWarnings("unused")
public abstract class OAutoCycleItem extends OAbstractItem {
    private final List<ItemProvider> providers;
    private final Duration interval;

    private BukkitTask task;
    private int index = 0;

    protected OAutoCycleItem(final Duration interval) {
        this.interval = interval;

        providers = getItemProviders();
    }

    protected abstract List<ItemProvider> getItemProviders();

    @Override
    public ItemProvider getItemProvider() {
        return providers.get(index);
    }

    @Override
    public void addWindow(final AbstractWindow window) {
        super.addWindow(window);

        if (task != null) {
            return;
        }

        task = OTaskHelper.runTaskTimer(() -> {
            index++;

            if (index == providers.size()) {
                index = 0;
            }

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