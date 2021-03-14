package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class ParcelContainer extends ContainerBase {

    public ParcelContainer(int id, PlayerInventory playerInventory, IInventory inventory) {
        super(Containers.PARCEL_CONTAINER, id, playerInventory, inventory);

        addSlot(new NonRecursiveSlot(inventory, 0, 80, 20) {
            @Override
            public int getMaxStackSize() {
                return 16;
            }
        });
        addPlayerInventorySlots();
    }

    public ParcelContainer(int id, PlayerInventory playerInventory) {
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
