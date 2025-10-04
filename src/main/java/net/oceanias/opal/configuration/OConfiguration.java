package net.oceanias.opal.configuration;

import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.OPlugin;
import java.io.File;
import java.nio.file.Path;
import de.exlll.configlib.*;

@SuppressWarnings({ "unused", "unchecked" })
public abstract class OConfiguration<T> implements OProvider {
    private Path path;
    private YamlConfigurationStore<T> store;

    private boolean isFirstLoad = true;

    public abstract String getLabel();

    protected abstract File getFile();

    protected YamlConfigurationProperties getProperties() {
        return ConfigLib.BUKKIT_DEFAULT_PROPERTIES
            .toBuilder()
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .build();
    }

    protected abstract T getInstance();

    protected abstract void setInstance(T config);

    protected void onLoad(final boolean isFirstLoad) {}

    protected void onSave() {}

    protected abstract Class<T> getClazz();

    public final void loadConfiguration() {
        if (path == null) {
            path = new File(OPlugin.get().getDataFolder(), getFile().getPath()).toPath();
        }

        if (store == null) {
            store = new YamlConfigurationStore<>(getClazz(), getProperties());
        }

        final OConfiguration<T> updated = (OConfiguration<T>) store.update(path);

        updated.path = path;
        updated.store = store;

        setInstance((T) updated);

        onLoad(isFirstLoad);

        isFirstLoad = false;
    }

    public final void saveConfiguration() {
        if (path == null || store == null || getInstance() == null) {
            return;
        }

        store.save(getInstance(), path);

        onSave();
    }

    @Override
    public final void registerInternally() {
        loadConfiguration();

        OProvider.super.registerInternally();
    }

    @Override
    public final void unregisterInternally() {
        OProvider.super.unregisterInternally();

        saveConfiguration();
    }
}