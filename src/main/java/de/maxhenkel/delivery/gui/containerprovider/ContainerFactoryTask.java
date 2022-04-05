package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.IContainerFactory;

public class ContainerFactoryTask<T extends ContainerBase> implements IContainerFactory<T> {

    private final ContainerCreator<T> containerCreator;

    public ContainerFactoryTask(ContainerCreator<T> containerCreator) {
        this.containerCreator = containerCreator;
    }

    public T create(int windowId, Inventory inv, FriendlyByteBuf data) {
        Task task = new Task();
        task.deserializeNBT(data.readNbt());

        try {
            return this.containerCreator.create(windowId, inv, task);
        } catch (ClassCastException var6) {
            return null;
        }
    }

    public interface ContainerCreator<T extends AbstractContainerMenu> {
        T create(int id, Inventory inv, Task tasks);
    }
}
