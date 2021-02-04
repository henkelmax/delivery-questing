package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class NonRecursiveSlot extends Slot {

    public NonRecursiveSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (!super.isItemValid(stack)) {
            return false;
        }

        return isNonRecursive(stack);
    }

    public static boolean isNonRecursive(ItemStack stack) {
        if (Group.getTaskContainer(stack) != null) {
            return false;
        }
        if (stack.getItem() instanceof BlockItem) {
            BlockItem block = (BlockItem) stack.getItem();
            if (block.getBlock() instanceof ShulkerBoxBlock) {
                return false;
            }
        }

        return true;
    }

}
