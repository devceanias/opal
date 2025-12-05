package net.oceanias.opal.menu.item.cycle;

import net.oceanias.opal.menu.item.OAbstractItem;
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
public abstract class OCycleItem extends OAbstractItem {
    private final List<ItemProvider> providers;
    private int currentProviderIndex;

    protected OCycleItem() {
        this(0);
    }

    protected OCycleItem(final int initialProviderIndex) {
        currentProviderIndex = initialProviderIndex;
        providers = getItemProviders();
    }

    protected abstract List<ItemProvider> getItemProviders();

    protected void onStateChange(@Nullable final Player player, final int state) {}

    @Override
    public ItemProvider getItemProvider() {
        return providers.get(currentProviderIndex);
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
        if (forward) {
            currentProviderIndex++;

            if (currentProviderIndex == providers.size()) {
                currentProviderIndex = 0;
            }
        } else {
            currentProviderIndex--;

            if (currentProviderIndex < 0) {
                currentProviderIndex = providers.size() - 1;
            }
        }

        onStateChange(player, currentProviderIndex);
        notifyWindows();
    }
}