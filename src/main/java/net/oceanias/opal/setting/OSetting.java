package net.oceanias.opal.setting;

import java.util.List;
import org.bukkit.Material;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@SuppressWarnings("unused")
@Getter
@Accessors(fluent = true, chain = false)
public abstract class OSetting<T> {
    protected final String pretty;
    protected final T initial;

    @Setter
    protected List<String> description;

    @Setter
    protected Material material;

    @Setter
    protected T value;

    protected OSetting(final String pretty, final T initial) {
        this.pretty = pretty;
        this.initial = initial;

        value = initial;
    }

    public void reset() {
        this.value = initial;
    }

    public abstract Type type();

    public enum Type {
        BOOLEAN,
        CHOICE,
        DOUBLE,
        INTEGER,
        STRING,
    }
}