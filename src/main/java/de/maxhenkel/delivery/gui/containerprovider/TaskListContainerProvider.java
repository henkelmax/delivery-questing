package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.ActiveTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class TaskListContainerProvider<T extends ContainerBase> implements INamedContainerProvider {

    private ContainerFactoryTaskList.ContainerCreator<T> container;
    private ActiveTasks tasks;
    private ITextComponent title;

    public TaskListContainerProvider(ContainerFactoryTaskList.ContainerCreator<T> container, ActiveTasks tasks, ITextComponent title) {
        this.container = container;
        this.tasks = tasks;
        this.title = title;
    }

    @Override
    public ITextComponent getDisplayName() {
        return title;
    }

    public static <T extends ContainerBase> void openGui(PlayerEntity player, ActiveTasks tasks, ITextComponent title, ContainerFactoryTaskList.ContainerCreator<T> containerCreator) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new TaskListContainerProvider<T>(containerCreator, tasks, title), (packetBuffer) -> {
                packetBuffer.writeCompoundTag(tasks.serializeNBT());
            });
        }

    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return container.create(id, playerInventory, tasks);
    }
}
