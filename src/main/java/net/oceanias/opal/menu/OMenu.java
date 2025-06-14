package net.oceanias.opal.menu;

import net.oceanias.opal.plugin.ORegistry;
import net.oceanias.opal.utility.builder.OItemBuilder;
import net.oceanias.opal.utility.extension.OPlayerExtension;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionType;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.window.Window;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OStringExtension.class, OPlayerExtension.class })
public abstract class OMenu {
    public abstract Gui getGui(Player player);

    public abstract Window getWindow(Gui gui, Player player);

    public void openMenu(final Player player) {
        getWindow(getGui(player), player).open();
    }

    @Contract("_ -> new")
    public static @NotNull AdventureComponentWrapper ofTitle(@NotNull final String title) {
        return new AdventureComponentWrapper(title.deserialize());
    }

    public static final class Previous extends PageItem {
        public Previous() {
            super(false);
        }

        @Override
        public ItemProvider getItemProvider(final @NotNull PagedGui<?> gui) {
            final int now = gui.getCurrentPage() + 1;
            final int max = gui.getPageAmount();

            if (gui.hasPreviousPage()) {
                return new OItemBuilder(Material.TIPPED_ARROW)
                    .setPotionType(PotionType.LONG_LEAPING)
                    .setName(ORegistry.getCaller().getColour() + "Previous Page")
                    .addLines(List.of(
                        "&7• &fTransition: &e" + now + " &7/ &e" + max + " &7» &6" + (now + 1) + " &7/ &6" + max,
                        "",
                        ORegistry.getCaller().getColour() + "Click &7to show!"
                    ))
                    .addGlint()
                    .hideAll();
            }

            return OItemBuilder.getFiller();
        }

        @Override
        public void handleClick(
            final @NotNull ClickType click,
            final @NotNull Player player,
            final @NotNull InventoryClickEvent event
        ) {
            if (!getGui().hasNextPage()) {
                player.soundDSR(Sound.BLOCK_NOTE_BLOCK_BASS);

                return;
            }

            super.handleClick(click, player, event);

            player.soundDSR(Sound.ITEM_BOOK_PAGE_TURN);
        }
    }

    public static final class Next extends PageItem {
        public Next() {
            super(true);
        }

        @Override
        public ItemProvider getItemProvider(final @NotNull PagedGui<?> gui) {
            final int now = gui.getCurrentPage() + 1;
            final int max = gui.getPageAmount();

            if (gui.hasNextPage()) {
                return new OItemBuilder(Material.TIPPED_ARROW)
                    .setPotionType(PotionType.LONG_FIRE_RESISTANCE)
                    .setName(ORegistry.getCaller().getColour() + "Next Page")
                    .addLines(List.of(
                        "&7• &fTransition: &6" + now + " &7/ &6" + max + " &7» &e" + (now - 1) + " &7/ &e" + max,
                        "",
                        ORegistry.getCaller().getColour() + "Click &7to show!"
                    ))
                    .addGlint()
                    .hideAll();
            }

            return OItemBuilder.getFiller();
        }

        @Override
        public void handleClick(
            final @NotNull ClickType click,
            final @NotNull Player player,
            final @NotNull InventoryClickEvent event
        ) {
            if (!getGui().hasNextPage()) {
                player.soundDSR(Sound.BLOCK_NOTE_BLOCK_BASS);

                return;
            }

            super.handleClick(click, player, event);

            player.soundDSR(Sound.ITEM_BOOK_PAGE_TURN);
        }
    }
}