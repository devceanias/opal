package net.oceanias.opal.configuration;

import net.oceanias.opal.component.impl.OProvider;
import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.builder.OSound;
import net.oceanias.opal.utility.extension.OStringExtension;
import net.oceanias.opal.utility.helper.OTextHelper;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import de.exlll.configlib.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "unchecked", "UnstableApiUsage" })
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
    }

    @Getter
    @Accessors(fluent = true)
    @Configuration
    @RequiredArgsConstructor
    public static final class Message {
        private Type type;
        private String message;
        private List<String> lines;

        @Ignore
        private Sound sound;

        @Ignore
        private Component cached;

        @Ignore
        private boolean replacements = false;

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

        public Message(final Type type, final String message, final List<String> lines) {
            this.type = type;
            this.message = message;
            this.lines = lines;
        }

        @Contract(value = "_ -> this", mutates = "this")
        public Message sound(final OSound.@NotNull Preset preset) {
            sound = preset.getDelegate();

            return this;
        }

        @Contract("_, _ -> new")
        public @NotNull Message replace(final CharSequence target, final CharSequence replacement) {
            if (lines == null) {
                final Message updated = new Message(type, message.replace(target, replacement));

                updated.replacements = true;

                return updated;
            }

            final List<String> replaced = lines.stream()
                .map(line -> line.replace(target, replacement))
                .toList();

            final Message updated = new Message(type, replaced);

            updated.replacements = true;

            return updated;
        }

        public Component render() {
            if (!replacements && cached != null) {
                return cached;
            }

            final String joined = lines != null
                ? String.join("\n", lines)
                : message;

            final Component component = OTextHelper.resolveCommonPlaceholders(joined).deserialize();

            if (!replacements) {
                cached = component;
            }

            return component;
        }

        private void send(final CommandSender sender, final Component component, final OSound sound) {
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

            if (sound == null) {
                return;
            }

            sound.play(sender);
        }

        public void send(final CommandSender sender) {
            final OSound sound = this.sound != null
                ? OSound.builder().sound(this.sound).build()
                : null;

            send(sender, render(), sound);
        }

        public void send(final @NotNull Iterable<? extends CommandSender> senders) {
            final Component component = render();

            final OSound sound = this.sound != null
                ? OSound.builder().sound(this.sound).build()
                : null;

            for (final CommandSender sender : senders) {
                send(sender, component, sound);
            }
        }

        public void broadcast() {
            send(OPlugin.get().getServer().getOnlinePlayers());
        }
    }
}