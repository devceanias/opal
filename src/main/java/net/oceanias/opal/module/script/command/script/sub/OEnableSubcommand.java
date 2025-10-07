package net.oceanias.opal.module.script.command.script.sub;

import net.oceanias.opal.command.OExecutable;
import net.oceanias.opal.command.OSubcommand;
import net.oceanias.opal.module.script.command.script.OScriptCommand;
import net.oceanias.opal.module.script.OScriptManager;
import net.oceanias.opal.module.script.core.OScript;
import net.oceanias.opal.utility.extension.OCommandSenderExtension;
import net.oceanias.opal.utility.helper.OTaskHelper;
import java.util.List;
import java.util.Objects;
import org.bukkit.Sound;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@ExtensionMethod(OCommandSenderExtension.class)
@RequiredArgsConstructor
public final class OEnableSubcommand extends OSubcommand {
    private final OScriptCommand parent;

    @Override
    public OExecutable getParent() {
        return parent;
    }

    @Contract(pure = true)
    @Override
    public @NotNull String getLabel() {
        return "enable";
    }

    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> getDescription() {
        return List.of("Enables a script.");
    }

    @Override
    public Argument<String> getSubcommand() {
        final String[] scripts = OScriptManager.get().getDisabled().stream()
            .map(OScript::getLabel)
            .toArray(String[]::new);

        return getBase()
            .thenNested(
                new StringArgument("label").replaceSuggestions(ArgumentSuggestions.strings(scripts))
                    .executes((sender, arguments) -> {
                        final String label = (String) Objects.requireNonNull(arguments.get("label"));

                        final OScript script = OScriptManager.get().getScript(label);

                        if (script == null) {
                            sender.actionDSR("&fThis script &ccould not &fbe found!");

                            return;
                        }

                        script.enable().thenAccept(result ->
                            OTaskHelper.runTask(() -> {
                                final String message = switch (result) {
                                    case EXECUTION_SUCCESS ->
                                        "&fThe script &6" + script.getLabel() + " &fhas been &aenabled&f.";
                                    case ALREADY_ENABLED -> "&fThis script is &calready enabled&f!";
                                    case ALREADY_DISABLED -> null;
                                    case COMPILATION_ERROR, RUNTIME_ERROR, IO_ERROR ->
                                        "&fAn &cerror occurred &fwhile enabling this script! &cCheck console.";
                                };

                                if (message == null) {
                                    return;
                                }

                                sender.actionDSR(message);
                                sender.soundDSR(Sound.BLOCK_NOTE_BLOCK_BELL);
                            })
                        );
                    })
            );
    }
}