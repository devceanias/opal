package net.oceanias.opal.listener;

import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.plugin.OPlugin;
import org.bukkit.event.Listener;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
public final class OListener {
    @RequiredArgsConstructor
    public static abstract class Bukkit implements Listener, OProvider {
        protected abstract OPlugin getPlugin();

        @Override
        public final void registerInternally() {
            getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

            OProvider.super.registerInternally();
        }
    }
}