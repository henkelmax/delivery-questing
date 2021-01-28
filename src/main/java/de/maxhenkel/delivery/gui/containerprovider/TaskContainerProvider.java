package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class TaskContainerProvider<T extends ContainerBase> implements INamedContainerProvider {

    private ContainerFactoryTask.ContainerCreator<T> container;
    private Task task;
    private ITextComponent title;

    public TaskContainerProvider(ContainerFactoryTask.ContainerCreator<T> container, Task task, ITextComponent title) {
        this.container = container;
        this.task = task;
        this.title = title;
    }

    @Override
    public ITextComponent getDisplayName() {
        return title;
    }

    public static <T extends ContainerBase> void openGui(PlayerEntity player, Task task, ITextComponent title, ContainerFactoryTask.ContainerCreator<T> containerCreator) {
        if (player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new TaskContainerProvider<T>(containerCreator, task, title), (packetBuffer) -> {
                packetBuffer.writeCompoundTag(task.serializeNBT());
            });
        }
    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return container.create(id, playerInventory, task);
    }
}
