package net.oceanias.ocean.configuration.impl;

import net.oceanias.ocean.Ocean;
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
public final class OPrimaryConfig extends OConfiguration<OPrimaryConfig> {
    @Getter
    @Accessors(fluent = true)
    private static final OPrimaryConfig get = new OPrimaryConfig(Ocean.get());

    public OPrimaryConfig() {
        super(Ocean.get());
    }

    public OPrimaryConfig(final OPlugin plugin) {
        super(plugin);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getLabel() {
        return "primary";
    }

    @Contract(value = " -> new", pure = true)
    @Override
    protected @NotNull File getFile() {
        return new File("config.yml");
    }

    @Override
    protected Class<OPrimaryConfig> getClazz() {
        return OPrimaryConfig.class;
    }

    private Menu menu = new Menu();

    @Getter
    @Configuration
    public static final class Menu {
        private Ingredients ingredients = new Ingredients();

        @Getter
        @Configuration
        public static final class Ingredients {
            private char border = '#';
        }
    }
}