package net.oceanias.opal.setting.impl;

import net.oceanias.opal.menu.item.OAbstractItem;
import net.oceanias.opal.setting.OSetting;
import net.oceanias.opal.utility.builder.OItem;
import net.oceanias.opal.utility.builder.OSound;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.xenondevs.invui.item.ItemProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Getter
@Setter
@Accessors(fluent = true)
public final class ODoubleSetting extends OSetting<Double> {
    private transient Double min;
    private transient Double max;

    @Override
    public ODoubleSetting name(final String name) {
        super.name(name);

        return this;
    }

    @Override
    public ODoubleSetting initial(final Double initial) {
        super.initial(initial);

        return this;
    }

    @Override
    public ODoubleSetting description(final List<String> description) {
        super.description(description);

        return this;
    }

    @Override
    public ODoubleSetting material(final Material material) {
        super.material(material);

        return this;
    }

    @Override
    public ODoubleSetting value(Double value) {
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

    public static class Item extends OAbstractItem {
        private final ODoubleSetting setting;
        private final double change;

        public Item(final ODoubleSetting setting) {
            this(setting, 0.5);
        }

        public Item(final ODoubleSetting setting, final double change) {
            this.setting = setting;
            this.change = Math.round(change * 10000.0) / 10000.0;
        }

        @Override
        public ItemProvider getItemProvider(final Player viewer) {
            final Material material = setting.material;

            final List<String> description = setting.description;
            final List<String> lore = new ArrayList<>();

            final Double min = setting.min;
            final Double max = setting.max;

            final Material type = material != null
                ? material
                : Material.PAPER;

            if (description != null && !description.isEmpty()) {
                lore.addAll(description.stream().map(line -> "&7" + line).toList());
                lore.add("");
            }

            lore.add("&fCurrent: &6" + String.format("%.2f", setting.value));
            lore.add("");
            lore.add("&fMinimum: &c" + (min == null ? "None" : String.format("%.2f", min)));
            lore.add("&fMaximum: &c" + (max == null ? "None" : String.format("%.2f", max)));
            lore.add("");
            lore.add("&eLeft-click &7to add &n" + change + "&7.");
            lore.add("&eRight-click &7to subtract &n" + change + "&7.");
            lore.add("&eShift-click &7to change by &n" + (change * 10.0) + "&7.");

            return OItem.builder(type)
                .name("&e" + setting.name)
                .lore(lore)
                .flagsAll()
                .build();
        }

        @Override
        public void onItemClick(
            @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
        ) {
            final double current = setting.value;

            double change = this.change;

            if (click.isShiftClick()) {
                change *= 10.0;
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