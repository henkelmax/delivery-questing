package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.ActiveTasks;
import net.minecraft.entity.player.PlayerInventory;

public class BulletinBoardContainer extends ContainerBase {

    private ActiveTasks tasks;

    public BulletinBoardContainer(int id, PlayerInventory playerInventory, ActiveTasks tasks) {
        super(Containers.BULLETIN_BOARD_CONTAINER, id, playerInventory, null);
        this.tasks = tasks;

        addPlayerInventorySlots();
    }

    public ActiveTasks getTasks() {
        return tasks;
    }

    @Override
    public int getInvOffset() {
        return 57;
    }

    @Override
    public int getInventorySize() {
        return 0;
    }

}
