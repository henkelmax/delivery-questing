package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.corelib.inventory.LockedSlot;
import de.maxhenkel.delivery.blocks.tileentity.MailboxTileEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;

public class MailboxContainer extends ContainerBase {

    private MailboxTileEntity mailbox;

    public MailboxContainer(int id, Inventory playerInventory, MailboxTileEntity mailbox) {
        super(Containers.MAILBOX_CONTAINER, id, playerInventory, null);
        this.mailbox = mailbox;
        Container inbox = new SimpleContainer(4);
        if (playerInventory.player instanceof ServerPlayer) {
            Container i = mailbox.getInbox((ServerPlayer) playerInventory.player);
            if (i != null) {
                inbox = i;
            }
        }
        Container outbox = mailbox.getOutbox();

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
