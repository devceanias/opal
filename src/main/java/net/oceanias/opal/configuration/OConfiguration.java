package net.oceanias.opal.configuration;

import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.plugin.OPlugin;
import java.io.File;
import java.nio.file.Path;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;

@SuppressWarnings("unused")
public abstract class OConfiguration<T> implements OProvider {
    private T config;

    private boolean isFirstLoad = true;

    protected abstract OPlugin getPlugin();

    public abstract String getLabel();

    protected abstract File getFile();

    protected YamlConfigurationProperties getProperties() {
        return ConfigLib.BUKKIT_DEFAULT_PROPERTIES
            .toBuilder()
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .build();
    }

    protected void onLoad(final boolean isFirstLoad) {}

    protected void onSave() {}

    protected abstract Class<T> getClazz();

    public final void loadConfiguration() {
        final Path path = new File(getPlugin().getDataFolder(), getFile().getPath()).toPath();

        config = YamlConfigurations.update(path, getClazz(), getProperties());

        onLoad(isFirstLoad);

        isFirstLoad = false;
    }

    public final void saveConfiguration() {
        if (config == null) {
            return;
        }

        YamlConfigurations.save(new File(getPlugin().getDataFolder(), getFile().getPath()).toPath(), getClazz(), config);

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