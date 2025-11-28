package net.oceanias.opal.menu.item.control.impl;

import net.oceanias.opal.menu.item.control.OAsyncControlItem;
import net.oceanias.opal.menu.item.control.OControlItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.TabGui;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
public abstract class OAsyncTabItem extends OAsyncControlItem<TabGui> {
    private final int tab;

    protected OAsyncTabItem(final int tab, final boolean autoAsyncExecution) {
        super(autoAsyncExecution);

        this.tab = tab;
    }

    @Override
    public void onItemClick(
        @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
    ) {
        getGui().setTab(tab);
    }
}