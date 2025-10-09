package net.oceanias.opal.utility.builder;

import net.kyori.adventure.text.Component;
import net.oceanias.opal.utility.extension.OComponentExtension;
import net.oceanias.opal.utility.extension.OStringExtension;
import net.oceanias.opal.utility.helper.OTextHelper;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.*;

// TODO: Add potential missing methods? I'm not bothered to check.
@SuppressWarnings({ "unused", "UnstableApiUsage", "ResultOfMethodCallIgnored" })
@Accessors(fluent = true)
@ExtensionMethod({ OStringExtension.class, OComponentExtension.class })
public final class OItem extends ItemStack implements ItemProvider {
    private static final OItem FILLER = OItem.builder()
        .material(Material.BLACK_STAINED_GLASS_PANE)
        .name("")
        .flagsAll()
        .tooltipHidden(true)
        .build();

    private OItem() {
        super(Material.AIR);
    }

    @Contract(" -> new")
    public static @NotNull OItemBuilder builder() {
        return new OItemBuilder();
    }

    public static OItemBuilder builder(final Material material) {
        return new OItemBuilder().material(material);
    }

    @Override
    public @NotNull ItemStack get(final String language) {
        return this;
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class OItemBuilder {
        private Material material = Material.AIR;
        private int amount = 1;

        private String name;
        private final List<String> lore = new ArrayList<>();
        private boolean divider = true;

        private Integer modelData;
        private NamespacedKey modelKey;

        private final Map<Enchantment, Integer> enchantments = new LinkedHashMap<>();
        private final Set<ItemFlag> flags = new LinkedHashSet<>();
        private Boolean unbreakable;

        private Integer damageNow;
        private Integer damageMax;

        private final Multimap<Attribute, AttributeModifier> attributeModifiers = HashMultimap.create();

        private Integer maxStackSize;

        private ItemRarity rarity;
        private FoodComponent food;
        private ToolComponent tool;
        private JukeboxPlayableComponent jukebox;
        private EquippableComponent equippable;

        private Integer enchantable;
        private Boolean glider;

        private UseCooldownComponent useCooldown;
        private ItemStack useRemainder;

        private Boolean glintOverride;
        private Tag<DamageType> damageResistant;

        private Boolean tooltipHidden;
        private NamespacedKey tooltipStyle;

        private OfflinePlayer skullOwner;
        private PlayerProfile playerProfile;

        private PotionType potionType;
        private Color potionColour;
        private final List<PotionEffect> potionEffects = new ArrayList<>();

        private String bookTitle;
        private String bookAuthor;
        private final List<String> bookPages = new ArrayList<>();
        private BookMeta.Generation bookGeneration;

        private Color leatherColor;

        private List<Pattern> bannerPatterns;

        private final List<FireworkEffect> fireworkEffects = new ArrayList<>();
        private Integer fireworkPower;

        private Color mapColour;
        private Boolean mapScaling;

        private Location lodestoneLocation;
        private Boolean lodestoneTracked;

        private TropicalFish.Pattern fishPatternType;
        private DyeColor fishBodyColour;
        private DyeColor fishPatternColour;

        private List<ItemStack> crossbowProjectiles;
        private final List<SuspiciousEffectEntry> stewEffects = new ArrayList<>();

        private ArmorTrim trim;
        private MusicInstrument instrument;

        private Integer ominousBottleAmplifier;

        public @NotNull OItem build() {
            final OItem item = (OItem) new OItem().withType(material);
            final ItemMeta meta = item.getItemMeta();

            item.setAmount(amount);

            if (meta == null) {
                return item;
            }

            if (name != null) {
                meta.displayName(name.deserialize().stripAbsentDecorations());
            }

            if (!lore.isEmpty()) {
                final List<Component> lines = new ArrayList<>();

                if (divider) {
                    lines.add(OTextHelper.LORE_DIVIDER_LONG.deserialize().stripAbsentDecorations());
                }

                for (final String line : lore) {
                    lines.add(line.deserialize().stripAbsentDecorations());
                }

                meta.lore(lines);
            }

            if (modelData != null) {
                meta.setCustomModelData(modelData);
            }

            if (modelKey != null) {
                meta.setItemModel(modelKey);
            }

            if (!enchantments.isEmpty()) {
                for (final Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }

            if (!flags.isEmpty()) {
                meta.addItemFlags(flags.toArray(new ItemFlag[0]));
            }

            if (unbreakable != null) {
                meta.setUnbreakable(unbreakable);
            }

            if (!attributeModifiers.isEmpty()) {
                for (final Map.Entry<Attribute, AttributeModifier> entry : attributeModifiers.entries()) {
                    meta.addAttributeModifier(entry.getKey(), entry.getValue());
                }
            }

            if (damageNow != null && meta instanceof final Damageable damageable) {
                damageable.setDamage(damageNow);
            }

            if (damageMax != null && meta instanceof final Damageable damageable) {
                damageable.setMaxDamage(damageMax);
            }

            if (maxStackSize != null) {
                meta.setMaxStackSize(maxStackSize);
            }

            if (rarity != null) {
                meta.setRarity(rarity);
            }

            if (food != null && meta.hasFood()) {
                meta.setFood(food);
            }

            if (tool != null && meta.hasTool()) {
                meta.setTool(tool);
            }

            if (jukebox != null && meta.hasJukeboxPlayable()) {
                meta.setJukeboxPlayable(jukebox);
            }

            if (equippable != null && meta.hasEquippable()) {
                meta.setEquippable(equippable);
            }

            if (enchantable != null) {
                meta.setEnchantable(enchantable);
            }

            if (glider != null) {
                meta.setGlider(glider);
            }

            if (useCooldown != null && meta.hasUseCooldown()) {
                meta.setUseCooldown(useCooldown);
            }

            if (useRemainder != null) {
                meta.setUseRemainder(useRemainder);
            }

            if (glintOverride != null) {
                meta.setEnchantmentGlintOverride(glintOverride);
            }

            if (damageResistant != null) {
                meta.setDamageResistant(damageResistant);
            }

            if (tooltipStyle != null) {
                meta.setTooltipStyle(tooltipStyle);
            }

            if (tooltipHidden != null) {
                meta.setHideTooltip(tooltipHidden);
            }

            if (meta instanceof final SkullMeta skull) {
                if (skullOwner != null) {
                    skull.setOwningPlayer(skullOwner);
                }

                if (playerProfile != null) {
                    skull.setPlayerProfile(playerProfile);
                }
            }

            if (meta instanceof final PotionMeta potion) {
                if (potionType != null) {
                    potion.setBasePotionType(potionType);
                }

                if (potionColour != null) {
                    potion.setColor(potionColour);
                }

                if (!potionEffects.isEmpty()) {
                    for (final PotionEffect effect : potionEffects) {
                        potion.addCustomEffect(effect, true);
                    }
                }
            }

            if (meta instanceof final BookMeta book) {
                if (bookTitle != null) {
                    book.title(bookTitle.deserialize());
                }

                if (bookAuthor != null) {
                    book.author(bookAuthor.deserialize());
                }

                if (!bookPages.isEmpty()) {
                    final List<Component> pages = new ArrayList<>();

                    for (final String page : bookPages) {
                        pages.add(page.deserialize());
                    }

                    book.pages(pages);
                }

                if (bookGeneration != null) {
                    book.setGeneration(bookGeneration);
                }
            }

            if (meta instanceof final LeatherArmorMeta leather) {
                if (leatherColor != null) {
                    leather.setColor(leatherColor);
                }
            }

            if (meta instanceof final BannerMeta banner) {
                if (bannerPatterns != null) {
                    banner.setPatterns(bannerPatterns);
                }
            }

            if (meta instanceof final FireworkMeta firework) {
                if (!fireworkEffects.isEmpty()) {
                    firework.addEffects(fireworkEffects);
                }

                if (fireworkPower != null) {
                    firework.setPower(fireworkPower);
                }
            }

            if (meta instanceof final MapMeta map) {
                if (mapColour != null) {
                    map.setColor(mapColour);
                }

                if (mapScaling != null) {
                    map.setScaling(mapScaling);
                }
            }

            if (meta instanceof final CompassMeta compass) {
                if (lodestoneLocation != null) {
                    compass.setLodestone(lodestoneLocation);
                }
                if (lodestoneTracked != null) {
                    compass.setLodestoneTracked(lodestoneTracked);
                }
            }

            if (meta instanceof final TropicalFishBucketMeta fish) {
                if (fishPatternType != null) {
                    fish.setPattern(fishPatternType);
                }
                if (fishBodyColour != null) {
                    fish.setBodyColor(fishBodyColour);
                }
                if (fishPatternColour != null) {
                    fish.setPatternColor(fishPatternColour);
                }
            }

            if (meta instanceof final CrossbowMeta crossbow) {
                if (crossbowProjectiles != null) {
                    crossbow.setChargedProjectiles(crossbowProjectiles);
                }
            }

            if (meta instanceof final SuspiciousStewMeta stew) {
                if (!stewEffects.isEmpty()) {
                    for (final SuspiciousEffectEntry effect : stewEffects) {
                        stew.addCustomEffect(effect, true);
                    }
                }
            }

            if (meta instanceof final ArmorMeta armour) {
                if (trim != null) {
                    armour.setTrim(trim);
                }
            }

            if (meta instanceof final MusicInstrumentMeta music) {
                if (instrument != null) {
                    music.setInstrument(instrument);
                }
            }

            if (meta instanceof final OminousBottleMeta ominous) {
                if (ominousBottleAmplifier != null) {
                    ominous.setAmplifier(ominousBottleAmplifier);
                }
            }

            item.setItemMeta(meta);

            return item;
        }

        public OItemBuilder lore(final String... lines) {
            lore.addAll(List.of(lines));

            return this;
        }

        public OItemBuilder lore(final List<String> lines) {
            lore.addAll(lines);

            return this;
        }

        public OItemBuilder lore(final String line) {
            lore.add(line);

            return this;
        }

        public OItemBuilder enchant(final Enchantment enchantment, final int level) {
            enchantments.put(enchantment, level);

            return this;
        }

        public OItemBuilder glint() {
            return enchant(Enchantment.MENDING, 1);
        }

        public OItemBuilder flags(final ItemFlag... flags) {
            this.flags.addAll(List.of(flags));

            return this;
        }

        public OItemBuilder flags(final List<ItemFlag> flags) {
            this.flags.addAll(flags);

            return this;
        }

        public OItemBuilder flags(final ItemFlag flag) {
            flags.add(flag);

            return this;
        }

        public OItemBuilder flagsAll() {
            flags.addAll(List.of(ItemFlag.values()));

            return this;
        }

        public OItemBuilder attributeModifier(final Attribute attribute, final AttributeModifier modifier) {
            attributeModifiers.put(attribute, modifier);

            return this;
        }

        public OItemBuilder attributeModifiersNone() {
            attributeModifiers.clear();

            return this;
        }

        public OItemBuilder potionEffects(final PotionEffect... effects) {
            potionEffects.addAll(List.of(effects));

            return this;
        }

        public OItemBuilder potionEffects(final List<PotionEffect> effects) {
            potionEffects.addAll(effects);

            return this;
        }

        public OItemBuilder potionEffect(final PotionEffect effect) {
            potionEffects.add(effect);

            return this;
        }

        public OItemBuilder bookPages(final String... pages) {
            bookPages.addAll(List.of(pages));

            return this;
        }

        public OItemBuilder bookPages(final List<String> pages) {
            bookPages.addAll(pages);

            return this;
        }

        public OItemBuilder bookPage(final String page) {
            bookPages.add(page);

            return this;
        }

        public OItemBuilder fireworkEffects(final FireworkEffect... effects) {
            fireworkEffects.addAll(List.of(effects));

            return this;
        }

        public OItemBuilder fireworkEffects(final List<FireworkEffect> effects) {
            fireworkEffects.addAll(effects);

            return this;
        }

        public OItemBuilder fireworkEffect(final FireworkEffect effect) {
            fireworkEffects.add(effect);

            return this;
        }

        public OItemBuilder stewEffects(final SuspiciousEffectEntry... effects) {
            stewEffects.addAll(List.of(effects));

            return this;
        }

        public OItemBuilder stewEffects(final List<SuspiciousEffectEntry> effects) {
            stewEffects.addAll(effects);

            return this;
        }

        public OItemBuilder stewEffect(final SuspiciousEffectEntry effect) {
            stewEffects.add(effect);

            return this;
        }
    }
}