package net.oceanias.opal.module.script.core;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import net.oceanias.opal.OPlugin;
import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.utility.helper.OTaskHelper;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.control.CompilationFailedException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

@SuppressWarnings("unused")
@Getter
public class OScript {
    private final File file;
    private final String label;
    private boolean enabled;

    private Script compiled;
    private Object instance;

    private long lastModified;

    public OScript(final @NotNull File file) {
        this.file = file;

        label = file.getName().replace(".groovy", "");
        enabled = false;

        lastModified = file.lastModified();
    }

    public CompletableFuture<Result> compile() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String content = Files.readString(file.toPath());
                final Binding binding = new Binding();

                binding.setVariable("plugin", OPlugin.get());

                compiled = new GroovyShell(getClass().getClassLoader(), binding).parse(content);
                instance = compiled.run();

                lastModified = file.lastModified();

                return Result.EXECUTION_SUCCESS;
            } catch (final IOException exception) {
                return Result.IO_ERROR.log(exception);
            } catch (final CompilationFailedException exception) {
                return Result.COMPILATION_ERROR.log(exception);
            } catch (final Exception exception) {
                return Result.RUNTIME_ERROR.log(exception);
            }
        });
    }

    public CompletableFuture<Result> enable() {
        if (enabled) {
            return CompletableFuture.completedFuture(Result.ALREADY_ENABLED);
        }

        if (compiled == null) {
            return compile().thenCompose(result -> {
                if (result != Result.EXECUTION_SUCCESS) {
                    return CompletableFuture.completedFuture(result);
                }

                return enable();
            });
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (instance instanceof final OProvider provider) {
                    OTaskHelper.runTask(provider::registerInternally);
                }

                enabled = true;

                return Result.EXECUTION_SUCCESS;
            } catch (final Exception exception) {
                return Result.RUNTIME_ERROR.log(exception);
            }
        });
    }

    public Result disable() {
        if (!enabled) {
            return Result.ALREADY_DISABLED;
        }

        try {
            if (instance instanceof final OProvider provider) {
                OTaskHelper.runTask(provider::unregisterInternally);
            }

            enabled = false;

            return Result.EXECUTION_SUCCESS;
        } catch (final Exception exception) {
            return Result.RUNTIME_ERROR.log(exception);
        }
    }

    public CompletableFuture<Result> reload() {
        final Result result = disable();

        if (result != Result.EXECUTION_SUCCESS && result != Result.ALREADY_DISABLED) {
            return CompletableFuture.completedFuture(result);
        }

        compiled = null;
        instance = null;

        return enable();
    }

    public boolean hasBeenModified() {
        return file.lastModified() != lastModified;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Result {
        EXECUTION_SUCCESS(null),
        ALREADY_ENABLED(null),
        ALREADY_DISABLED(null),
        COMPILATION_ERROR(exception ->
            OPlugin.get().getLogger().log(
                Level.SEVERE, "Error compiling script: " + exception.getMessage() + "!", exception
            )
        ),
        RUNTIME_ERROR(exception ->
            OPlugin.get().getLogger().log(
                Level.SEVERE, "Error running script: " + exception.getMessage() + "!", exception
            )
        ),
        IO_ERROR(exception ->
            OPlugin.get().getLogger().log(
                Level.SEVERE, "Error fetching script: " + exception.getMessage() + "!", exception
            )
        );

        private final Consumer<Exception> logger;

        public Result log(final Exception exception) {
            if (logger != null) {
                logger.accept(exception);
            }

            return this;
        }
    }
}