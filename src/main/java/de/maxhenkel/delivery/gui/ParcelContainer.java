package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;

public class ParcelContainer extends ContainerBase {

    public ParcelContainer(int id, Inventory playerInventory, Container inventory) {
        super(Containers.PARCEL_CONTAINER, id, playerInventory, inventory);

        addSlot(new NonRecursiveSlot(inventory, 0, 80, 20) {
            @Override
            public int getMaxStackSize() {
                return 16;
            }
        });
        addPlayerInventorySlots();
    }

    public ParcelContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(1));
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
