package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;

public class GroupContainerProvider<T extends ContainerBase> implements MenuProvider {

    private ContainerFactoryGroup.ContainerCreator<T> container;
    private Group group;
    private BlockEntity tileEntity;
    private Component title;

    public GroupContainerProvider(ContainerFactoryGroup.ContainerCreator<T> container, BlockEntity tileEntity, Group group, Component title) {
        this.container = container;
        this.tileEntity = tileEntity;
        this.group = group;
        this.title = title;
    }

    @Override
    public Component getDisplayName() {
        return title;
    }

    public static <T extends ContainerBase> void openGui(Player player, BlockEntity tileEntity, Group group, Component title, ContainerFactoryGroup.ContainerCreator<T> containerCreator) {
        if (player instanceof ServerPlayer) {
            NetworkHooks.openGui((ServerPlayer) player, new GroupContainerProvider<T>(containerCreator, tileEntity, group, title), (packetBuffer) -> {
                packetBuffer.writeBlockPos(tileEntity.getBlockPos());
                packetBuffer.writeNbt(group.serializeNBT());
            });
        }

    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
        return container.create(id, playerInventory, tileEntity, group);
    }
}
