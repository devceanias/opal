package net.oceanias.opal.menu.item.control;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.helper.OTaskHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class OAsyncControlItem<G extends Gui> extends ControlItem<G> {
    private final boolean autoAsyncExecution;

    private volatile ItemProvider provider = new ItemWrapper(new ItemStack(Material.AIR));

    protected abstract void setItemProvider();

    protected OAsyncControlItem(final boolean autoAsyncExecution) {
        this.autoAsyncExecution = autoAsyncExecution;

        if (autoAsyncExecution && OPlugin.get().getServer().isPrimaryThread()) {
            OTaskHelper.runTaskLaterAsync(this::setItemProvider, 1L);

            return;
        }

        OTaskHelper.runTaskLater(this::setItemProvider, 1L);
    }

    protected final void applyItemProvider(final ItemProvider provider) {
        this.provider = provider;

        notifyWindows();
    }

    @Override
    public ItemProvider getItemProvider(final G gui) {
        return provider;
    }

    public abstract void onItemClick(
        final @NotNull ClickType click, final @NotNull Player player, final @NotNull InventoryClickEvent event
    );

    @ApiStatus.Internal
    @Override
    public final void handleClick(
        final @NotNull ClickType click, final @NotNull Player player, final @NotNull InventoryClickEvent event
    ) {
        if (click == ClickType.DOUBLE_CLICK) {
            return;
        }

        onItemClick(click, player, event);
    }

    @Override
    public void notifyWindows() {
        if (autoAsyncExecution && OPlugin.get().getServer().isPrimaryThread()) {
            OTaskHelper.runTaskAsync(super::notifyWindows);

            return;
        }

        super.notifyWindows();
    }
}