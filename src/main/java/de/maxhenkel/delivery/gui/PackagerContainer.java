package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.blocks.tileentity.PackagerTileEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

public class PackagerContainer extends ContainerBase {

    protected PackagerTileEntity packager;
    protected Inventory playerInventory;

    public PackagerContainer(int id, Inventory playerInventory, PackagerTileEntity packager) {
        super(Containers.PACKAGER_CONTAINER, id, playerInventory, packager.getInventory());
        this.playerInventory = playerInventory;
        this.packager = packager;

        addSlot(new UpgradeSlot(packager.getUpgradeInventory(), 0, 7, 17));

        addSlot(new NonRecursiveSlot(inventory, 0, 79, 36));
        addSlot(new TaskContainerSlot(inventory, 1, 133, 36));

        addPlayerInventorySlots();
        addDataSlots(packager.getFields());
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (playerInventory.player instanceof ServerPlayer) {
            packager.syncContents((ServerPlayer) playerInventory.player);
        }
    }

    public PackagerTileEntity getPackager() {
        return packager;
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
