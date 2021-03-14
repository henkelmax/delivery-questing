package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.IContainerFactory;

public class ContainerFactoryTask<T extends ContainerBase> implements IContainerFactory<T> {

    private final ContainerCreator<T> containerCreator;

    public ContainerFactoryTask(ContainerCreator<T> containerCreator) {
        this.containerCreator = containerCreator;
    }

    public T create(int windowId, PlayerInventory inv, PacketBuffer data) {
        Task task = new Task();
        task.deserializeNBT(data.readNbt());

        try {
            return this.containerCreator.create(windowId, inv, task);
        } catch (ClassCastException var6) {
            return null;
        }
    }

    public interface ContainerCreator<T extends Container> {
        T create(int id, PlayerInventory inv, Task tasks);
    }
}
