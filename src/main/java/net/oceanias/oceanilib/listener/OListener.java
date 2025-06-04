package net.oceanias.oceanilib.listener;

import net.oceanias.oceanilib.module.OProvider;
import net.oceanias.oceanilib.plugin.OPlugin;
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