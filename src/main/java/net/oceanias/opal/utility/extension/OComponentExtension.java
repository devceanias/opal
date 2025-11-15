package net.oceanias.opal.utility.extension;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import com.bruhdows.minitext.MiniText;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@UtilityClass
public final class OComponentExtension {
    public @NotNull String serialize(final Component component) {
        return MiniText.miniText().serialize(component);
    }

    @Deprecated
    public Component stripAbsentDecorations(Component component) {
        for (final TextDecoration decoration : TextDecoration.values()) {
            component = component.decorationIfAbsent(decoration, TextDecoration.State.FALSE);
        }

        return component;
    }
}