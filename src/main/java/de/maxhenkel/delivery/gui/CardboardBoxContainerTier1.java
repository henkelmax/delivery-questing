package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.tileentity.CardboradBoxTileEntity;
import net.minecraft.world.entity.player.Inventory;

public class CardboardBoxContainerTier1 extends CardboardBoxContainer {

    public CardboardBoxContainerTier1(int id, Inventory playerInventory, CardboradBoxTileEntity cardboadBox) {
        super(Containers.CARDBOARD_TIER_1_CONTAINER, id, playerInventory, cardboadBox, Tier.TIER_1);

        addSlot(new NonRecursiveSlot(inventory, 0, 80, 20));
        addPlayerInventorySlots();
    }

    @Override
    public int getInventorySize() {
        return 1;
    }

}
