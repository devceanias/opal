package net.oceanias.opal.utility.helper;

import org.bukkit.Location;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "unused", "BooleanMethodIsAlwaysInverted" })
@UtilityClass
public final class OLocationHelper {
    public boolean hasMovedExact(final @NotNull Location from, final @NotNull Location to) {
        return from.getX() != to.getX()
            || from.getY() != to.getY()
            || from.getZ() != to.getZ();
    }

    public boolean hasMovedBlock(final @NotNull Location from, final @NotNull Location to) {
        return from.getBlockX() != to.getBlockX()
            || from.getBlockY() != to.getBlockY()
            || from.getBlockZ() != to.getBlockZ();
    }

    public @NotNull String formatBlockCoordinates(final @NotNull Location location, final String colour) {
        return String.format(
            "%s%d&f, %s%d&f, %s%d",
            colour, location.getBlockX(),
            colour, location.getBlockY(),
            colour, location.getBlockZ()
        );
    }

    public @NotNull String formatPreciseCoordinates(final @NotNull Location location, final String colour) {
        return String.format(
            "%s%.2f&f, %s%.2f&f, %s%.2f",
            colour, location.getX(),
            colour, location.getY(),
            colour, location.getZ()
        );
    }
}