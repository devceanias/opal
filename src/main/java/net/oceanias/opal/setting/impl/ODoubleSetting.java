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
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod(OCommandSenderExtension.class)
@Getter
public final class ODoubleSetting extends OSetting<Double, ODoubleSetting> {
    private final Double min;
    private final Double max;

    public ODoubleSetting(final String pretty, final double initial) {
        this(pretty, initial, null, null);
    }

    public ODoubleSetting(final String pretty, final double initial, final Double min, final Double max) {
        super(pretty, initial);

        this.min = min;
        this.max = max;
    }

    @Override
    public void value(Double value) {
        if (min != null && value < min) {
            value = min;
        }

        if (max != null && value > max) {
            value = max;
        }

        this.value = value;
    }

    @Contract(" -> new")
    @Override
    public @NotNull xyz.xenondevs.invui.item.Item item() {
        return new Item(this);
    }

    public static class Item extends AbstractItem {
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

            if (min != null) {
                lore.add("&fMinimum: &c" + String.format("%.2f", min));
            }

            if (max != null) {
                lore.add("&fMaximum: &c" + String.format("%.2f", max));
            }

            lore.add("");
            lore.add("&eLeft-click &7to add &n" + change + "&7.");
            lore.add("&eRight-click &7to subtract &n" + change + "&7.");
            lore.add("&eShift-click &7to change by &n" + (change * 10.0) + "&7.");

            return new OItemBuilder(type)
                .setName("&e" + setting.pretty)
                .addLore(lore)
                .hideFlags();
        }

        @Override
        public void handleClick(
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

            player.soundDSR(Sound.BLOCK_NOTE_BLOCK_CHIME);

            notifyWindows();
        }
    }
}