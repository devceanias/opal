package net.oceanias.ocean.utility.extension;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

@SuppressWarnings("unused")
public final class OComponentExtension {
    public static Component stripAbsentDecorations(Component component) {
        for (final TextDecoration decoration : TextDecoration.values()) {
            component = component.decorationIfAbsent(decoration, TextDecoration.State.FALSE);
        }

        return component;
    }
}