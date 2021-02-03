package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.corelib.inventory.LockedSlot;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class DronePadContainer extends ContainerBase {

    protected DronePadTileEntity dronePadTileEntity;

    public DronePadContainer(int id, PlayerInventory playerInventory, DronePadTileEntity dronePadTileEntity) {
        super(Containers.DRONE_PAD_CONTAINER, id, playerInventory, dronePadTileEntity.getInventory());
        this.dronePadTileEntity = dronePadTileEntity;
        addSlot(new Slot(inventory, 0, 53, 36) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return Group.getTaskContainer(stack) != null; //TODO prevent parcels and envelopes or render them
            }
        });
        if (dronePadTileEntity.getWorld().isRemote) {
            addSlot(new LockedSlot(new Inventory(1), 0, 107, 36, true, true));
        } else {
            addSlot(new LockedSlot(dronePadTileEntity.getTemporaryDroneInventory(), 0, 107, 36, true, true));
        }

        addPlayerInventorySlots();
        trackIntArray(dronePadTileEntity.getFields());
    }

    public DronePadTileEntity getDronePadTileEntity() {
        return dronePadTileEntity;
    }

    @Override
    public int getInvOffset() {
        return 0;
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

}
