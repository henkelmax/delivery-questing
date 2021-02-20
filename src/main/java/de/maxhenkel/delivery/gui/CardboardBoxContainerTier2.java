package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.tileentity.CardboradBoxTileEntity;
import net.minecraft.entity.player.PlayerInventory;

public class CardboardBoxContainerTier2 extends CardboardBoxContainer {

    public CardboardBoxContainerTier2(int id, PlayerInventory playerInventory, CardboradBoxTileEntity cardboadBox) {
        super(Containers.CARDBOARD_TIER_2_CONTAINER, id, playerInventory, cardboadBox, Tier.TIER_2);

        for (int i = 0; i < 4; i++) {
            addSlot(new NonRecursiveSlot(inventory, i, 52 + i * 18, 20));
        }

        addPlayerInventorySlots();
    }

    @Override
    public int getInventorySize() {
        return 4;
    }

}
