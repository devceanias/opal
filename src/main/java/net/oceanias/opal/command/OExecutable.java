package net.oceanias.opal.command;

import dev.jorel.commandapi.CommandPermission;

public interface OExecutable {
    CommandPermission getPermission();
}