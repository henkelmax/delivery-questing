package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class GroupContainerProvider<T extends ContainerBase> implements INamedContainerProvider {

    private ContainerFactoryGroup.ContainerCreator<T> container;
    private Group group;
    private TileEntity tileEntity;
    private ITextComponent title;

    public GroupContainerProvider(ContainerFactoryGroup.ContainerCreator<T> container, TileEntity tileEntity, Group group, ITextComponent title) {
        this.container = container;
        this.tileEntity = tileEntity;
        this.group = group;
        this.title = title;
    }

    @Override
    public ITextComponent getDisplayName() {
        return title;
    }

    public static <T extends ContainerBase> void openGui(PlayerEntity player, TileEntity tileEntity, Group group, ITextComponent title, ContainerFactoryGroup.ContainerCreator<T> containerCreator) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new GroupContainerProvider<T>(containerCreator, tileEntity, group, title), (packetBuffer) -> {
                packetBuffer.writeBlockPos(tileEntity.getBlockPos());
                packetBuffer.writeNbt(group.serializeNBT());
            });
        }

    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return container.create(id, playerInventory, tileEntity, group);
    }
}
