package net.oceanias.ocean.command;

import net.oceanias.ocean.plugin.OPlugin;
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
    public static final String SUBCOMMAND_NODE_NAME = "subcommand";

    protected final OPlugin plugin;

    private MultiLiteralArgument base;

    public abstract OCommand getParent();

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
        return OCommand.ofPermission(plugin, getParent().getLabel() + "." + getLabel());
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