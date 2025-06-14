package net.oceanias.opal.utility.extension;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class OComponentExtension {
    public static @NotNull String serialize(final Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    public static Component stripAbsentDecorations(Component component) {
        for (final TextDecoration decoration : TextDecoration.values()) {
            component = component.decorationIfAbsent(decoration, TextDecoration.State.FALSE);
        }

        return component;
    }
}