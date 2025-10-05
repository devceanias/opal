package net.oceanias.opal.menu;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.builder.OItemBuilder;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
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
        Structure.addGlobalIngredient('#', OItemBuilder.getFiller());
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
        public ItemProvider getItemProvider(final @NotNull PagedGui<?> gui) {
            final int now = gui.getCurrentPage() + 1;
            final int max = gui.getPageAmount();

            if (gui.hasPreviousPage()) {
                return new OItemBuilder(Material.TIPPED_ARROW)
                    .setPotionType(PotionType.SLOW_FALLING)
                    .setName(OPlugin.get().getColour() + "Previous Page")
                    .addLore(List.of(
                        "&fPage: &e" + now + "&7/&e" + max + " &7» &a" + (now + 1) + "&7/&a" + max,
                        "",
                        OPlugin.get().getColour() + "Click &7to turn!"
                    ))
                    .addGlint()
                    .hideFlags();
            }

            if (back != null) {
                return new OItemBuilder(Material.SPECTRAL_ARROW)
                    .setName(OPlugin.get().getColour() + "Go Back")
                    .addLore(List.of(
                        OPlugin.get().getColour() + "Click &7to use!"
                    ))
                    .addGlint()
                    .hideFlags();
            }

            return OItemBuilder.getFiller();
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
        public ItemProvider getItemProvider(final @NotNull PagedGui<?> gui) {
            final int now = gui.getCurrentPage() + 1;
            final int max = gui.getPageAmount();

            if (gui.hasNextPage()) {
                return new OItemBuilder(Material.TIPPED_ARROW)
                    .setPotionType(PotionType.SLOW_FALLING)
                    .setName(OPlugin.get().getColour() + "Next Page")
                    .addLore(List.of(
                        "&fPage: &e" + now + "&7/&e" + max + " &7» &a" + (now - 1) + "&7/&a" + max,
                        "",
                        OPlugin.get().getColour() + "Click &7to turn!"
                    ))
                    .addGlint()
                    .hideFlags();
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
                return;
            }

            super.handleClick(click, player, event);

            player.soundDSR(Sound.ITEM_BOOK_PAGE_TURN);
        }
    }
}