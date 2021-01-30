package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.corelib.inventory.LockedSlot;
import de.maxhenkel.delivery.blocks.tileentity.MailboxTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class MailboxContainer extends ContainerBase {

    private MailboxTileEntity mailbox;

    public MailboxContainer(int id, PlayerInventory playerInventory, MailboxTileEntity mailbox) {
        super(Containers.MAILBOX_CONTAINER, id, playerInventory, null);
        this.mailbox = mailbox;
        IInventory inbox = new Inventory(4);
        if (playerInventory.player instanceof ServerPlayerEntity) {
            IInventory i = mailbox.getInbox((ServerPlayerEntity) playerInventory.player);
            if (i != null) {
                inbox = i;
            }
        }
        IInventory outbox = mailbox.getOutbox();

        for (int i = 0; i < 4; i++) {
            addSlot(new LockedSlot(inbox, i, 8 + i * 18, 46, true, false));
        }

        for (int i = 0; i < 4; i++) {
            addSlot(new MailboxOutboxSlot(outbox, i, 98 + i * 18, 46));
        }

        addPlayerInventorySlots();
    }

    public MailboxTileEntity getMailbox() {
        return mailbox;
    }

    @Override
    public int getInvOffset() {
        return -7;
    }

    @Override
    public int getInventorySize() {
        return 8;
    }

}
