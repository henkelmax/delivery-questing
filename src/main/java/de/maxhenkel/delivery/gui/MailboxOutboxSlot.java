package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.blocks.CardboardBoxBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class MailboxOutboxSlot extends Slot {

    public MailboxOutboxSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (!super.isItemValid(stack)) {
            return false;
        }
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof CardboardBoxBlock;
    }
}
