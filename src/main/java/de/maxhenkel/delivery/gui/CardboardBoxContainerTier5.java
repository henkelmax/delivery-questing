package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.tileentity.CardboradBoxTileEntity;
import net.minecraft.entity.player.PlayerInventory;

public class CardboardBoxContainerTier5 extends CardboardBoxContainer {

    public CardboardBoxContainerTier5(int id, PlayerInventory playerInventory, CardboradBoxTileEntity cardboadBox) {
        super(Containers.CARDBOARD_TIER_5_CONTAINER, id, playerInventory, cardboadBox, Tier.TIER_5);

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 9; i++) {
                addSlot(new NonRecursiveSlot(inventory, i + j * 9, 8 + i * 18, 20 + j * 18));
            }
        }

        addPlayerInventorySlots();
    }

    @Override
    public int getInventorySize() {
        return 27;
    }

    @Override
    public int getInvOffset() {
        return 3;
    }
}
