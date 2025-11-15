package net.oceanias.opal.menu;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.builder.OItem;
import net.oceanias.opal.utility.builder.OSound;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.util.*;
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
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;
import xyz.xenondevs.invui.window.Window;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OStringExtension.class, OCommandSenderExtension.class })
public abstract class OMenu {
    private final Map<UUID, Window> windows = new HashMap<>();

    private static final OItem GO_BACK_ITEM = OItem.builder(Material.TIPPED_ARROW)
        .name("&cGo Back")
        .lore("&fClick &7to use!")
        .potionType(PotionType.HEALING)
        .flagsAll()
        .build();

    public abstract Gui.Builder<?, ?> getGui(Player player);

    public abstract Window getWindow(Gui gui, Player player);

    public void openMenu(final Player player) {
        final Gui.Builder<?, ?> gui = getGui(player);

        if (gui instanceof final PagedGui.Builder<?> paged) {
            addPageNavigationIngredients(paged);
        }

        final Window window = getWindow(gui.build(), player);

        Tracker.registerMenu(this, window, player);

        window.addCloseHandler(() -> Tracker.unregisterMenu(this, window, player));
        window.open();
    }

    public void addPageNavigationIngredients(final PagedGui.@NotNull Builder<?> builder) {
        builder
            .addIngredient('<', new OMenu.Previous())
            .addIngredient('>', new OMenu.Next());
    }

    @Contract("_ -> new")
    public static @NotNull AdventureComponentWrapper ofTitle(@NotNull final String title) {
        return new AdventureComponentWrapper(title.deserialize());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Tracker {
        private static final Map<Class<? extends OMenu>, Set<Window>> WINDOWS_BY_CLASS = new HashMap<>();
        private static final Map<OMenu, Map<UUID, Window>> WINDOWS_BY_INSTANCE = new HashMap<>();

        public static void registerMenu(final @NotNull OMenu menu, final Window window, final @NotNull Player player) {
            final UUID uuid = player.getUniqueId();

            menu.windows.put(uuid, window);

            WINDOWS_BY_INSTANCE
                .computeIfAbsent(menu, ignored -> new HashMap<>())
                .put(uuid, window);

            WINDOWS_BY_CLASS
                .computeIfAbsent(menu.getClass(), ignored -> new HashSet<>())
                .add(window);
        }

        public static void unregisterMenu(final OMenu menu, final Window window, final @NotNull Player player) {
            final UUID uuid = player.getUniqueId();

            final Map<UUID, Window> windowsByInstance = WINDOWS_BY_INSTANCE.get(menu);
            final Set<Window> windowsByClass = WINDOWS_BY_CLASS.get(menu.getClass());

            menu.windows.remove(uuid);

            if (windowsByInstance != null) {
                windowsByInstance.remove(uuid);

                if (windowsByInstance.isEmpty()) {
                    WINDOWS_BY_INSTANCE.remove(menu);
                }
            }

            if (windowsByClass == null) {
                return;
            }

            windowsByClass.remove(window);

            if (!windowsByClass.isEmpty()) {
                return;
            }

            WINDOWS_BY_CLASS.remove(menu.getClass());
        }

        public static void closeAll(final Class<? extends OMenu> clazz) {
            final Set<Window> windows = WINDOWS_BY_CLASS.get(clazz);

            if (windows == null) {
                return;
            }

            for (final Window window : new ArrayList<>(windows)) {
                window.close();
            }
        }

        public static void closeAll(final OMenu menu) {
            final Map<UUID, Window> windows = WINDOWS_BY_INSTANCE.get(menu);

            if (windows == null) {
                return;
            }

            for (final Window window : new ArrayList<>(windows.values())) {
                window.close();
            }
        }

        public static void closeFor(final Class<? extends OMenu> clazz, final Player player) {
            final Set<Window> windows = WINDOWS_BY_CLASS.get(clazz);

            if (windows == null) {
                return;
            }

            for (final Window window : new ArrayList<>(windows)) {
                if (!player.equals(window.getCurrentViewer())) {
                    continue;
                }

                window.close();
            }
        }

        public static void closeFor(final @NotNull OMenu menu, final @NotNull Player player) {
            final Window window = menu.windows.get(player.getUniqueId());

            if (window == null) {
                return;
            }

            window.close();
        }

        public static int getOpen(final Class<? extends OMenu> clazz) {
            final Set<Window> windows = WINDOWS_BY_CLASS.get(clazz);

            return windows != null
                ? windows.size()
                : 0;
        }

        public static @NotNull Set<Player> getViewers(final Class<? extends OMenu> clazz) {
            final Set<Window> windows = WINDOWS_BY_CLASS.get(clazz);

            if (windows == null) {
                return Set.of();
            }

            final Set<Player> viewers = new HashSet<>();

            for (final Window window : windows) {
                final Player viewer = window.getCurrentViewer();

                if (viewer == null) {
                    continue;
                }

                viewers.add(viewer);
            }

            return viewers;
        }

        public static boolean isViewing(final Class<? extends OMenu> clazz, final Player player) {
            final Set<Window> windows = WINDOWS_BY_CLASS.get(clazz);

            if (windows == null) {
                return false;
            }

            for (final Window window : windows) {
                if (!player.equals(window.getCurrentViewer())) {
                    continue;
                }

                return true;
            }

            return false;
        }

        public static boolean isViewing(final @NotNull OMenu menu, final @NotNull Player player) {
            final Window window = menu.windows.get(player.getUniqueId());

            return window != null && player.equals(window.getCurrentViewer());
        }
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
                return GO_BACK_ITEM;
            }

            return OItem.FILLER;
        }

        @Override
        public void handleClick(
            final @NotNull ClickType click, final @NotNull Player player, final @NotNull InventoryClickEvent event
        ) {
            if (getGui().hasPreviousPage()) {
                super.handleClick(click, player, event);

                OSound.builder().sound(Sound.ITEM_BOOK_PAGE_TURN).build().play(player);

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
            final @NotNull ClickType click, final @NotNull Player player, final @NotNull InventoryClickEvent event
        ) {
            if (!getGui().hasNextPage()) {
                return;
            }

            super.handleClick(click, player, event);

            OSound.builder().sound(Sound.ITEM_BOOK_PAGE_TURN).build().play(player);
        }
    }

    @RequiredArgsConstructor
    public static final class Back extends AbstractItem {
        private final OMenu back;

        @Override
        public ItemProvider getItemProvider() {
            return GO_BACK_ITEM;
        }

        @Override
        public void handleClick(
            final @NotNull ClickType click, final @NotNull Player player, final @NotNull InventoryClickEvent event
        ) {
            back.openMenu(player);
        }
    }
}