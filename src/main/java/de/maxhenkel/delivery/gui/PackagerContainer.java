package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.blocks.tileentity.PackagerTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;

public class PackagerContainer extends ContainerBase {

    protected PackagerTileEntity packager;
    protected PlayerInventory playerInventory;

    public PackagerContainer(int id, PlayerInventory playerInventory, PackagerTileEntity packager) {
        super(Containers.PACKAGER_CONTAINER, id, playerInventory, packager.getInventory());
        this.playerInventory = playerInventory;
        this.packager = packager;

        addSlot(new UpgradeSlot(packager.getUpgradeInventory(), 0, 7, 17));

        addSlot(new NonRecursiveSlot(inventory, 0, 79, 36));
        addSlot(new TaskContainerSlot(inventory, 1, 133, 36));

        addPlayerInventorySlots();
        trackIntArray(packager.getFields());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (playerInventory.player instanceof ServerPlayerEntity) {
            packager.syncContents((ServerPlayerEntity) playerInventory.player);
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
