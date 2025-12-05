package net.oceanias.opal.menu.item.cycle;

import net.oceanias.opal.menu.item.OAsyncItem;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.ItemProvider;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings("unused")
public abstract class OAsyncCycleItem extends OAsyncItem {
    private volatile List<ItemProvider> providers = List.of();
    private int currentProviderIndex;

    protected OAsyncCycleItem(final boolean autoAsyncExecution) {
        this(0, autoAsyncExecution);
    }

    protected OAsyncCycleItem(final int initialProviderIndex, final boolean autoAsyncExecution) {
        super(autoAsyncExecution);

        currentProviderIndex = initialProviderIndex;
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

    protected void onStateChange(@Nullable final Player player, final int state) {}

    @Override
    public ItemProvider getItemProvider() {
        final List<ItemProvider> list = providers;

        if (list.isEmpty()) {
            return super.getItemProvider();
        }

        return list.get(currentProviderIndex);
    }

    @Override
    public void onItemClick(
        @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
    ) {
        if (click.isRightClick()) {
            cycleItemProvider(player, false);

            return;
        }

        if (!click.isLeftClick()) {
            return;
        }

        cycleItemProvider(player, true);
    }

    public void cycleItemProvider(final boolean forward) {
        cycleItemProvider(null, forward);
    }

    private void cycleItemProvider(@Nullable final Player player, final boolean forward) {
        final List<ItemProvider> list = providers;

        if (list.isEmpty()) {
            return;
        }

        final int size = list.size();

        if (forward) {
            currentProviderIndex++;

            if (currentProviderIndex == size) {
                currentProviderIndex = 0;
            }
        } else {
            currentProviderIndex--;

            if (currentProviderIndex < 0) {
                currentProviderIndex = size - 1;
            }
        }

        onStateChange(player, currentProviderIndex);
        notifyWindows();
    }
}