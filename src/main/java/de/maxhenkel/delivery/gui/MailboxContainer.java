package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.corelib.inventory.LockedSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class MailboxContainer extends ContainerBase {

    public MailboxContainer(int id, PlayerInventory playerInventory, IInventory inboxInventory, IInventory outboxInventory) {
        super(Containers.MAILBOX_CONTAINER, id, playerInventory, null);

        for (int i = 0; i < 4; i++) {
            addSlot(new LockedSlot(inboxInventory, i, 52 + i * 18, 20, true, false));
        }

        for (int i = 0; i < 4; i++) {
            addSlot(new MailboxOutboxSlot(outboxInventory, i, 52 + i * 18, 51));
        }

        addPlayerInventorySlots();
    }

    public MailboxContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(4), new Inventory(4));
    }

    @Override
    public int getInvOffset() {
        return -2;
    }

    @Override
    public int getInventorySize() {
        return 8;
    }

}
