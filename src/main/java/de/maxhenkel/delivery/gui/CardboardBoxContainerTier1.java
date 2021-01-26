package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.blocks.CardboardBoxBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class CardboardBoxContainerTier1 extends CardboardBoxContainer {

    public CardboardBoxContainerTier1(int id, PlayerInventory playerInventory, IInventory inventory) {
        super(Containers.CARDBOARD_TIER_1_CONTAINER, id, playerInventory, inventory, CardboardBoxBlock.Tier.TIER_1);

        addSlot(new NonRecursiveSlot(inventory, 0, 80, 20));
        addPlayerInventorySlots();
    }

    public CardboardBoxContainerTier1(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(CardboardBoxBlock.Tier.TIER_1.getSlotCount()));
    }

    @Override
    public int getInventorySize() {
        return 1;
    }

}
