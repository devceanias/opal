package net.oceanias.opal.plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

// TODO: Fix
@SuppressWarnings("unused")
public final class ORegistry {
    private static final Map<Plugin, OPlugin> registry = new ConcurrentHashMap<>();
    private static final Map<Class<?>, OPlugin> cache = new ConcurrentHashMap<>();

    public static void registerPlugin(final OPlugin plugin) {
        registry.put(plugin, plugin);
    }

    public static void unregisterPlugin(final OPlugin plugin) {
        registry.remove(plugin);
    }

    public static OPlugin fromClass(final Class<?> clazz) {
        final Plugin plugin = JavaPlugin.getProvidingPlugin(clazz);

        if (!(plugin instanceof final OPlugin opal)) {
            throw new IllegalStateException("Plugin is not an OPlugin: " + plugin.getName());
        }

        return opal;
    }

    public static OPlugin getCaller() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
            .walk(frames -> frames
                .map(StackWalker.StackFrame::getDeclaringClass)
                .filter(clazz -> !clazz.getName().startsWith("net.oceanias.opal"))
                .findFirst()
                .map(clazz -> cache.computeIfAbsent(clazz, ORegistry::fromClass))
                .orElseThrow(() -> new IllegalStateException("Error finding OPlugin for caller class."))
            );
    }
}