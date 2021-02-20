package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.tileentity.CardboradBoxTileEntity;
import net.minecraft.entity.player.PlayerInventory;

public class CardboardBoxContainerTier3 extends CardboardBoxContainer {

    public CardboardBoxContainerTier3(int id, PlayerInventory playerInventory, CardboradBoxTileEntity cardboadBox) {
        super(Containers.CARDBOARD_TIER_3_CONTAINER, id, playerInventory, cardboadBox, Tier.TIER_3);

        for (int i = 0; i < 9; i++) {
            addSlot(new NonRecursiveSlot(inventory, i, 8 + i * 18, 20));
        }

        addPlayerInventorySlots();
    }

    @Override
    public int getInventorySize() {
        return 9;
    }

}
