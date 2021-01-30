package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.player.PlayerInventory;

public class BulletinBoardContainer extends ContainerBase {

    private Group group;

    public BulletinBoardContainer(int id, PlayerInventory playerInventory, Group group) {
        super(Containers.BULLETIN_BOARD_CONTAINER, id, playerInventory, null);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

}