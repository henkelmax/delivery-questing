package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.IContainerFactory;

public class ContainerFactoryGroup<T extends ContainerBase> implements IContainerFactory<T> {

    private final ContainerCreator<T> containerCreator;

    public ContainerFactoryGroup(ContainerCreator<T> containerCreator) {
        this.containerCreator = containerCreator;
    }

    public T create(int windowId, Inventory inv, FriendlyByteBuf data) {
        BlockEntity tileEntity = inv.player.level.getBlockEntity(data.readBlockPos());
        Group group = new Group();
        group.deserializeNBT(data.readNbt());

        try {
            return this.containerCreator.create(windowId, inv, tileEntity, group);
        } catch (ClassCastException var6) {
            return null;
        }
    }

    public interface ContainerCreator<T extends AbstractContainerMenu> {
        T create(int id, Inventory inv, BlockEntity tileEntity, Group group);
    }
}
