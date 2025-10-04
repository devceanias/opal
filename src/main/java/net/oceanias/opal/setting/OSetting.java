package net.oceanias.opal.setting;

import java.util.List;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings({ "unused", "unchecked" })
@Getter
public abstract class OSetting<T, S extends OSetting<T, S>> {
    @Accessors(fluent = true)
    protected final String pretty;

    @Accessors(fluent = true)

    protected final T initial;

    @Accessors(fluent = true)
    protected List<String> description;

    @Accessors(fluent = true)
    protected Material material;

    @Setter
    @Accessors(fluent = true, chain = false)
    protected T value;

    protected OSetting(final String pretty, final T initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Error creating setting; initial value must not be null.");
        }

        this.pretty = pretty;
        this.initial = initial;

        value = initial;
    }

    protected S self() {
        return (S) this;
    }

    public S description(final List<String> description) {
        this.description = description;

        return self();
    }

    public S material(final Material material) {
        this.material = material;

        return self();
    }

    public final void reset() {
        value = initial;
    }

    public abstract Item item();
}