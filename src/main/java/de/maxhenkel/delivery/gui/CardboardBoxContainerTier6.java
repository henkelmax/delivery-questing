package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Tier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class CardboardBoxContainerTier6 extends CardboardBoxContainer {

    public CardboardBoxContainerTier6(int id, PlayerInventory playerInventory, IInventory inventory) {
        super(Containers.CARDBOARD_TIER_6_CONTAINER, id, playerInventory, inventory, Tier.TIER_6);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 6; j++) {
                addSlot(new NonRecursiveSlot(inventory, i + j * 9, 8 + i * 18, 20 + j * 18));
            }
        }

        addPlayerInventorySlots();
    }

    public CardboardBoxContainerTier6(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(54));
    }

    @Override
    public int getInventorySize() {
        return 54;
    }

    @Override
    public int getInvOffset() {
        return 57;
    }
}
