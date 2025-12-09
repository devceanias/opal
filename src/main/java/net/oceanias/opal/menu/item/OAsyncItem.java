package net.oceanias.opal.menu.item;

import net.oceanias.opal.OPlugin;
import net.oceanias.opal.utility.helper.OTaskHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

@SuppressWarnings("unused")
public abstract class OAsyncItem extends OAbstractItem {
    private final boolean autoAsyncExecution;

    private volatile ItemProvider provider = new ItemWrapper(new ItemStack(Material.AIR));

    protected abstract void setItemProvider();

    protected OAsyncItem(final boolean autoAsyncExecution) {
        this.autoAsyncExecution = autoAsyncExecution;

        if (autoAsyncExecution && OPlugin.get().getServer().isPrimaryThread()) {
            OTaskHelper.runTaskLaterAsync(this::setItemProvider, 1L);

            return;
        }

        OTaskHelper.runTaskLater(this::setItemProvider, 1L);
    }

    protected final void applyItemProvider(final ItemProvider provider) {
        this.provider = provider;

        notifyWindows();
    }

    @Override
    public ItemProvider getItemProvider() {
        return provider;
    }

    @Override
    public void notifyWindows() {
        if (autoAsyncExecution && OPlugin.get().getServer().isPrimaryThread()) {
            OTaskHelper.runTaskAsync(super::notifyWindows);

            return;
        }

        super.notifyWindows();
    }
}