package net.oceanias.oceanilib.command;

import net.oceanias.oceanilib.plugin.OPlugin;
import java.util.Collections;
import java.util.List;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class OSubcommand {
    protected final OPlugin plugin;

    public abstract OCommand getParent();

    public abstract String getSublabel();

    public final String getLabel() {
        return getParent().getLabel();
    }

    @Contract(" -> new")
    protected final @NotNull MultiLiteralArgument getBase() {
        return new MultiLiteralArgument("subcommand", getSublabel());
    }

    public CommandPermission getPermission() {
        return OCommand.ofPermission(plugin, getLabel() + "." + getSublabel());
    }

    public List<OSubcommand> getSubcommands() {
        return Collections.emptyList();
    }

    public abstract Argument<String> getSubcommand();

    public final Argument<String> get() {
        final Argument<String> base = getSubcommand().withPermission(getPermission());

        for (final OSubcommand subcommand : getSubcommands()) {
            base.then(subcommand.get());
        }

        return base;
    }
}