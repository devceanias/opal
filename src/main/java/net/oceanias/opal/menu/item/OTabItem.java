package net.oceanias.opal.menu.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.TabGui;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class OTabItem extends OControlItem<TabGui> {
    private final int tab;

    @Override
    public void onItemClick(
        @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
    ) {
        getGui().setTab(tab);
    }
}