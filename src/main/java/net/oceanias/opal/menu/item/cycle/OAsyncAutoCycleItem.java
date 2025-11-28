package net.oceanias.opal.menu.item.cycle;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.menu.item.OAsyncItem;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.time.Duration;
import java.util.List;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.window.AbstractWindow;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class OAsyncAutoCycleItem extends OAsyncItem {
    private final Duration interval;
    private final boolean autoAsyncExecution;

    private volatile List<ItemProvider> providers = List.of();

    private BukkitTask task;
    private int currentProviderIndex = 0;

    protected OAsyncAutoCycleItem(final Duration interval, final boolean autoAsyncExecution) {
        super(autoAsyncExecution);

        this.interval = interval;
        this.autoAsyncExecution = autoAsyncExecution;
    }

    protected abstract void setItemProviders();

    @Override
    protected final void setItemProvider() {
        setItemProviders();
    }

    protected final void applyItemProviders(final @NotNull List<ItemProvider> providers) {
        this.providers = providers;

        if (providers.isEmpty()) {
            currentProviderIndex = 0;
        } else if (currentProviderIndex < 0 || currentProviderIndex >= providers.size()) {
            currentProviderIndex = 0;
        }

        notifyWindows();
    }

    @Override
    public ItemProvider getItemProvider() {
        final List<ItemProvider> list = providers;

        if (list.isEmpty()) {
            return super.getItemProvider();
        }

        return list.get(currentProviderIndex);
    }

    @Override
    public void addWindow(final AbstractWindow window) {
        super.addWindow(window);

        if (task != null) {
            return;
        }

        final Runnable cycle = () -> {
            final List<ItemProvider> list = providers;

            if (list.isEmpty()) {
                return;
            }

            currentProviderIndex++;

            if (currentProviderIndex == list.size()) {
                currentProviderIndex = 0;
            }

            notifyWindows();
        };

        if (autoAsyncExecution && OPlugin.get().getServer().isPrimaryThread()) {
            task = OTaskHelper.runTaskTimerAsync(cycle, Duration.ZERO, interval);

            return;
        }

        task = OTaskHelper.runTaskTimer(cycle, Duration.ZERO, interval);
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