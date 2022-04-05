package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BulletinBoardContainer extends ContainerBase {

    private Group group;

    public BulletinBoardContainer(int id, Inventory playerInventory, BlockEntity tileEntity, Group group) {
        super(Containers.BULLETIN_BOARD_CONTAINER, id, playerInventory, null);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

}
