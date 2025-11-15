package net.oceanias.opal.utility.builder;

import net.oceanias.opal.utility.extension.OComponentExtension;
import net.oceanias.opal.utility.extension.OStringExtension;
import net.oceanias.opal.utility.helper.OTextHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import net.kyori.adventure.text.Component;
import xyz.xenondevs.invui.item.ItemProvider;
import com.google.common.collect.HashMultimap;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.NotNull;

/**
 * @deprecated Use {@link OItem} instead.
 * This class will be removed in a future version.
 */
@Deprecated
@SuppressWarnings("unused")
@ExtensionMethod({ OStringExtension.class, OComponentExtension.class })
public final class OItemBuilder implements ItemProvider {
    private final ItemStack stack;

    @Getter
    private static final OItemBuilder filler = new OItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
        .setName("")
        .hideFlags()
        .hideTooltip();

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
            meta.displayName(name.deserialize(true));

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder setAmount(final int amount) {
        stack.setAmount(amount);

        return this;
    }

    public OItemBuilder setOwner(final OfflinePlayer owner) {
        final ItemMeta meta = stack.getItemMeta();

        if (meta instanceof final SkullMeta skull) {
            skull.setOwningPlayer(owner);

            stack.setItemMeta(skull);
        }

        return this;
    }

    public OItemBuilder addLore(final List<String> lines, final boolean divider) {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            final List<Component> lore = meta.hasLore()
                ? new ArrayList<>(Objects.requireNonNull(meta.lore()))
                : new ArrayList<>();

            if (divider) {
                lore.add(OTextHelper.LORE_DIVIDER_LONG.deserialize());
            }

            for (final String line : lines) {
                lore.add(line.deserialize(true));
            }

            meta.lore(lore);

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder addLore(final List<String> lines) {
        return addLore(lines, true);
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

    public OItemBuilder hideFlags() {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.setAttributeModifiers(HashMultimap.create());
            meta.addItemFlags(ItemFlag.values());

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder hideTooltip() {
        final ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            meta.setHideTooltip(true);

            stack.setItemMeta(meta);
        }

        return this;
    }

    public OItemBuilder addCommon(
        final String name, final List<String> lines, final boolean filler, final boolean glint
    ) {
        setName(name)
            .addLore(lines, filler)
            .setUnbreakable(true)
            .hideFlags();

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