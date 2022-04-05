package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.items.EnvelopeItem;
import de.maxhenkel.delivery.items.ParcelItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MailboxOutboxSlot extends Slot {

    public MailboxOutboxSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!super.mayPlace(stack)) {
            return false;
        }
        return stack.getItem() instanceof EnvelopeItem || stack.getItem() instanceof ParcelItem;
    }
}
