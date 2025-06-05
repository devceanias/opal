package net.oceanias.ocean.command;

import net.oceanias.ocean.module.OProvider;
import net.oceanias.ocean.plugin.OPlugin;
import java.util.Collections;
import java.util.List;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class OCommand implements OProvider {
    protected final OPlugin plugin;

    public abstract String getLabel();

    @Contract(" -> new")
    protected final @NotNull CommandTree getBase() {
        return new CommandTree(getLabel());
    }

    public List<OSubcommand> getSubcommands() {
        return Collections.emptyList();
    }

    public CommandPermission getPermission() {
        return ofPermission(plugin, getLabel());
    }

    public CommandTree getCommand() {
        return getBase();
    }

    @Override
    public void onRegister() {
        final CommandTree tree = getCommand().withPermission(getPermission());

        CommandAPI.unregister(getLabel(), true);

        for (final OSubcommand subcommand : getSubcommands()) {
            tree.then(subcommand.buildSubcommand());
        }

        tree.register();
    }

    @Override
    public void onUnregister() {
        CommandAPI.unregister(getLabel(), true);
    }

    @Contract("_, _ -> new")
    public static @NotNull CommandPermission ofPermission(final @NotNull OPlugin plugin, final String name) {
        return CommandPermission.fromString(plugin.getLabel() + ".command." + name);
    }
}