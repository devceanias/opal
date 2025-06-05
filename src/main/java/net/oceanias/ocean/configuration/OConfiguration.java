package net.oceanias.ocean.configuration;

import net.oceanias.ocean.module.OProvider;
import net.oceanias.ocean.plugin.OPlugin;
import java.io.File;
import java.nio.file.Path;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class OConfiguration<T> implements OProvider {
    protected final OPlugin plugin;

    private T config;

    protected abstract File getFile();

    protected YamlConfigurationProperties getProperties() {
        return ConfigLib.BUKKIT_DEFAULT_PROPERTIES;
    }

    protected void onLoad() {}

    protected void onSave() {}

    protected abstract Class<T> getClazz();

    public void loadConfiguration() {
        final Path path = new File(plugin.getDataFolder(), getFile().getPath()).toPath();

        config = YamlConfigurations.update(path, getClazz(), getProperties());

        onLoad();
    }

    public void saveConfiguration() {
        if (config == null) {
            return;
        }

        YamlConfigurations.save(new File(plugin.getDataFolder(), getFile().getPath()).toPath(), getClazz(), config);

        onSave();
    }

    @Override
    public final void onRegister() {
        loadConfiguration();
    }

    @Override
    public final void onUnregister() {
        saveConfiguration();
    }
}