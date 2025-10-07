package net.oceanias.opal;

import java.util.List;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "UnstableApiUsage" })
public abstract class OLoader implements PluginLoader {
    private static final List<Triple<String, String, String>> REPOSITORIES = List.of(
        Triple.of("central", "default", "https://repo.maven.apache.org/maven2/"),
        Triple.of("xenondevs", "default", "https://repo.xenondevs.xyz/releases/")
    );

    private static final List<Pair<String, String>> ARTIFACTS = List.of(
        Pair.of("xyz.xenondevs.invui:invui:pom:1.46", null),
        Pair.of("org.apache.groovy:groovy-all:pom:5.0.1", null)
    );

    @Override
    public final void classloader(@NotNull final PluginClasspathBuilder builder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();

        for (final Triple<String, String, String> repository : REPOSITORIES) {
            resolver.addRepository(new RemoteRepository.Builder(
                repository.getLeft(),   // ID
                repository.getMiddle(), // Type
                repository.getRight()   // URL
            ).build());
        }

        for (final Pair<String, String> artifact : ARTIFACTS) {
            resolver.addDependency(new Dependency(
                new DefaultArtifact(artifact.getLeft()), // Artifact
                artifact.getRight()                      // Scope
            ));
        }

        builder.addLibrary(resolver);
    }
}