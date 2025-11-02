package net.oceanias.opal.setting.impl;

import net.oceanias.opal.setting.OSetting;
import net.oceanias.opal.utility.builder.OItem;
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
public final class OBooleanSetting extends OSetting<Boolean> {
    public OBooleanSetting() {
        super(false);
    }

    @Override
    public OBooleanSetting name(final String name) {
        super.name(name);

        return this;
    }

    @Override
    public OBooleanSetting initial(final Boolean initial, final boolean overwriteCurrentValue) {
        super.initial(initial, overwriteCurrentValue);

        return this;
    }

    @Override
    public OBooleanSetting description(final List<String> description) {
        super.description(description);

        return this;
    }

    @Override
    public OBooleanSetting material(final Material material) {
        super.material(material);

        return this;
    }

    @Override
    public OBooleanSetting value(final Boolean value) {
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
            setting.value(!setting.value);

            player.soundDSR(Sound.BLOCK_NOTE_BLOCK_PLING);

            notifyWindows();
        }
    }
}