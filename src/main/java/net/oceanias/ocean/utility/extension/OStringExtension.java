package net.oceanias.ocean.utility.extension;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class OStringExtension {
    public static final String CHAT_DIVIDER = "&7&m                                                            ";
    public static final String LORE_DIVIDER = "&8&m                                              ";

    public static @NotNull Component deserialize(final String string, final boolean section) {
        if (!section) {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
        }

        return LegacyComponentSerializer.legacySection().deserialize(string);
    }

    public static @NotNull Component deserialize(final String string) {
        return deserialize(string, false);
    }
}