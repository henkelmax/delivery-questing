package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class EnvelopeContainer extends ContainerBase {

    public EnvelopeContainer(int id, PlayerInventory playerInventory, IInventory inventory) {
        super(Containers.ENVELOPE_CONTAINER, id, playerInventory, inventory);

        addSlot(new NonRecursiveSlot(inventory, 0, 80, 20) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addPlayerInventorySlots();
    }

    public EnvelopeContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(1));
    }

    @Override
    public int getInvOffset() {
        return -33;
    }

    @Override
    public int getInventorySize() {
        return 1;
    }
}
