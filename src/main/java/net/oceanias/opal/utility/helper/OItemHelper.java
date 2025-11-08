package net.oceanias.opal.utility.helper;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class OItemHelper {
    public ItemStack @NotNull [] cloneNonAir(final ItemStack @NotNull [] items) {
        final int length = items.length;
        final ItemStack[] result = new ItemStack[length];

        for (int index = 0; index < length; index++) {
            final ItemStack item = items[index];

            result[index] = (item != null && item.getType() != Material.AIR) ? item.clone() : null;
        }

        return result;
    }

    public boolean hasNonAir(final ItemStack @NotNull [] items) {
        for (final ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            return true;
        }

        return false;
    }
}