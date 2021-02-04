package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class TaskContainerSlot extends Slot {

    public TaskContainerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return super.isItemValid(stack) && Group.getTaskContainer(stack) != null;
    }
}
