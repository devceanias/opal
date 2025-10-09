package net.oceanias.opal.utility.builder;

import net.kyori.adventure.text.Component;
import net.oceanias.opal.OPlugin;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

// TODO: Add missing meta types (e.g. ShieldMeta).
@SuppressWarnings({ "unused", "UnstableApiUsage", "ResultOfMethodCallIgnored", "UnusedReturnValue" })
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

    public static OItemBuilder builder(final ItemStack stack) {
        return new OItemBuilder().from(stack);
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
        private final List<PotionEffect> stewEffects = new ArrayList<>();

        private ArmorTrim trim;
        private MusicInstrument instrument;

        private Integer ominousBottleAmplifier;

        private final Map<NamespacedKey, PersistentDataEntry<?>> newPersistentData = new LinkedHashMap<>();
        private byte[] oldPersistentData;

        private record PersistentDataEntry<T>(PersistentDataType<?, T> type, T value) {}

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
                    for (final PotionEffect effect : stewEffects) {
                        stew.addCustomEffect(
                            SuspiciousEffectEntry.create(effect.getType(), effect.getDuration()), true
                        );
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

            final PersistentDataContainer container = meta.getPersistentDataContainer();

            if (oldPersistentData != null) {
                try {
                    container.readFromBytes(oldPersistentData);
                } catch (final IOException exception) {
                    OPlugin.get().getLogger().log(
                        Level.SEVERE,
                        "Error reading persistent data container:" + exception.getMessage() + "!",
                        exception
                    );
                }
            }

            if (!newPersistentData.isEmpty()) {
                for (final Map.Entry<NamespacedKey, PersistentDataEntry<?>> entry : newPersistentData.entrySet()) {
                    setPersistentData(container, entry.getKey(), entry.getValue());
                }
            }

            item.setItemMeta(meta);

            return item;
        }

        @SuppressWarnings("unchecked")
        private static <T> void setPersistentData(
            final PersistentDataContainer pdc,
            final NamespacedKey key,
            final PersistentDataEntry<?> entry
        ) {
            pdc.set(key, (PersistentDataType<?, T>) entry.type(), (T) entry.value());
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

        public OItemBuilder stewEffects(final PotionEffect... effects) {
            stewEffects.addAll(List.of(effects));

            return this;
        }

        public OItemBuilder stewEffects(final List<PotionEffect> effects) {
            stewEffects.addAll(effects);

            return this;
        }

        public OItemBuilder stewEffect(final PotionEffect effect) {
            stewEffects.add(effect);

            return this;
        }

        public <P, C> OItemBuilder newPersistentData(
            final NamespacedKey key,
            final PersistentDataType<P, C> type,
            final C value
        ) {
            newPersistentData.put(key, new PersistentDataEntry<>(type, value));

            return this;
        }

        public OItemBuilder persistentDataNone() {
            newPersistentData.clear();

            return this;
        }

        @Contract("_ -> this")
        public OItemBuilder from(final @NotNull ItemStack stack) {
            final ItemMeta meta = stack.getItemMeta();

            material(stack.getType());
            amount(stack.getAmount());

            if (meta == null) {
                return this;
            }

            if (meta.hasItemName()) {
                name(meta.itemName().serialize());
            }

            if (meta.hasLore()) {
                final List<Component> lines = meta.lore();

                if (lines != null) {
                    for (final Component line : lines) {
                        lore(line.serialize());
                    }
                }
            }

            if (meta.hasCustomModelData()) {
                modelData(meta.getCustomModelData());
            }

            if (meta.hasItemModel()) {
                modelKey(meta.getItemModel());
            }

            if (meta.hasEnchants()) {
                for (final Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    enchant(entry.getKey(), entry.getValue());
                }
            }

            flags.addAll(meta.getItemFlags());

            if (meta.isUnbreakable()) {
                unbreakable(true);
            }

            if (meta instanceof final Damageable damageable) {
                if (damageable.hasDamage()) {
                    damageNow(damageable.getDamage());
                }

                if (damageable.hasMaxDamage()) {
                    damageMax(damageable.getMaxDamage());
                }
            }

            if (meta.hasAttributeModifiers()) {
                final Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();

                if (modifiers != null) {
                    for (final Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
                        attributeModifier(entry.getKey(), entry.getValue());
                    }
                }
            }

            if (meta.hasMaxStackSize()) {
                maxStackSize(meta.getMaxStackSize());
            }

            if (meta.hasRarity()) {
                rarity(meta.getRarity());
            }

            if (meta.hasFood()) {
                food(meta.getFood());
            }

            if (meta.hasTool()) {
                tool(meta.getTool());
            }

            if (meta.hasJukeboxPlayable()) {
                jukebox(meta.getJukeboxPlayable());
            }

            if (meta.hasEquippable()) {
                equippable(meta.getEquippable());
            }

            if (meta.hasEnchantable()) {
                enchantable(meta.getEnchantable());
            }

            if (meta.isGlider()) {
                glider(true);
            }

            if (meta.hasUseCooldown()) {
                useCooldown(meta.getUseCooldown());
            }

            if (meta.hasUseRemainder()) {
                useRemainder(meta.getUseRemainder());
            }

            if (meta.hasEnchantmentGlintOverride()) {
                glintOverride(meta.getEnchantmentGlintOverride());
            }

            if (meta.hasDamageResistant()) {
                damageResistant(meta.getDamageResistant());
            }

            if (meta.hasTooltipStyle()) {
                tooltipStyle(meta.getTooltipStyle());
            }

            if (meta.isHideTooltip()) {
                tooltipHidden(true);
            }

            if (meta instanceof final SkullMeta skull) {
                final PlayerProfile profile = skull.getPlayerProfile();

                if (skull.hasOwner()) {
                    skullOwner(skull.getOwningPlayer());
                }

                if (profile != null) {
                    playerProfile(profile);
                }
            }

            if (meta instanceof final PotionMeta potion) {
                if (potion.hasBasePotionType()) {
                    potionType(potion.getBasePotionType());
                }

                if (potion.hasColor()) {
                    potionColour(potion.getColor());
                }

                if (potion.hasCustomEffects()) {
                    potionEffects.addAll(potion.getCustomEffects());
                }
            }

            if (meta instanceof final BookMeta book) {
                if (book.hasTitle()) {
                    bookTitle(book.title().serialize());
                }

                if (book.hasAuthor()) {
                    bookAuthor(book.author().serialize());
                }

                if (book.hasPages()) {
                    for (final Component page : book.pages()) {
                        bookPage(page.serialize());
                    }
                }

                if (book.hasGeneration()) {
                    bookGeneration(book.getGeneration());
                }
            }

            if (meta instanceof final LeatherArmorMeta leather) {
                leatherColor(leather.getColor());
            }

            if (meta instanceof final BannerMeta banner) {
                final List<Pattern> patterns = banner.getPatterns();

                if (!patterns.isEmpty()) {
                    bannerPatterns(patterns);
                }
            }

            if (meta instanceof final FireworkMeta firework) {
                if (firework.hasEffects()) {
                    fireworkEffects.addAll(firework.getEffects());
                }

                fireworkPower(firework.getPower());
            }

            if (meta instanceof final MapMeta map) {
                if (map.hasColor()) {
                    mapColour(map.getColor());
                }

                if (map.isScaling()) {
                    mapScaling(true);
                }
            }

            if (meta instanceof final CompassMeta compass) {
                if (compass.hasLodestone()) {
                    lodestoneLocation(compass.getLodestone());
                }

                if (compass.isLodestoneTracked()) {
                    lodestoneTracked(true);
                }
            }

            if (meta instanceof final TropicalFishBucketMeta fish) {
                if (fish.hasVariant()) {
                    fishPatternType(fish.getPattern());
                    fishBodyColour(fish.getBodyColor());
                    fishPatternColour(fish.getPatternColor());
                }
            }

            if (meta instanceof final CrossbowMeta crossbow) {
                if (crossbow.hasChargedProjectiles()) {
                    crossbowProjectiles(crossbow.getChargedProjectiles());
                }
            }

            if (meta instanceof final SuspiciousStewMeta stew) {
                if (stew.hasCustomEffects()) {
                    stewEffects.addAll(stew.getCustomEffects());
                }
            }

            if (meta instanceof final ArmorMeta armour) {
                if (armour.hasTrim()) {
                    trim(armour.getTrim());
                }
            }

            if (meta instanceof final MusicInstrumentMeta music) {
                final MusicInstrument instrument = music.getInstrument();

                if (instrument != null) {
                    instrument(instrument);
                }
            }

            if (meta instanceof final OminousBottleMeta ominous) {
                if (ominous.hasAmplifier()) {
                    ominousBottleAmplifier(ominous.getAmplifier());
                }
            }

            final PersistentDataContainer pdc = meta.getPersistentDataContainer();

            if (!pdc.isEmpty()) {
                try {
                    oldPersistentData = pdc.serializeToBytes();
                } catch (final IOException exception) {
                    OPlugin.get().getLogger().log(
                        Level.SEVERE,
                        "Error serializing persistent data container:" + exception.getMessage() + "!",
                        exception
                    );
                }
            }

            return this;
        }
    }
}