package net.oceanias.opal.menu.item.control.impl;

import net.oceanias.opal.menu.item.control.OAsyncControlItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.PagedGui;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class OAsyncPageItem extends OAsyncControlItem<PagedGui<?>> {
    private final boolean forward;

    protected OAsyncPageItem(final boolean forward, final boolean autoAsyncExecution) {
        super(autoAsyncExecution);

        this.forward = forward;
    }

    @Override
    public void onItemClick(
        @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
    ) {
        if (!forward) {
            getGui().goBack();

            return;
        }

        getGui().goForward();
    }
}