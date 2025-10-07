package net.oceanias.opal.listener;

import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.OPlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
public final class OListener {
    @RequiredArgsConstructor
    public static abstract class Bukkit implements Listener, OProvider {
        @Override
        public final void registerInternally() {
            OPlugin.get().getServer().getPluginManager().registerEvents(this, OPlugin.get());

            OProvider.super.registerInternally();
        }

        @Override
        public final void unregisterInternally() {
            OProvider.super.unregisterInternally();

            HandlerList.unregisterAll(this);
        }
    }
}