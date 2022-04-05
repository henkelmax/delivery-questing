package de.maxhenkel.delivery.gui.containerprovider;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

public class TaskContainerProvider<T extends ContainerBase> implements MenuProvider {

    private ContainerFactoryTask.ContainerCreator<T> container;
    private Task task;
    private Component title;

    public TaskContainerProvider(ContainerFactoryTask.ContainerCreator<T> container, Task task, Component title) {
        this.container = container;
        this.task = task;
        this.title = title;
    }

    @Override
    public Component getDisplayName() {
        return title;
    }

    public static <T extends ContainerBase> void openGui(Player player, Task task, Component title, ContainerFactoryTask.ContainerCreator<T> containerCreator) {
        if (player instanceof ServerPlayer) {
            NetworkHooks.openGui((ServerPlayer) player, new TaskContainerProvider<T>(containerCreator, task, title), (packetBuffer) -> {
                packetBuffer.writeNbt(task.serializeNBT());
            });
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
        return container.create(id, playerInventory, task);
    }
}
