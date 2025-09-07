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

    private static final Map<String, String> REPLACEMENTS_MAP;
    private static final Pattern REPLACEMENTS_PATTERN;

    static {
        REPLACEMENTS_MAP = Map.ofEntries(
            Map.entry("&1", "&#0000E7"), Map.entry("[dark_blue]", "&#0000E7"),
            Map.entry("&2", "&#2BB02B"), Map.entry("[dark_green]", "&#2BB02B"),
            Map.entry("&3", "&#00D1D1"), Map.entry("[dark_aqua]", "&#00D1D1"),
            Map.entry("&4", "&#D10000"), Map.entry("[dark_red]", "&#D10000"),
            Map.entry("&5", "&#880ED4"), Map.entry("[dark_purple]", "&#880ED4"),
            Map.entry("&6", "&6"), Map.entry("[orange]", "&6"), Map.entry("[gold]", "&6"),
            Map.entry("&9", "&#4444FF"), Map.entry("[blue]", "&#4444FF"),
            Map.entry("&a", "&a"), Map.entry("[green]", "&a"),
            Map.entry("&b", "&#00FFFF"), Map.entry("[aqua]", "&#00FFFF"),
            Map.entry("&c", "&c"), Map.entry("[red]", "&c"),
            Map.entry("&d", "&#FF00FF"), Map.entry("[magenta]", "&#FF00FF"), Map.entry("[light_purple]", "&#FF00FF"),
            Map.entry("&e", "&e"), Map.entry("[yellow]", "&e")
        );

        REPLACEMENTS_PATTERN = Pattern.compile(REPLACEMENTS_MAP.keySet().stream()
            .map(Pattern::quote) // Escape special characters
            .sorted((a,b) -> b.length() - a.length()) // Prioritise longer aliases
            .reduce((a,b) -> a + "|" + b)
            .orElse("")
        );
    }

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