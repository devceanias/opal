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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@ExtensionMethod(OCommandSenderExtension.class)
@SuppressWarnings("unused")
@Getter
@Setter
@Accessors(fluent = true)
public final class OChoiceSetting extends OSetting<String> {
    private transient List<String> choices;

    public OChoiceSetting() {
        super("Option 1");
    }

    @Override
    public OChoiceSetting name(final String name) {
        super.name(name);

        return this;
    }

    @Override
    public OChoiceSetting initial(final String initial, final boolean overwriteCurrentValue) {
        super.initial(initial, overwriteCurrentValue);

        return this;
    }

    @Override
    public OChoiceSetting description(final List<String> description) {
        super.description(description);

        return this;
    }

    @Override
    public OChoiceSetting material(final Material material) {
        super.material(material);

        return this;
    }

    @Override
    public OChoiceSetting value(final String value) {
        if (choices == null) {
            throw new IllegalArgumentException("Error updating setting; the choices list is null.");
        }

        if (!choices.contains(value)) {
            throw new IllegalArgumentException("Error updating setting; new value must be in the choices list.");
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
        private final OChoiceSetting setting;

        @Override
        public ItemProvider getItemProvider(final Player viewer) {
            final List<String> choices = setting.choices;

            if (choices == null) {
                throw new IllegalArgumentException("Error constructing item; the choices list is null.");
            }

            final Material material = setting.material;

            final List<String> description = setting.description;
            final List<String> lore = new ArrayList<>();

            final Material type = material != null
                ? material
                : Material.COMPASS;

            if (description != null && !description.isEmpty()) {
                lore.addAll(description.stream().map(line -> "&7" + line).toList());
                lore.add("");
            }

            lore.add("&fCurrent: &6" + setting.value);
            lore.add("");
            lore.add("&eChoices:");

            for (final String choice : choices) {
                final String colour = choice.equals(setting.value) ? "&a" : "&7";

                lore.add(colour + "• " + choice);
            }

            lore.add("");
            lore.add("&eLeft-click &7to cycle forwards.");
            lore.add("&eRight-click &7to cycle backwards.");

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
            final List<String> choices = setting.choices;

            final int current = choices.indexOf(setting.value);
            final int size = choices.size();

            if (click.isLeftClick()) {
                setting.value(choices.get((current + 1) % size));
            } else if (click.isRightClick()) {
                setting.value(choices.get((current - 1 + size) % size));
            }

            player.soundDSR(Sound.BLOCK_NOTE_BLOCK_BIT);

            notifyWindows();
        }
    }
}