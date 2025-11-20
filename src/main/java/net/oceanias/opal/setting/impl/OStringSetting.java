package net.oceanias.opal.setting.impl;

import net.oceanias.opal.listener.OListener;
import net.oceanias.opal.menu.OMenu;
import net.oceanias.opal.setting.OSetting;
import net.oceanias.opal.utility.builder.OItem;
import net.oceanias.opal.utility.builder.OMessage;
import net.oceanias.opal.utility.builder.OSound;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import net.oceanias.opal.utility.extension.OComponentExtension;
import net.oceanias.opal.utility.helper.OTaskHelper;
import net.oceanias.opal.utility.helper.OTextHelper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.window.Window;
import xyz.xenondevs.invui.window.WindowManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OComponentExtension.class, OCommandSenderExtension.class })
@Getter
public final class OStringSetting extends OSetting<String> {
    private transient Integer limit;

    @Override
    public OStringSetting name(final String name) {
        super.name(name);

        return this;
    }

    @Override
    public OStringSetting initial(final String initial) {
        super.initial(initial);

        return this;
    }

    @Override
    public OStringSetting description(final List<String> description) {
        super.description(description);

        return this;
    }

    @Override
    public OStringSetting material(final Material material) {
        super.material(material);

        return this;
    }

    @Override
    public OStringSetting value(String value) {
        if (limit != null && value.length() > limit) {
            value = value.substring(0, limit);
        }

        this.value = value;

        return this;
    }

    @Contract(" -> new")
    @Override
    public @NotNull xyz.xenondevs.invui.item.Item item() {
        return new Item(this);
    }

    @RequiredArgsConstructor
    public static class Item extends AbstractItem {
        private final OStringSetting setting;

        @Override
        public ItemProvider getItemProvider(final Player viewer) {
            final Material material = setting.material;

            final List<String> description = setting.description;
            final List<String> lore = new ArrayList<>();

            final Integer limit = setting.limit;

            final Material type = material != null
                ? material
                : Material.NAME_TAG;

            if (description != null && !description.isEmpty()) {
                lore.addAll(description.stream().map(line -> "&7" + line).toList());
                lore.add("");
            }

            addCurrentLore(lore, setting.value);

            if (limit != null) {
                lore.add("");
                lore.add("&fLimit: &c" + limit + " characters");
            }

            lore.add("");
            lore.add("&eClick &7to change!");

            return OItem.builder(type)
                .name("&e" + setting.name)
                .lore(lore)
                .flagsAll()
                .build();
        }

        @Override
        public void handleClick(
            @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
        ) {
            final Window window = WindowManager.getInstance().getOpenWindow(player);

            final OMenu menu = event.getInventory().getHolder() instanceof final OMenu opal
                ? opal
                : null;

            if (window != null) {
                window.close();
            } else {
                player.closeInventory();
            }

            OMessage.builder()
                .line("&aEnter a new value in the chat!")
                .line("&7→ &fType &ccancel &fto cancel.")
                .blanks(true)
                .sound(OSound.Preset.SUCCESS)
                .build()
                .send(player);

            Listener.awaitInput(player, setting, menu);
        }

        private void addCurrentLore(final @NotNull List<String> lore, final @NotNull String value) {
            lore.add("&eCurrent:");

            final int limit = OTextHelper.LORE_DIVIDER_LONG
                .replaceAll(OTextHelper.COLOUR_CODE_REGEX, "")
                .length();

            if (value.replaceAll(OTextHelper.COLOUR_CODE_REGEX, "").length() <= limit) {
                lore.add("&7• &6" + value);

                return;
            }

            final String[] words = value.split(" ");
            final StringBuilder builder = new StringBuilder();

            for (final String word : words) {
                final String test = builder.isEmpty() ? word : builder + " " + word;

                if (test.replaceAll(OTextHelper.COLOUR_CODE_REGEX, "").length() > limit) {
                    if (!builder.isEmpty()) {
                        lore.add("&7• &6" + builder);

                        builder.setLength(0);
                        builder.append(word);
                    } else {
                        lore.add("&7• &6" + word);
                    }
                } else {
                    if (!builder.isEmpty()) {
                        builder.append(" ");
                    }

                    builder.append(word);
                }
            }

            if (builder.isEmpty()) {
                return;
            }

            lore.add("&7• &6" + builder);
        }
    }

    @RequiredArgsConstructor
    public static final class Listener extends OListener.Bukkit {
        private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(45);
        private static final String CANCEL_KEYWORD = "cancel";

        private static final Map<UUID, OStringSetting> awaitingInput = new ConcurrentHashMap<>();
        private static final Map<UUID, BukkitTask> timeoutTasks = new ConcurrentHashMap<>();
        private static final Map<UUID, OMenu> previousMenus = new ConcurrentHashMap<>();

        private static void awaitInput(final @NotNull Player player, final OStringSetting setting, final OMenu menu) {
            final UUID uuid = player.getUniqueId();

            cancelTimeout(player);

            awaitingInput.put(uuid, setting);

            final BukkitTask task = OTaskHelper.runTaskLater(() -> {
                if (awaitingInput.remove(uuid) == null) {
                    return;
                }

                timeoutTasks.remove(uuid);
                previousMenus.remove(uuid);

                OMessage.builder()
                    .line("&fThe input has &ctimed out&f!")
                    .blanks(true)
                    .sound(OSound.Preset.ERROR)
                    .build()
                    .send(player);
            }, TIMEOUT_DURATION);

            timeoutTasks.put(uuid, task);
            previousMenus.put(uuid, menu);
        }

        private static void cancelTimeout(final @NotNull Player player) {
            final BukkitTask task = timeoutTasks.remove(player.getUniqueId());

            if (task == null) {
                return;
            }

            task.cancel();
        }

        private static void leaveInput(final @NotNull Player player) {
            final UUID uuid = player.getUniqueId();

            awaitingInput.remove(uuid);

            cancelTimeout(player);

            previousMenus.remove(uuid);
        }

        @EventHandler
        public void onAsyncChat(final @NotNull AsyncChatEvent event) {
            final Player player = event.getPlayer();
            final UUID uuid = player.getUniqueId();

            if (!awaitingInput.containsKey(uuid)) {
                return;
            }

            final OStringSetting setting = awaitingInput.get(uuid);

            final Integer limit = setting.limit;
            final String message = event.message().serialize();

            event.setCancelled(true);

            if (limit != null && !message.equalsIgnoreCase(CANCEL_KEYWORD) && message.length() > limit) {
                OMessage.builder()
                    .line("&fYour input is &ctoo long&f! The limit is &6" + limit + " characters&f.")
                    .blanks(true)
                    .sound(OSound.Preset.ERROR)
                    .build()
                    .send(player);

                return;
            }

            final OMenu menu = previousMenus.get(uuid);

            if (menu != null) {
                menu.openMenu(player);
            }

            if (message.equalsIgnoreCase(CANCEL_KEYWORD)) {
                OMessage.builder()
                    .line("&fYou have &ccancelled &fthe input!")
                    .blanks(true)
                    .sound(OSound.Preset.ERROR)
                    .build()
                    .send(player);

                leaveInput(player);

                return;
            }

            setting.value(message);

            OSound.builder().sound(Sound.BLOCK_NOTE_BLOCK_HARP).build().play(player);

            leaveInput(player);
        }

        @EventHandler
        public void onQuit(final @NotNull PlayerQuitEvent event) {
            leaveInput(event.getPlayer());
        }
    }
}