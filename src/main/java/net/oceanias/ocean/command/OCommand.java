package net.oceanias.ocean.command;

import net.oceanias.ocean.module.OProvider;
import net.oceanias.ocean.plugin.OPlugin;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.command.CommandSender;
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

    public List<String> getAliases() {
        return Collections.emptyList();
    }

    public CommandPermission getPermission() {
        return ofPermission(plugin, getLabel());
    }

    public Predicate<CommandSender> getRequirement() {
        return sender -> true;
    }

    public CommandTree getCommand() {
        return getBase();
    }

    @Override
    public void onRegister() {
        final CommandTree tree = getCommand()
            .withAliases(getAliases().toArray(new String[0]))
            .withPermission(getPermission())
            .withRequirement(getRequirement());

        CommandAPI.unregister(getLabel(), true);

        for (final OSubcommand subcommand : getSubcommands()) {
            tree.then(subcommand.get());
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