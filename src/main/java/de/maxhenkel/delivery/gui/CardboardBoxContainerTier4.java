package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Tier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class CardboardBoxContainerTier4 extends CardboardBoxContainer {

    public CardboardBoxContainerTier4(int id, PlayerInventory playerInventory, IInventory inventory) {
        super(Containers.CARDBOARD_TIER_4_CONTAINER, id, playerInventory, inventory, Tier.TIER_4);

        for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 9; i++) {
                addSlot(new NonRecursiveSlot(inventory, i + j * 9, 8 + i * 18, 20 + j * 18));
            }
        }

        addPlayerInventorySlots();
    }

    public CardboardBoxContainerTier4(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(18));
    }

    @Override
    public int getInventorySize() {
        return 18;
    }

    @Override
    public int getInvOffset() {
        return -15;
    }
}
