package net.oceanias.opal.configuration;

import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.extension.OStringExtension;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import de.exlll.configlib.*;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "unchecked" })
@ExtensionMethod(OStringExtension.class)
public abstract class OConfiguration<T> implements OProvider {
    private Path path;
    private YamlConfigurationStore<T> store;

    private boolean isFirstLoad = true;

    public abstract String getLabel();

    protected abstract File getFile();

    protected YamlConfigurationProperties getProperties() {
        return ConfigLib.BUKKIT_DEFAULT_PROPERTIES
            .toBuilder()
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .build();
    }

    protected abstract T getInstance();

    protected abstract void setInstance(T config);

    protected void onLoad(final boolean isFirstLoad) {}

    protected void onSave() {}

    protected abstract Class<T> getClazz();

    public final void loadConfiguration() {
        if (path == null) {
            path = new File(OPlugin.get().getDataFolder(), getFile().getPath()).toPath();
        }

        if (store == null) {
            store = new YamlConfigurationStore<>(getClazz(), getProperties());
        }

        final OConfiguration<T> updated = (OConfiguration<T>) store.update(path);

        updated.path = path;
        updated.store = store;

        setInstance((T) updated);

        onLoad(isFirstLoad);

        isFirstLoad = false;
    }

    public final void saveConfiguration() {
        if (path == null || store == null || getInstance() == null) {
            return;
        }

        store.save(getInstance(), path);

        onSave();
    }

    @Override
    public final void registerInternally() {
        loadConfiguration();

        OProvider.super.registerInternally();
    }

    @Override
    public final void unregisterInternally() {
        OProvider.super.unregisterInternally();

        saveConfiguration();
    }

    public record Message(
        Type type,
        String message,
        List<String> lines
    ) {
        public enum Type {
            PLAYER_CHAT,
            ACTION_BAR
        }

        public Message(final Type type, final String message) {
            this(type, message, null);
        }

        public Message(final Type type, final List<String> lines) {
            this(type, null, lines);
        }

        @Contract("_, _ -> new")
        public @NotNull Message replace(final CharSequence target, final CharSequence replacement) {
            if (lines == null) {
                return new Message(type, message.replace(target, replacement));
            }

            final List<String> replaced = lines.stream()
                .map(line -> line.replace(target, replacement))
                .toList();

            return new Message(type, replaced);
        }

        public void send(final CommandSender sender) {
            final String result;
            final String joined = lines != null ? String.join("\n", lines) : message;

            if (sender instanceof ConsoleCommandSender || type == Type.PLAYER_CHAT) {
                if (lines != null && !lines.isEmpty()) {
                    result = joined;
                } else {
                    result = OPlugin.get().getPrefix() + " " + joined;
                }
            } else {
                result = joined;
            }

            final Component component = result.deserialize();

            switch (type) {
                case PLAYER_CHAT -> sender.sendMessage(component);
                case ACTION_BAR -> {
                    if (sender instanceof ConsoleCommandSender) {
                        sender.sendMessage(component);

                        return;
                    }

                    sender.sendActionBar(component);
                }
            }
        }

        public void send(final @NotNull Iterable<? extends CommandSender> senders) {
            for (final CommandSender sender : senders) {
                send(sender);
            }
        }

        public void broadcast() {
            for (final Player sender : OPlugin.get().getServer().getOnlinePlayers()) {
                send(sender);
            }
        }
    }
}