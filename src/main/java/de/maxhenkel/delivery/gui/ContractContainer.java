package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.world.entity.player.Inventory;

public class ContractContainer extends ContainerBase {

    private Task task;

    public ContractContainer(int id, Inventory playerInventory, Task task) {
        super(Containers.CONTRACT_CONTAINER, id, playerInventory, null);
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

}
