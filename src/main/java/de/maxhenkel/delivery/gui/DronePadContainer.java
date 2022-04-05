package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.corelib.inventory.LockedSlot;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DronePadContainer extends ContainerBase {

    protected DronePadTileEntity dronePadTileEntity;

    public DronePadContainer(int id, Inventory playerInventory, DronePadTileEntity dronePadTileEntity) {
        super(Containers.DRONE_PAD_CONTAINER, id, playerInventory, dronePadTileEntity.getInventory());
        this.dronePadTileEntity = dronePadTileEntity;
        addSlot(new Slot(inventory, 0, 53, 36) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return Group.getTaskContainer(stack) != null;
            }
        });
        if (dronePadTileEntity.getLevel().isClientSide) {
            addSlot(new LockedSlot(new SimpleContainer(1), 0, 107, 36, true, true));
        } else {
            addSlot(new LockedSlot(dronePadTileEntity.getTemporaryDroneInventory(), 0, 107, 36, true, true));
        }

        addSlot(new UpgradeSlot(dronePadTileEntity.getUpgradeInventory(), 0, 80, 59));

        addPlayerInventorySlots();
        addDataSlots(dronePadTileEntity.getFields());
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
        return 3;
    }

}
