package net.oceanias.opal.setting;

import java.util.List;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
@Getter
public abstract class OSetting<T> {
    @Accessors(fluent = true)
    protected transient final String pretty;

    @Accessors(fluent = true)
    protected transient final T initial;

    @Accessors(fluent = true)
    protected transient List<String> description;

    @Accessors(fluent = true)
    protected transient Material material;

    @Setter
    @Accessors(fluent = true)
    protected T value;

    protected OSetting(final String pretty, final T initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Error creating setting; initial value must not be null.");
        }

        this.pretty = pretty;
        this.initial = initial;

        value = initial;
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