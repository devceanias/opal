package net.oceanias.opal.menu.item;

import net.oceanias.opal.utility.helper.OTaskHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

@SuppressWarnings("unused")
public abstract class OAsyncItem extends OAbstractItem {
    private volatile ItemProvider provider;

    protected abstract void setItemProvider();

    protected OAsyncItem() {
        provider = new ItemWrapper(new ItemStack(Material.AIR));

        OTaskHelper.runTaskAsync(this::setItemProvider);
    }

    protected final void setProvider(final ItemProvider provider) {
        this.provider = provider;

        notifyWindows();
    }

    @Override
    public ItemProvider getItemProvider() {
        return provider;
    }
}