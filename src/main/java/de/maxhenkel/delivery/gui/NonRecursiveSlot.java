package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class NonRecursiveSlot extends Slot {

    public NonRecursiveSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!super.mayPlace(stack)) {
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
