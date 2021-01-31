package de.maxhenkel.delivery.gui.computer;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.gui.Containers;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.player.PlayerInventory;

public class ComputerContainer extends ContainerBase {

    private Group group;

    public ComputerContainer(int id, PlayerInventory playerInventory, Group group) {
        super(Containers.COMPUTER_CONTAINER, id, playerInventory, null);
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }
}
