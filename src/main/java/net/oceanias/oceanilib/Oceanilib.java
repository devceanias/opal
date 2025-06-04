package net.oceanias.oceanilib;

import org.bukkit.plugin.java.JavaPlugin;
import lombok.Getter;
import lombok.experimental.Accessors;

@SuppressWarnings("unused")
public final class Oceanilib extends JavaPlugin {
    @Getter
    @Accessors(fluent = true)
    private static Oceanilib get;

    @Override
    public void onEnable() {
        get = this;
    }
}