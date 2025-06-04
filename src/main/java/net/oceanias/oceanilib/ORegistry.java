package net.oceanias.oceanilib;

import net.oceanias.oceanilib.plugin.OPlugin;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.Plugin;

public final class ORegistry {
    private static final Map<Plugin, OPlugin> registry = new ConcurrentHashMap<>();

    public static void registerPlugin(final OPlugin plugin) {
        registry.put(plugin, plugin);
    }

    public static void unregisterPlugin(final OPlugin plugin) {
        registry.remove(plugin);
    }

    public static OPlugin getCaller() {
        return registry.values().stream()
            .filter(plugin ->
                plugin.getClass().getClassLoader().equals(
                    StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(
                        frame -> frame.skip(1).findFirst().orElseThrow().getDeclaringClass().getClassLoader()
                    )
                )
            )
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Error finding OPlugin for caller class."));
    }
}