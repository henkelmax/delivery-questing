package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.blocks.BarrelBlock;
import de.maxhenkel.delivery.blocks.CardboardBoxBlock;
import de.maxhenkel.delivery.items.EnvelopeItem;
import de.maxhenkel.delivery.items.ParcelItem;
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
        if (stack.getItem() instanceof BlockItem) {
            BlockItem block = (BlockItem) stack.getItem();
            if (block.getBlock() instanceof ShulkerBoxBlock) {
                return false;
            }
            if (block.getBlock() instanceof CardboardBoxBlock) {
                return false;
            }
            if (block.getBlock() instanceof BarrelBlock) {
                return false;
            }
        }
        if (stack.getItem() instanceof EnvelopeItem) {
            return false;
        } else if (stack.getItem() instanceof ParcelItem) {
            return false;
        }

        return true;
    }

}
