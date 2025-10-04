package net.oceanias.opal.setting.impl;

import net.oceanias.opal.setting.OSetting;
import net.oceanias.opal.utility.builder.OItemBuilder;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod(OCommandSenderExtension.class)
public final class OBooleanSetting extends OSetting<Boolean, OBooleanSetting> {
    public OBooleanSetting(final String pretty, final boolean initial) {
        super(pretty, initial);
    }

    @Contract(" -> new")
    @Override
    public @NotNull xyz.xenondevs.invui.item.Item item() {
        return new Item(this);
    }

    @RequiredArgsConstructor
    public static class Item extends AbstractItem {
        private final OBooleanSetting setting;

        @Override
        public ItemProvider getItemProvider(final Player viewer) {
            final Boolean value = setting.value;
            final Material material = setting.material;
            final String state = value ? "&aEnabled" : "&cDisabled";

            final List<String> description = setting.description;
            final List<String> lore = new ArrayList<>();

            final Material type = material != null
                ? material
                : (value ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE);

            if (description != null && !description.isEmpty()) {
                lore.addAll(description.stream().map(line -> "&7" + line).toList());
                lore.add("");
            }

            lore.add("&fState: " + state);

            lore.add("");
            lore.add("&eClick &7to toggle!");

            return new OItemBuilder(type)
                .setName("&e" + setting.pretty)
                .addLore(lore)
                .hideFlags();
        }

        @Override
        public void handleClick(
            @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
        ) {
            setting.value(!setting.value);

            player.soundDSR(Sound.BLOCK_NOTE_BLOCK_PLING);

            notifyWindows();
        }
    }
}