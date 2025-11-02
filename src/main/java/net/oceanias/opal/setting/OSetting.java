package net.oceanias.opal.setting;

import java.util.List;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
@Getter
@Accessors(fluent = true)
public abstract class OSetting<T> {
    protected transient String name = "Unspecified Name";

    protected transient T initial;

    protected transient List<String> description;

    protected transient Material material;

    @Setter
    protected T value;

    protected OSetting(final T initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Error creating setting; initial value must not be null.");
        }

        this.initial = initial;

        value = initial;
    }

    public OSetting<T> name(final String name) {
        this.name = name;

        return this;
    }

    public OSetting<T> initial(final T initial, final boolean overwriteCurrentValue) {
        this.initial = initial;

        if (overwriteCurrentValue) {
            value = initial;
        }

        return this;
    }

    public OSetting<T> description(final List<String> description) {
        this.description = description;

        return this;
    }

    public OSetting<T> material(final Material material) {
        this.material = material;

        return this;
    }

    public final void reset() {
        value = initial;
    }

    public abstract Item item();
}