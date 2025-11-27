package net.oceanias.opal.menu.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class OControlItem<G extends Gui> extends ControlItem<G> {
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
}