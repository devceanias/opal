package net.oceanias.opal.command;

import net.oceanias.opal.plugin.OPlugin;
import java.util.Collections;
import java.util.List;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class OSubcommand implements OExecutable {
    public static final String SUBCOMMAND_NODE_NAME = "subcommand";

    private MultiLiteralArgument base;

    protected abstract OPlugin getPlugin();

    public abstract OExecutable getParent();

    public abstract String getLabel();

    public List<String> getDescription() {
        return List.of();
    }

    @Contract(" -> new")
    protected final @NotNull MultiLiteralArgument getBase() {
        if (base == null) {
            base = new MultiLiteralArgument(SUBCOMMAND_NODE_NAME, getLabel());
        }

        return base;
    }

    public CommandPermission getPermission() {
        return OCommand.ofPermission(getPlugin(), getParent().getPermission() + "." + getLabel());
    }

    public List<OSubcommand> getSubcommands() {
        return Collections.emptyList();
    }

    public Argument<String> getSubcommand() {
        return getBase();
    }

    public final Argument<String> buildSubcommand() {
        final Argument<String> base = getSubcommand().withPermission(getPermission());

        for (final OSubcommand subcommand : getSubcommands()) {
            base.then(subcommand.buildSubcommand());
        }

        return base;
    }
}