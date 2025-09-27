package net.oceanias.opal.utility.extension;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private static final Map<String, String> REPLACEMENTS_MAP = Map.ofEntries(
        Map.entry("&1", "&#0000E7"), Map.entry("[dark_blue]", "&#0000E7"),
        Map.entry("&2", "&#2BB02B"), Map.entry("[dark_green]", "&#2BB02B"),
        Map.entry("&3", "&#00D1D1"), Map.entry("[dark_aqua]", "&#00D1D1"),
        Map.entry("&4", "&#D10000"), Map.entry("[dark_red]", "&#D10000"),
        Map.entry("&5", "&#880ED4"), Map.entry("[dark_purple]", "&#880ED4"),
        Map.entry("&6", "&#FFA500"), Map.entry("[orange]", "&#FFA500"),
        Map.entry("&9", "&#4444FF"), Map.entry("[blue]", "&#4444FF"),
        Map.entry("&a", "&#00FF00"), Map.entry("[green]", "&#00FF00"),
        Map.entry("&b", "&#00FFFF"), Map.entry("[aqua]", "&#00FFFF"),
        Map.entry("&c", "&#FF0000"), Map.entry("[red]", "&#FF0000"),
        Map.entry("&d", "&#FF00FF"), Map.entry("[magenta]", "&#FF00FF"), Map.entry("[light_purple]", "&#FF00FF"),
        Map.entry("&e", "&#FFFF00"), Map.entry("[yellow]", "&#FFFF00"),
        Map.entry("&g", "&#FFD700"), Map.entry("[gold]", "&#FFD700")
    );

    private static final Pattern REPLACEMENTS_PATTERN = Pattern.compile(REPLACEMENTS_MAP.keySet().stream()
        .map(Pattern::quote) // Escape special characters.
        .sorted((a, b) -> b.length() - a.length()) // Prioritise longer aliases.
        .reduce((a, b) -> a + "|" + b)
        .orElse("")
    );

    public static @NotNull Component deserialize(final String string) {
        return deserializeToFormatted(string).component();
    }

    public static FormattedText deserializeToFormatted(final String string) {
        final Matcher matcher = REPLACEMENTS_PATTERN.matcher(string);
        final StringBuilder builder = new StringBuilder(string.length() * 2);

        while (matcher.find()) {
            matcher.appendReplacement(builder, REPLACEMENTS_MAP.get(matcher.group()));
        }

        matcher.appendTail(builder);

        return MiniText.miniText().deserialize(builder.toString());
    }
}