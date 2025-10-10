package net.oceanias.opal.menu;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.builder.OItem;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import net.oceanias.opal.utility.extension.OStringExtension;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.window.Window;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OStringExtension.class, OCommandSenderExtension.class })
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

    public static void addIngredients() {
        Structure.addGlobalIngredient('#', OItem.FILLER.get());
        Structure.addGlobalIngredient('<', new OMenu.Previous());
        Structure.addGlobalIngredient('>', new OMenu.Next());
    }

    public static final class Previous extends PageItem {
        private final OMenu back;

        public Previous() {
            super(false);

            this.back = null;
        }

        public Previous(final OMenu back) {
            super(false);

            this.back = back;
        }

        @Override
        public @NotNull ItemProvider getItemProvider(final @NotNull PagedGui<?> gui) {
            final int now = gui.getCurrentPage() + 1;
            final int max = gui.getPageAmount();

            if (gui.hasPreviousPage()) {
                return OItem.builder(Material.TIPPED_ARROW)
                    .amount(Math.max(1, Math.min(64, now - 1)))
                    .name(OPlugin.get().getColour() + "Previous Page")
                    .lore(
                        "&fCurrent: &6" + now,
                        "",
                        OPlugin.get().getColour() + "Click &7to turn!"
                    )
                    .potionType(PotionType.HEALING)
                    .flagsAll()
                    .build();
            }

            if (back != null) {
                return OItem.builder(Material.TIPPED_ARROW)
                    .name("&cGo Back")
                    .lore("&fClick &7to use!")
                    .potionType(PotionType.HEALING)
                    .flagsAll()
                    .build();
            }

            return OItem.FILLER;
        }

        @Override
        public void handleClick(
            final @NotNull ClickType click,
            final @NotNull Player player,
            final @NotNull InventoryClickEvent event
        ) {
            if (getGui().hasPreviousPage()) {
                super.handleClick(click, player, event);

                player.soundDSR(Sound.ITEM_BOOK_PAGE_TURN);

                return;
            }

            if (back == null) {
                return;
            }

            back.openMenu(player);
        }
    }

    public static final class Next extends PageItem {
        public Next() {
            super(true);
        }

        @Override
        public @NotNull ItemProvider getItemProvider(final @NotNull PagedGui<?> gui) {
            final int now = gui.getCurrentPage() + 1;
            final int max = gui.getPageAmount();

            if (gui.hasNextPage()) {
                return OItem.builder(Material.TIPPED_ARROW)
                    .amount(Math.max(1, Math.min(64, now + 1)))
                    .name(OPlugin.get().getColour() + "Next Page")
                    .lore(
                        "&fCurrent: &6" + now,
                        "",
                        OPlugin.get().getColour() + "Click &7to turn!"
                    )
                    .potionType(PotionType.LEAPING)
                    .flagsAll()
                    .build();
            }

            return OItem.FILLER;
        }

        @Override
        public void handleClick(
            final @NotNull ClickType click,
            final @NotNull Player player,
            final @NotNull InventoryClickEvent event
        ) {
            if (!getGui().hasNextPage()) {
                return;
            }

            super.handleClick(click, player, event);

            player.soundDSR(Sound.ITEM_BOOK_PAGE_TURN);
        }
    }
}