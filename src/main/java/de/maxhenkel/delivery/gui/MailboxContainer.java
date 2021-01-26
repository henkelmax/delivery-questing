package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class MailboxContainer extends ContainerBase {

    public MailboxContainer(int id, PlayerInventory playerInventory, IInventory outboxInventory) {
        super(Containers.MAILBOX_CONTAINER, id, playerInventory, null);

        for (int i = 0; i < 9; i++) {
            addSlot(new MailboxOutboxSlot(outboxInventory, i, 8 + i * 18, 110));
        }

        addPlayerInventorySlots();
    }

    public MailboxContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(4));
    }

    @Override
    public int getInvOffset() {
        return 57;
    }

    @Override
    public int getInventorySize() {
        return 9;
    }

}
