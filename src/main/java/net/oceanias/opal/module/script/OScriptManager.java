package net.oceanias.opal.module.script;

import lombok.Getter;
import net.oceanias.opal.OPlugin;
import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.module.script.core.OScript;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "ResultOfMethodCallIgnored", "UnusedReturnValue", "unchecked" })
@Getter
public class OScriptManager implements OProvider {
    @Getter
    @Accessors(fluent = true)
    private static final OScriptManager get = new OScriptManager();

    private final File scriptsFolder = new File(OPlugin.get().getDataFolder(), "scripts/all");
    private final Map<String, OScript> loadedScripts = new ConcurrentHashMap<>();

    private WatchService watchService;
    private Thread watchThread;

    @Override
    public void onRegister() {
        if (!scriptsFolder.exists()) {
            scriptsFolder.mkdirs();
        }

        loadScripts();
        startWatching();
    }

    @Override
    public void onUnregister() {
        stopWatching();
        disableAll();

        loadedScripts.clear();
    }

    public void loadScripts() {
        final File[] files = scriptsFolder.listFiles((dir, name) -> name.endsWith(".groovy"));

        if (files == null) {
            return;
        }

        for (final File file : files) {
            final String label = file.getName().replace(".groovy", "");

            if (loadedScripts.containsKey(label)) {
                continue;
            }

            final OScript script = new OScript(file);

            loadedScripts.put(label, script);
        }
    }

    public OScript getScript(@NotNull final String name) {
        return loadedScripts.get(name);
    }

    public List<OScript> getEnabled() {
        return loadedScripts.values().stream()
            .filter(OScript::isEnabled)
            .collect(Collectors.toList());
    }

    public List<OScript> getDisabled() {
        return loadedScripts.values().stream()
            .filter(script -> !script.isEnabled())
            .collect(Collectors.toList());
    }

    public void enableAll() {
        for (final OScript script : loadedScripts.values()) {
            script.enable();
        }
    }

    public void disableAll() {
        for (final OScript script : loadedScripts.values()) {
            script.disable();
        }
    }

    public void reloadAll() {
        for (final OScript script : loadedScripts.values()) {
            script.reload();
        }
    }

    private void startWatching() {
        try {
            watchService = FileSystems.getDefault().newWatchService();

            scriptsFolder.toPath().register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE
            );

            watchThread = new Thread(this::processEvents, "ScriptWatcherThread");
            watchThread.setDaemon(true);
            watchThread.start();
        } catch (final Exception exception) {
            OPlugin.get().getLogger().log(
                Level.SEVERE,
                "Error starting script file watcher: " + exception.getMessage() + "!",
                exception
            );
        }
    }

    private void stopWatching() {
        try {
            if (watchService != null) {
                watchService.close();
            }

            if (watchThread == null) {
                return;
            }

            watchThread.interrupt();
            watchThread.join();
        } catch (final Exception ignored) {}
    }

    private void processEvents() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final WatchKey key = watchService.take();

                for (final WatchEvent<?> event : key.pollEvents()) {
                    final Path changed = ((WatchEvent<Path>) event).context();
                    final File file = scriptsFolder.toPath().resolve(changed).toFile();

                    if (!file.getName().endsWith(".groovy")) {
                        continue;
                    }

                    final WatchEvent.Kind<?> kind = event.kind();
                    final String label = file.getName().replace(".groovy", "");

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        if (!loadedScripts.containsKey(label)) {
                            loadedScripts.put(label, new OScript(file));
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        final OScript script = loadedScripts.remove(label);

                        if (script != null && script.isEnabled()) {
                            script.disable();
                        }
                    }
                }

                key.reset();
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
            } catch (final Exception exception) {
                OPlugin.get().getLogger().log(
                    Level.SEVERE,
                    "Error processing script folder events: " + exception.getMessage() + "!",
                    exception
                );
            }
        }
    }
}