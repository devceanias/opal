package net.oceanias.opal.menu.item;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class OPageItem extends OControlItem<PagedGui<?>> {
    private final boolean forward;

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