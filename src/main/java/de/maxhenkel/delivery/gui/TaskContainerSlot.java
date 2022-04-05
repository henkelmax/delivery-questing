package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TaskContainerSlot extends Slot {

    public TaskContainerSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && Group.getTaskContainer(stack) != null;
    }
}
