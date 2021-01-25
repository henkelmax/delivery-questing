package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.blocks.CardboardBoxBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;

public class CardboardBoxContainerTier3 extends CardboardBoxContainer {

    public CardboardBoxContainerTier3(int id, PlayerInventory playerInventory, IInventory inventory) {
        super(Containers.CARDBOARD_TIER_3_CONTAINER, id, playerInventory, inventory, CardboardBoxBlock.Tier.TIER_3);

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 20));
        }

        addPlayerInventorySlots();
    }

    public CardboardBoxContainerTier3(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(9));
    }

    @Override
    public int getInventorySize() {
        return 9;
    }

}
