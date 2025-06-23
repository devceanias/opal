package net.oceanias.opal.command;

import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.plugin.OPlugin;
import net.oceanias.opal.utility.extension.OPlayerExtension;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.AbstractArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@ExtensionMethod({ OPlayerExtension.class, OStringExtension.class })
public abstract class OCommand implements OProvider {
    private List<HelpLine> help;
    private final Map<List<String>, List<String>> descriptions = new ConcurrentHashMap<>();

    private CommandTree base;

    protected abstract OPlugin getPlugin();

    public abstract String getLabel();

    protected final @NotNull CommandTree getBase() {
        if (base == null) {
            base = new CommandTree(getLabel());
        }

        return base;
    }

    public List<OSubcommand> getSubcommands() {
        return Collections.emptyList();
    }

    public CommandPermission getPermission() {
        return ofPermission(getPlugin(), getLabel());
    }

    public CommandTree getCommand() {
        return getBase();
    }

    @Override
    public final void registerInternally() {
        final CommandTree tree = getCommand()
            .withPermission(getPermission());

        final Set<String> commands = new HashSet<>();

        commands.add(getLabel());

        Collections.addAll(commands, tree.getAliases());

        for (final String command : commands) {
            CommandAPIBukkit.unregister(command, true, true);
        }

        buildDescriptions(getSubcommands(), new ArrayList<>());

        for (final OSubcommand sub : getSubcommands()) {
            tree.then(sub.buildSubcommand());
        }

        try {
            attachHelp(tree);
        } catch (final Exception exception) {
            throw new IllegalStateException(
                "Error attaching help to command " + getLabel() + ".", exception
            );
        }

        tree.register();

        OProvider.super.registerInternally();
    }

    @Override
    public final void unregisterInternally() {
        OProvider.super.unregisterInternally();

        CommandAPI.unregister(getLabel(), true);
    }

    private void buildDescriptions(@NotNull final List<OSubcommand> commands, final List<String> path) {
        for (final OSubcommand command : commands) {
            final List<String> chain = new ArrayList<>(path);
            final List<String> description = command.getDescription();

            chain.add(command.getLabel());

            if (!description.isEmpty()) {
                descriptions.put(chain, description);
            }

            buildDescriptions(command.getSubcommands(), chain);
        }
    }

    private void attachHelp(final Object node) throws Exception {
        final BukkitExecutable<?> executable = (BukkitExecutable<?>) node;

        if (!executable.getExecutor().hasAnyExecutors()) {
            executable.executes(this::sendHelp);
        }

        for (final Object child : getArguments(node)) {
            attachHelp(child);
        }
    }

    private void sendHelp(final CommandSender sender, final CommandArguments args) {
        if (!(sender instanceof final Player player)) {
            return;
        }

        final List<Component> lines = new ArrayList<>(List.of(
            OStringExtension.CHAT_DIVIDER_SHORT.deserialize(),
            (getPlugin().getColour() + WordUtils.capitalize(getLabel()) + " Commands:").deserialize()
        ));

        for (final HelpLine line : getUsages()) {
            final StringBuilder builder = new StringBuilder("/").append(getLabel());

            for (final HelpSegment seg : line.segments) {
                builder.append(" ");

                if (seg.literal) {
                    builder.append(seg.name);

                    continue;
                }

                if (seg.optional) {
                    builder.append("<yellow>[").append(seg.name).append("]</yellow>");

                    continue;
                }

                builder.append("<yellow>(").append(seg.name).append(")</yellow>");
            }

            final List<String> description = line.description;

            Component component = ("<gray>• <white>" + builder).deserialize();

            if (description != null) {
                component = component.hoverEvent(HoverEvent.showText(
                    String.join("\n", getPlugin().getColour() + description).deserialize()
                ));
            }

            lines.add(component);
        }

        lines.add(OStringExtension.CHAT_DIVIDER_SHORT.deserialize());

        player.sendMessage(Component.join(JoinConfiguration.separator(Component.newline()), lines));
        player.soundDSR(Sound.BLOCK_NOTE_BLOCK_CHIME);
    }

    private @NotNull List<HelpLine> getUsages() {
        if (help == null) {
            try {
                help = new ArrayList<>();

                traverseTree(getBase(), new ArrayList<>(), help);
            } catch (final Exception exception) {
                throw new IllegalStateException(
                    "Error collecting usage lines for command " + getLabel() + ".", exception
                );
            }
        }

        return help;
    }

    private void traverseTree(
        @NotNull final Object node, final List<HelpSegment> segments, final List<HelpLine> lines
    ) throws Exception {
        final List<Object> children = getArguments(node);

        if (children.isEmpty()) {
            final List<String> literals = new ArrayList<>();

            for (final HelpSegment segment : segments) {
                if (!segment.literal) {
                    continue;
                }

                literals.add(segment.name);
            }

            lines.add(new HelpLine(new ArrayList<>(segments), descriptions.get(literals)));

            return;
        }

        for (final Object child : children) {
            boolean subcommand = false;
            final String name;
            boolean optional = false;

            if (child instanceof final AbstractArgument<?, ?, ?, ?> argument) {
                if (argument.getNodeName().equals(OSubcommand.SUBCOMMAND_NODE_NAME)) {
                    subcommand = true;

                    if (child instanceof final MultiLiteralArgument multi) {
                        final String[] literals = multi.getLiterals();

                        name = literals.length > 0 ? literals[0] : "";
                    } else {
                        name = argument.getNodeName();
                    }
                } else {
                    name = argument.getNodeName();
                    optional = argument.isOptional();
                }
            } else {
                name = child.getClass().getSimpleName().toLowerCase();
            }

            segments.add(new HelpSegment(name, subcommand, optional));

            traverseTree(child, segments, lines);

            segments.removeLast();
        }
    }

    @SuppressWarnings("unchecked")
    private static @NotNull List<Object> getArguments(@NotNull final Object node) throws NoSuchFieldException {
        for (Class<?> clazz = node.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                final Field field = clazz.getDeclaredField("arguments");

                field.setAccessible(true);

                return (List<Object>) field.get(node);
            } catch (final IllegalAccessException | NoSuchFieldException ignored) {}
        }

        throw new NoSuchFieldException(
            "Error finding field with name arguments in " + node.getClass().getSimpleName() + "."
        );
    }

    @Contract("_, _ -> new")
    public static @NotNull CommandPermission ofPermission(final @NotNull OPlugin plugin, final String name) {
        return CommandPermission.fromString(plugin.getLabel() + ".command." + name);
    }

    private record HelpLine(List<HelpSegment> segments, List<String> description) {}

    private record HelpSegment(String name, boolean literal, boolean optional) {}
}