package net.oceanias.opal.menu;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.menu.item.OAbstractItem;
import net.oceanias.opal.menu.item.OPageItem;
import net.oceanias.opal.utility.builder.OItem;
import net.oceanias.opal.utility.builder.OSound;
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
import xyz.xenondevs.invui.window.Window;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@ExtensionMethod(OStringExtension.class)
public abstract class OMenu {
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
        final Gui.Builder<?, ?> builder = getGui(player);

        if (builder instanceof final PagedGui.Builder<?> paged) {
            addPageNavigationIngredients(paged);
        }

        final Gui gui = builder.build();
        final Window window = getWindow(gui, player);

        Tracker.registerMenu(this, gui, window, player);

        window.addOpenHandler(() -> {
            if (isMenuOpenSound() && !silent) {
                OSound.builder().sound(OSound.Preset.OPEN).build().play(player);
            }
        });

        window.addCloseHandler(() -> Tracker.unregisterMenu(this, player));
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
        private static final Map<Class<? extends OMenu>, Set<Session>> sessionsByClass = new HashMap<>();
        private static final Map<OMenu, Map<Player, Session>> sessionsByInstance = new HashMap<>();

        public static void registerMenu(
            final @NotNull OMenu menu, final Gui gui, final Window window, final @NotNull Player player
        ) {
            final Session session = new Session(gui, window);

            sessionsByInstance.computeIfAbsent(menu, ignored -> new HashMap<>()).put(player, session);

            sessionsByClass
                .computeIfAbsent(menu.getClass(), ignored -> new HashSet<>())
                .add(session);
        }

        public static void unregisterMenu(final @NotNull OMenu menu, final @NotNull Player player) {
            final Set<Session> sessionsByClass = Tracker.sessionsByClass.get(menu.getClass());
            final Map<Player, Session> sessionsByInstance = Tracker.sessionsByInstance.get(menu);

            final Class<? extends OMenu> clazz = menu.getClass();

            if (sessionsByClass == null || sessionsByInstance == null) {
                throw new IllegalStateException(
                    "Error unregistering menu " + clazz.getSimpleName() + "; it is not registered"
                );
            }

            sessionsByClass.remove(sessionsByInstance.get(player));

            if (sessionsByClass.isEmpty()) {
                Tracker.sessionsByClass.remove(clazz);
            }

            sessionsByInstance.remove(player);

            if (!sessionsByInstance.isEmpty()) {
                return;
            }

            Tracker.sessionsByInstance.remove(menu);
        }

        public static void closeAll(final Class<? extends OMenu> clazz) {
            final Set<Session> sessions = sessionsByClass.get(clazz);

            if (sessions == null) {
                return;
            }

            for (final Session session : new ArrayList<>(sessions)) {
                session.window.close();
            }
        }

        public static void closeAll(final OMenu menu) {
            final Map<Player, Session> sessions = sessionsByInstance.get(menu);

            if (sessions == null) {
                return;
            }

            for (final Session session : new ArrayList<>(sessions.values())) {
                session.window.close();
            }
        }

        public static void refreshAll(final Class<? extends OMenu> clazz) {
            final Set<Session> sessions = sessionsByClass.get(clazz);

            if (sessions == null) {
                return;
            }

            for (final Session session : new ArrayList<>(sessions)) {
                final Player viewer = session.window.getCurrentViewer();

                if (viewer == null) {
                    continue;
                }

                final OMenu menu = findMenu(session);

                if (menu == null) {
                    continue;
                }

                reopenMenu(menu, viewer);
            }
        }

        public static void refreshAll(final OMenu menu) {
            final Map<Player, Session> sessions = sessionsByInstance.get(menu);

            if (sessions == null) {
                return;
            }

            for (final Map.Entry<Player, Session> session : new ArrayList<>(sessions.entrySet())) {
                final Player viewer = session.getKey();

                if (viewer == null) {
                    continue;
                }

                reopenMenu(menu, viewer);
            }
        }

        public static void closeFor(final Class<? extends OMenu> clazz, final Player player) {
            final Set<Session> sessions = sessionsByClass.get(clazz);

            if (sessions == null) {
                return;
            }

            for (final Session session : new ArrayList<>(sessions)) {
                final Window window = session.window;

                if (!player.equals(window.getCurrentViewer())) {
                    continue;
                }

                window.close();
            }
        }

        public static void closeFor(final @NotNull OMenu menu, final @NotNull Player player) {
            final Session session = findSession(menu, player);

            if (session == null) {
                return;
            }

            session.window.close();
        }

        public static void refreshFor(final Class<? extends OMenu> clazz, final Player player) {
            final Set<Session> sessions = sessionsByClass.get(clazz);

            if (sessions == null) {
                return;
            }

            for (final Session session : new ArrayList<>(sessions)) {
                if (!player.equals(session.window.getCurrentViewer())) {
                    continue;
                }

                final OMenu menu = findMenu(session);

                if (menu == null) {
                    return;
                }

                reopenMenu(menu, player);

                return;
            }
        }

        public static void refreshFor(final @NotNull OMenu menu, final @NotNull Player player) {
            if (findSession(menu, player) == null) {
                return;
            }

            reopenMenu(menu, player);
        }

        public static int getOpen(final Class<? extends OMenu> clazz) {
            final Set<Session> sessions = sessionsByClass.get(clazz);

            if (sessions == null) {
                return 0;
            }

            return sessions.size();
        }

        public static @NotNull Set<Player> getViewers(final Class<? extends OMenu> clazz) {
            final Set<Session> sessions = sessionsByClass.get(clazz);

            if (sessions == null) {
                return Set.of();
            }

            final Set<Player> viewers = new HashSet<>();

            for (final Session session : sessions) {
                final Player viewer = session.window.getCurrentViewer();

                if (viewer == null) {
                    continue;
                }

                viewers.add(viewer);
            }

            return viewers;
        }

        public static boolean isViewing(final Class<? extends OMenu> clazz, final Player player) {
            final Set<Session> sessions = sessionsByClass.get(clazz);

            if (sessions == null) {
                return false;
            }

            for (final Session session : sessions) {
                if (!player.equals(session.window.getCurrentViewer())) {
                    continue;
                }

                return true;
            }

            return false;
        }

        public static boolean isViewing(final @NotNull OMenu menu, final @NotNull Player player) {
            final Session session = findSession(menu, player);

            return session != null && player.equals(session.window.getCurrentViewer());
        }

        private static void reopenMenu(final @NotNull OMenu menu, final Player player) {
            final Session session = findSession(menu, player);

            if (session == null) {
                return;
            }

            final Gui gui = session.gui;

            final Window intermediate = Window.single()
                .setViewer(player)
                .setGui(Gui.empty(gui.getWidth(), gui.getHeight()))
                .setTitle("")
                .build();

            intermediate.open();

            OTaskHelper.runTask(() ->
                menu.openMenu(player, true)
            );
        }

        public static @Nullable Session findSession(final OMenu menu, final Player player) {
            final Map<Player, Session> sessions = Tracker.sessionsByInstance.get(menu);

            if (sessions == null) {
                return null;
            }

            return sessions.get(player);
        }

        private static @Nullable OMenu findMenu(final Session session) {
            for (final Map.Entry<OMenu, Map<Player, Session>> entry : sessionsByInstance.entrySet()) {
                if (!entry.getValue().containsValue(session)) {
                    continue;
                }

                return entry.getKey();
            }

            return null;
        }

        public record Session(Gui gui, Window window) {}
    }

    public static final class Previous extends OPageItem {
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
        public void onItemClick(
            final @NotNull ClickType click, final @NotNull Player player, final @NotNull InventoryClickEvent event
        ) {
            if (getGui().hasPreviousPage()) {
                super.onItemClick(click, player, event);

                OSound.builder().sound(Sound.ITEM_BOOK_PAGE_TURN).build().play(player);

                return;
            }

            if (back == null) {
                return;
            }

            back.openMenu(player);
        }
    }

    public static final class Next extends OPageItem {
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
        public void onItemClick(
            final @NotNull ClickType click, final @NotNull Player player, final @NotNull InventoryClickEvent event
        ) {
            if (!getGui().hasNextPage()) {
                return;
            }

            super.onItemClick(click, player, event);

            OSound.builder().sound(Sound.ITEM_BOOK_PAGE_TURN).build().play(player);
        }
    }

    @RequiredArgsConstructor
    public static final class Back extends OAbstractItem {
        private final OMenu back;

        @Override
        public ItemProvider getItemProvider() {
            return GO_BACK_ITEM;
        }

        @Override
        public void onItemClick(
            final @NotNull ClickType click, final @NotNull Player player, final @NotNull InventoryClickEvent event
        ) {
            back.openMenu(player);
        }
    }
}