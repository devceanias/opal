package net.oceanias.ocean;

import net.oceanias.ocean.configuration.OConfiguration;
import net.oceanias.ocean.plugin.OPlugin;
import java.io.File;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
@Getter
@Configuration
final class Config extends OConfiguration<Config> {
    @Getter
    @Accessors(fluent = true)
    private static final Config get = new Config(Ocean.get());

    public Config(final OPlugin plugin) {
        super(plugin);
    }

    @Contract(value = " -> new", pure = true)
    @Override
    protected @NotNull File getFile() {
        return new File("config.yml");
    }

    @Override
    protected Class<Config> getClazz() {
        return Config.class;
    }

    private Menu menu = new Menu();

    @Getter
    @Configuration
    static final class Menu {
        private Ingredients ingredients = new Ingredients();

        @Getter
        @Configuration
        static final class Ingredients {
            private char border = '#';
        }
    }
}