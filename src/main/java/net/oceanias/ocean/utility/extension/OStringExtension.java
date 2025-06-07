package net.oceanias.ocean.utility.extension;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class OStringExtension {
    public static final String CHAT_DIVIDER_SHORT =
        "<gray><strikethrough>                                                            </strikethrough>";

    public static final String CHAT_DIVIDER_LONG =
        "<gray><strikethrough>                                                                              </strikethrough>";

    public static final String LORE_DIVIDER =
        "<dark_gray><strikethrough>                                              </strikethrough>";

    public static @NotNull Component deserialize(final String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }
}