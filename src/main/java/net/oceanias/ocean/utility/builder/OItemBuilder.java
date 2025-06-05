package net.oceanias.ocean.utility.builder;

import net.oceanias.ocean.utility.extension.OComponentExtension;
import net.oceanias.ocean.utility.extension.OStringExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import net.kyori.adventure.text.Component;
import xyz.xenondevs.invui.item.ItemProvider;
import com.google.common.collect.HashMultimap;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OStringExtension.class, OComponentExtension.class })
public class OItemBuilder implements ItemProvider {
    private final ItemStack stack;

    @Getter
    private static final OItemBuilder border = new OItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
        .setName("")
        .hideAll();

    public OItemBuilder(final Material material) {
        stack = new ItemStack(material);
    }

    public OItemBuilder(final Material material, final int amount) {
        stack = new ItemStack(material, amount);
    }

    public OItemBuilder(final ItemStack stack) {
        this.stack = stack;
    }

    public OItemBuilder setName(final String name) {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.displayName(name.deserialize().stripAbsentDecorations());

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder addLines(final List<String> lines, final boolean divider) {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            final List<Component> lore = meta.hasLore()
                ? new ArrayList<>(Objects.requireNonNull(meta.lore()))
                : new ArrayList<>();

            if (divider) {
                lore.add(OStringExtension.LORE_DIVIDER.deserialize());
            }

            for (final String line : lines) {
                lore.add(line.deserialize().stripAbsentDecorations());
            }

            meta.lore(lore);

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder addLines(final List<String> lines) {
        return addLines(lines, true);
    }

    public OItemBuilder setUnbreakable(final boolean unbreakable) {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.setUnbreakable(unbreakable);

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder addEnchantment(final Enchantment enchant, final int level, final boolean restricted) {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.addEnchant(enchant, level, restricted);

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder addGlint() {
        return addEnchantment(Enchantment.MENDING, 1, false);
    }

    public OItemBuilder addModel(final int model) {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(model);

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder setPotionType(final PotionType type) {
        final ItemMeta meta = stack.getItemMeta();

        if (meta instanceof final PotionMeta potion) {
            potion.setBasePotionType(type);

            stack.setItemMeta(potion);
        }

        return this;
    }

    public OItemBuilder hideAll() {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.setHideTooltip(true);
            meta.setAttributeModifiers(HashMultimap.create());
            meta.addItemFlags(ItemFlag.values());

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder addCommon(
        final String name, final List<String> lines, final boolean filler, final boolean glint
    ) {
        setName(name)
            .addLines(lines, filler)
            .setUnbreakable(true)
            .hideAll();

        if (glint) {
            addGlint();
        }

        return this;
    }

    public OItemBuilder addCommon(final String name, final List<String> lines, final boolean filler) {
        return addCommon(name, lines, filler, true);
    }

    public OItemBuilder addCommon(final String name, final List<String> lines) {
        return addCommon(name, lines, true, true);
    }

    @Override
    public @NotNull ItemStack get(final String language) {
        return stack;
    }
}