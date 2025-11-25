package net.oceanias.opal.menu;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.builder.OItem;
import net.oceanias.opal.utility.builder.OSound;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import net.oceanias.opal.utility.extension.OStringExtension;
import net.oceanias.opal.utility.helper.OTaskHelper;
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
import org.jetbrains.annotations.Nullable;

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

    protected abstract Gui.Builder<?, ?> getGui(Player player);

    protected abstract Window getWindow(Gui gui, Player player);

    public void openMenu(final Player player) {
        openMenu(player, false);
    }

    private void openMenu(final Player player, final boolean silent) {
        final Gui.Builder<?, ?> gui = getGui(player);

        if (gui instanceof final PagedGui.Builder<?> paged) {
            addPageNavigationIngredients(paged);
        }

        final Window window = getWindow(gui.build(), player);

        Tracker.registerMenu(this, window, player);

        window.addOpenHandler(() -> {
            if (isMenuOpenSound() && !silent) {
                OSound.builder().sound(OSound.Preset.OPEN).build().play(player);
            }
        });

        window.addCloseHandler(() -> Tracker.unregisterMenu(this, window, player));
        window.open();
    }

    protected boolean isMenuOpenSound() {
        return true;
    }

    public static void addPageNavigationIngredients(final PagedGui.@NotNull Builder<?> builder) {
        builder
            .addIngredient('<', new Previous())
            .addIngredient('>', new Next());
    }

    @Contract("_ -> new")
    public static @NotNull AdventureComponentWrapper ofTitle(@NotNull final String title) {
        return new AdventureComponentWrapper(title.deserialize());
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Tracker {
        private static final Map<Class<? extends OMenu>, Set<Window>> windowsByClass = new HashMap<>();
        private static final Map<OMenu, Map<UUID, Window>> windowsByInstance = new HashMap<>();

        public static void registerMenu(final @NotNull OMenu menu, final Window window, final @NotNull Player player) {
            final UUID uuid = player.getUniqueId();

            menu.windows.put(uuid, window);

            windowsByInstance
                .computeIfAbsent(menu, ignored -> new HashMap<>())
                .put(uuid, window);

            windowsByClass
                .computeIfAbsent(menu.getClass(), ignored -> new HashSet<>())
                .add(window);
        }

        public static void unregisterMenu(final OMenu menu, final Window window, final @NotNull Player player) {
            final UUID uuid = player.getUniqueId();

            final Map<UUID, Window> windowsByInstance = Tracker.windowsByInstance.get(menu);
            final Set<Window> windowsByClass = Tracker.windowsByClass.get(menu.getClass());

            menu.windows.remove(uuid);

            if (windowsByInstance != null) {
                windowsByInstance.remove(uuid);

                if (windowsByInstance.isEmpty()) {
                    Tracker.windowsByInstance.remove(menu);
                }
            }

            if (windowsByClass == null) {
                return;
            }

            windowsByClass.remove(window);

            if (!windowsByClass.isEmpty()) {
                return;
            }

            Tracker.windowsByClass.remove(menu.getClass());
        }

        public static void closeAll(final Class<? extends OMenu> clazz) {
            final Set<Window> windows = windowsByClass.get(clazz);

            if (windows == null) {
                return;
            }

            for (final Window window : new ArrayList<>(windows)) {
                window.close();
            }
        }

        public static void closeAll(final OMenu menu) {
            final Map<UUID, Window> windows = windowsByInstance.get(menu);

            if (windows == null) {
                return;
            }

            for (final Window window : new ArrayList<>(windows.values())) {
                window.close();
            }
        }

        public static void refreshAll(final Class<? extends OMenu> clazz) {
            final Set<Window> windows = windowsByClass.get(clazz);

            if (windows == null) {
                return;
            }

            for (final Window window : new ArrayList<>(windows)) {
                final Player viewer = window.getCurrentViewer();

                if (viewer == null) {
                    continue;
                }

                final OMenu menu = findMenuByWindow(window);

                if (menu == null) {
                    continue;
                }

                reopenMenu(menu, viewer);
            }
        }

        public static void refreshAll(final OMenu menu) {
            final Map<UUID, Window> instances = windowsByInstance.get(menu);

            if (instances == null) {
                return;
            }

            for (final Map.Entry<UUID, Window> entry : new ArrayList<>(instances.entrySet())) {
                final Player viewer = OPlugin.get().getServer().getPlayer(entry.getKey());

                if (viewer == null) {
                    continue;
                }

                reopenMenu(menu, viewer);
            }
        }

        public static void closeFor(final Class<? extends OMenu> clazz, final Player player) {
            final Set<Window> windows = windowsByClass.get(clazz);

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

        public static void refreshFor(final Class<? extends OMenu> clazz, final Player player) {
            final Set<Window> windows = windowsByClass.get(clazz);

            if (windows == null) {
                return;
            }

            for (final Window window : new ArrayList<>(windows)) {
                if (!player.equals(window.getCurrentViewer())) {
                    continue;
                }

                final OMenu menu = findMenuByWindow(window);

                if (menu == null) {
                    return;
                }

                reopenMenu(menu, player);

                return;
            }
        }

        public static void refreshFor(final @NotNull OMenu menu, final @NotNull Player player) {
            final Window window = menu.windows.get(player.getUniqueId());

            if (window == null) {
                return;
            }

            reopenMenu(menu, player);
        }

        public static int getOpen(final Class<? extends OMenu> clazz) {
            final Set<Window> windows = windowsByClass.get(clazz);

            return windows != null
                ? windows.size()
                : 0;
        }

        public static @NotNull Set<Player> getViewers(final Class<? extends OMenu> clazz) {
            final Set<Window> windows = windowsByClass.get(clazz);

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
            final Set<Window> windows = windowsByClass.get(clazz);

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

        private static void reopenMenu(final @NotNull OMenu menu, final Player player) {
            final Window intermediate = Window.single()
                .setViewer(player)
                .setGui(Gui.empty(9, 1))
                .setTitle("")
                .build();

            intermediate.open();

            OTaskHelper.runTask(() ->
                menu.openMenu(player, true)
            );
        }

        private static @Nullable OMenu findMenuByWindow(final Window window) {
            for (final Map.Entry<OMenu, Map<UUID, Window>> entry : windowsByInstance.entrySet()) {
                if (!entry.getValue().containsValue(window)) {
                    continue;
                }

                return entry.getKey();
            }

            return null;
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
            final int current = gui.getCurrentPage() + 1;

            if (gui.hasPreviousPage()) {
                return OItem.builder(Material.TIPPED_ARROW)
                    .amount(Math.max(1, Math.min(64, current - 1)))
                    .name(OPlugin.get().getColour() + "Previous Page")
                    .lore(
                        "&fCurrent: &6" + current,
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
            final int current = gui.getCurrentPage() + 1;

            if (gui.hasNextPage()) {
                return OItem.builder(Material.TIPPED_ARROW)
                    .amount(Math.max(1, Math.min(64, current + 1)))
                    .name(OPlugin.get().getColour() + "Next Page")
                    .lore(
                        "&fCurrent: &6" + current,
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