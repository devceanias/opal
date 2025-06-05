package net.oceanias.ocean.utility.helper;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class OLocationHelper {
    public static boolean hasMovedExact(final @NotNull Location from, final @NotNull Location to) {
        return
            from.getX() != to.getX() ||
            from.getY() != to.getY() ||
            from.getZ() != to.getZ();
    }

    public static boolean hasMovedBlock(final @NotNull Location from, final @NotNull Location to) {
        return
            from.getBlockX() != to.getBlockX() ||
            from.getBlockY() != to.getBlockY() ||
            from.getBlockZ() != to.getBlockZ();
    }
}