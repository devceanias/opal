package net.oceanias.opal.menu.item.control.impl;

import net.oceanias.opal.menu.item.control.OControlItem;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.gui.ScrollGui;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class OScrollItem extends OControlItem<ScrollGui<?>> {
    private final Map<ClickType, Integer> linesByClick;

    protected OScrollItem(final int lines) {
        linesByClick = Map.of(
            ClickType.LEFT, lines,
            ClickType.SHIFT_LEFT, lines,
            ClickType.RIGHT, lines,
            ClickType.SHIFT_RIGHT, lines
        );
    }

    @Override
    public void onItemClick(
        @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
    ) {
        final Integer lines = linesByClick.get(click);

        if (lines == null) {
            return;
        }

        getGui().scroll(lines);
    }
}