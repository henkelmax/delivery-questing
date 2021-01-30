package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.IContainerFactory;

public class ContainerFactoryGroup<T extends ContainerBase> implements IContainerFactory<T> {

    private final ContainerCreator<T> containerCreator;

    public ContainerFactoryGroup(ContainerCreator<T> containerCreator) {
        this.containerCreator = containerCreator;
    }

    public T create(int windowId, PlayerInventory inv, PacketBuffer data) {
        Group group = new Group();
        group.deserializeNBT(data.readCompoundTag());

        try {
            return this.containerCreator.create(windowId, inv, group);
        } catch (ClassCastException var6) {
            return null;
        }
    }

    public interface ContainerCreator<T extends Container> {
        T create(int id, PlayerInventory inv, Group group);
    }
}
