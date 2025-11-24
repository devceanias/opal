package net.oceanias.opal.setting.impl;

import net.oceanias.opal.setting.OSetting;
import net.oceanias.opal.utility.builder.OItem;
import net.oceanias.opal.utility.builder.OSound;
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
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod(OCommandSenderExtension.class)
@Getter
@Setter
@Accessors(fluent = true)
public final class OIntegerSetting extends OSetting<Integer> {
    private transient Integer min;
    private transient Integer max;

    @Override
    public OIntegerSetting name(final String name) {
        super.name(name);

        return this;
    }

    @Override
    public OIntegerSetting initial(final Integer initial) {
        super.initial(initial);

        return this;
    }

    @Override
    public OIntegerSetting description(final List<String> description) {
        super.description(description);

        return this;
    }

    @Override
    public OIntegerSetting material(final Material material) {
        super.material(material);

        return this;
    }

    @Override
    public OIntegerSetting value(Integer value) {
        if (min != null && value < min) {
            value = min;
        }

        if (max != null && value > max) {
            value = max;
        }

        this.value = value;

        return this;
    }

    @Contract(" -> new")
    @Override
    public @NotNull xyz.xenondevs.invui.item.Item item() {
        return new Item(this);
    }

    public static class Item extends AbstractItem {
        private final OIntegerSetting setting;
        private final int change;

        public Item(final OIntegerSetting setting) {
            this(setting, 1);
        }

        public Item(final OIntegerSetting setting, final int change) {
            this.setting = setting;
            this.change = change;
        }

        @Override
        public ItemProvider getItemProvider(final Player viewer) {
            final Material material = setting.material;

            final List<String> description = setting.description;
            final List<String> lore = new ArrayList<>();

            final Integer min = setting.min;
            final Integer max = setting.max;

            final Material type = material != null
                ? material
                : Material.PAPER;

            if (description != null && !description.isEmpty()) {
                lore.addAll(description.stream().map(line -> "&7" + line).toList());
                lore.add("");
            }

            lore.add("&fCurrent: &6" + setting.value);
            lore.add("");
            lore.add("&fMinimum: &c" + (min == null ? "None" : min));
            lore.add("&fMaximum: &c" + (max == null ? "None" : max));
            lore.add("");
            lore.add("&eLeft-click &7to add &n" + change + "&7.");
            lore.add("&eRight-click &7to subtract &n" + change + "&7.");
            lore.add("&eShift-click &7to change by &n" + (change * 10) + "&7.");

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
            final int current = setting.value;

            int change = this.change;

            if (click.isShiftClick()) {
                change *= 10;
            }

            if (click.isLeftClick()) {
                setting.value(current + change);
            } else if (click.isRightClick()) {
                setting.value(current - change);
            }

            OSound.builder().sound(Sound.BLOCK_NOTE_BLOCK_CHIME).build().play(player);

            notifyWindows();
        }
    }
}