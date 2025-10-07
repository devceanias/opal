package net.oceanias.opal.configuration.impl;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.configuration.OConfiguration;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import dev.jorel.commandapi.CommandAPI;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
@Getter
@Accessors(fluent = true)
@Configuration
public final class OScriptConfig extends OConfiguration<OScriptConfig> {
    @Getter
    private static OScriptConfig get = new OScriptConfig();

    @Contract(pure = true)
    @Override
    public @NotNull String getLabel() {
        return "script";
    }

    @Contract(value = " -> new", pure = true)
    @Override
    protected @NotNull File getFile() {
        return new File("scripts/config.yml");
    }

    @Override
    protected OScriptConfig getInstance() {
        return get;
    }

    @Override
    protected void setInstance(final OScriptConfig config) {
        get = config;
    }

    @Override
    protected void onLoad(final boolean isFirstLoad) {
        if (isFirstLoad) {
            return;
        }

        for (final Player player : OPlugin.get().getServer().getOnlinePlayers()) {
            CommandAPI.updateRequirements(player);
        }
    }

    @Override
    protected Class<OScriptConfig> getClazz() {
        return OScriptConfig.class;
    }

    private Command command = new Command();

    @Getter
    @Configuration
    public static final class Command {
        private Requirements requirements = new Requirements();

        @Getter
        @Configuration
        public static final class Requirements {
            private String permission = "opal.command.script";

            private List<UUID> uuids = List.of(
                UUID.fromString("a48010b2-247f-4d52-aa4e-5be5be39f385"), // Plantal
                UUID.fromString("a8045ac2-691e-4017-8901-09d887bcfa0d"), // stasio
                UUID.fromString("9f077306-f7f4-4e24-a642-5f176c2ef887")  // eqzy
            );
        }
    }
}