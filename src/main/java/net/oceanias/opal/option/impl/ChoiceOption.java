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
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@ExtensionMethod(OCommandSenderExtension.class)
@SuppressWarnings("unused")
@Getter
public final class ChoiceOption<T> extends Option<T> {
    private final List<T> choices;

    public ChoiceOption(final String pretty, final T initial, final @NotNull List<T> choices) {
        super(pretty, initial);

        this.choices = choices;

        if (choices.contains(initial)) {
            return;
        }

        throw new IllegalArgumentException("Error creating option; initial value must be in choices.");
    }

    @Override
    public void value(final T value) {
        if (!choices.contains(value)) {
            return;
        }

        this.value = value;
    }

    @Override
    public Type type() {
        return Type.CHOICE;
    }

    @RequiredArgsConstructor
    public static final class Item<T> extends AbstractItem {
        private final ChoiceOption<T> option;

        @Override
        public ItemProvider getItemProvider(final Player viewer) {
            final Material material = option.material;

            final List<String> description = option.description;
            final List<String> lore = new ArrayList<>();

            final Material type = material != null
                ? material
                : Material.COMPASS;

            if (description != null && !description.isEmpty()) {
                lore.addAll(description.stream().map(line -> "&7" + line).toList());
                lore.add("");
            }

            lore.add("&fCurrent: &6" + option.value);
            lore.add("");
            lore.add("&fChoices:");

            for (final T choice : option.choices) {
                final String colour = choice.equals(option.value) ? "&a" : "&7";

                lore.add(colour + "• " + choice);
            }

            lore.add("");
            lore.add("&eLeft-click &7to cycle forwards.");
            lore.add("&eRight-click &7to cycle backwards.");

            return new OItemBuilder(type)
                .setName("&e" + option.pretty)
                .addLore(lore);
        }

        @Override
        public void handleClick(
            @NotNull final ClickType click, @NotNull final Player player, @NotNull final InventoryClickEvent event
        ) {
            final List<T> choices = option.choices;

            final int current = choices.indexOf(option.value);
            final int size = choices.size();

            if (click.isLeftClick()) {
                option.value(choices.get((current + 1) % size));
            } else if (click.isRightClick()) {
                option.value(choices.get((current - 1 + size) % size));
            }

            player.soundDSR(Sound.BLOCK_NOTE_BLOCK_BIT);

            notifyWindows();
        }
    }
}