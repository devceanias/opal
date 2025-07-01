package net.oceanias.opal.command;

import dev.jorel.commandapi.CommandPermission;

@SuppressWarnings("unused")
public interface OExecutable {
    CommandPermission getPermission();
}