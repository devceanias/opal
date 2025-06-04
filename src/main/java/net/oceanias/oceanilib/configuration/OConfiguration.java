package net.oceanias.oceanilib.configuration;

import net.oceanias.oceanilib.plugin.OPlugin;
import java.io.File;
import java.nio.file.Path;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurationStore;
import de.exlll.configlib.YamlConfigurations;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class OConfiguration<T> {
    protected final OPlugin plugin;

    private T config;

    protected abstract File getFile();

    protected abstract YamlConfigurationProperties getProperties();

    protected void onLoad() {}

    protected void onSave() {}

    protected abstract Class<T> getClazz();

    public void loadConfiguration() {
        final Path path = new File(plugin.getDataFolder(), getFile().getPath()).toPath();

        YamlConfigurations.update(path, getClazz(), getProperties());

        config = YamlConfigurations.load(path, getClazz(), getProperties());

        onLoad();
    }

    public void saveConfiguration() {
        if (config == null) {
            return;
        }

        final Path path = new File(plugin.getDataFolder(), getFile().getPath()).toPath();
        final YamlConfigurationStore<T> store = new YamlConfigurationStore<>(getClazz(), getProperties());

        store.save(config, path);

        onSave();
    }
}