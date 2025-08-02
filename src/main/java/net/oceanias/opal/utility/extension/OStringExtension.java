package net.oceanias.opal.utility.extension;

import net.kyori.adventure.text.Component;
import com.bruhdows.minitext.FormattedText;
import com.bruhdows.minitext.MiniText;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class OStringExtension {
    public static final String CHAT_DIVIDER_SHORT =
        "&7&m                                                            ";

    public static final String CHAT_DIVIDER_LONG =
        "&7&m                                                                              ";

    public static final String LORE_DIVIDER =
        "&8&m                                              ";

    public static @NotNull Component deserialize(final String string) {
        return deserializeToFormatted(string).component();
    }

    public static @NotNull FormattedText deserializeToFormatted(final String string) {
        return MiniText.miniText().deserialize(string);
    }
}