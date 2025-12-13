package net.oceanias.opal;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.ZapperPlugin;

@SuppressWarnings({ "unused", "UnstableApiUsage" })
public abstract class OBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull final BootstrapContext context) {
        ZapperPlugin.initialise(context);
    }
}