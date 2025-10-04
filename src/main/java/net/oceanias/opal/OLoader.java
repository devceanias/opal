package net.oceanias.opal;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "UnstableApiUsage" })
public abstract class OLoader implements PluginLoader {
    private static final String INVUI_VERSION = "1.46";

    @Override
    public final void classloader(@NotNull final PluginClasspathBuilder builder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder(
            "xenondevs",
            "default",
            "https://repo.xenondevs.xyz/releases/"
        ).build());

        resolver.addDependency(new Dependency(
            new DefaultArtifact("xyz.xenondevs.invui:invui:pom:" + INVUI_VERSION),
            null
        ));

        builder.addLibrary(resolver);
    }
}