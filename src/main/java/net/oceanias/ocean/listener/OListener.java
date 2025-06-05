package net.oceanias.ocean.listener;

import net.oceanias.ocean.module.OProvider;
import net.oceanias.ocean.plugin.OPlugin;
import org.bukkit.event.Listener;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
public final class OListener {
    @RequiredArgsConstructor
    public static abstract class Bukkit implements Listener, OProvider {
        protected final OPlugin plugin;

        @Override
        public void onRegister() {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }
}