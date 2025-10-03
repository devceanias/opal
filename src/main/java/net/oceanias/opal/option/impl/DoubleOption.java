package net.oceanias.opal.option.impl;

import net.oceanias.opal.option.Option;
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
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod(OCommandSenderExtension.class)
@Getter
public final class DoubleOption extends Option<Double> {
    private final Double min;
    private final Double max;

    public DoubleOption(final String pretty, final double initial) {
        this(pretty, initial, null, null);
    }

    public DoubleOption(final String pretty, final double initial, final Double min, final Double max) {
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

    @Override
    public Type type() {
        return Type.DOUBLE;
    }

    public class Item extends AbstractItem {
        private final DoubleOption option;
        private final double change;

        public Item(final DoubleOption option) {
            this(option, 0.5);
        }

        public Item(final DoubleOption option, final double change) {
            this.option = option;
            this.change = Math.round(change * 10000.0) / 10000.0;
        }

        @Override
        public ItemProvider getItemProvider(final Player viewer) {
            final Material material = option.material;

            final List<String> description = option.description;
            final List<String> lore = new ArrayList<>();

            final Double min = option.min;
            final Double max = option.max;

            final Material type = material != null
                ? material
                : Material.PAPER;

            if (description != null && !description.isEmpty()) {
                lore.addAll(description.stream().map(line -> "&7" + line).toList());
                lore.add("");
            }

            lore.add("&fCurrent: &6" + String.format("%.2f", option.value));

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
                .setName("&e" + option.pretty)
                .addLore(lore);
        }

        @Override
        public void handleClick(
            @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
        ) {
            final double current = option.value;

            double change = this.change;

            if (click.isShiftClick()) {
                change *= 10.0;
            }

            if (click.isLeftClick()) {
                option.value(current + change);
            } else if (click.isRightClick()) {
                option.value(current - change);
            }

            player.soundDSR(Sound.BLOCK_NOTE_BLOCK_CHIME);

            notifyWindows();
        }
    }
}