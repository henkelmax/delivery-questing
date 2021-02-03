package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Tier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class CardboardBoxContainerTier2 extends CardboardBoxContainer {

    public CardboardBoxContainerTier2(int id, PlayerInventory playerInventory, IInventory inventory) {
        super(Containers.CARDBOARD_TIER_2_CONTAINER, id, playerInventory, inventory, Tier.TIER_2);

        for (int i = 0; i < 4; i++) {
            addSlot(new NonRecursiveSlot(inventory, i, 52 + i * 18, 20));
        }

        addPlayerInventorySlots();
    }

    public CardboardBoxContainerTier2(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(4));
    }

    @Override
    public int getInventorySize() {
        return 4;
    }

}
