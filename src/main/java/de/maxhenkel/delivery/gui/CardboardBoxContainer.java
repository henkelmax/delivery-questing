package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.blocks.CardboardBoxBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;

public abstract class CardboardBoxContainer extends ContainerBase {

    protected CardboardBoxBlock.Tier tier;

    public CardboardBoxContainer(ContainerType type, int id, PlayerInventory playerInventory, IInventory inventory, CardboardBoxBlock.Tier tier) {
        super(type, id, playerInventory, inventory);
        this.tier = tier;
    }

    public CardboardBoxBlock.Tier getTier() {
        return tier;
    }

    @Override
    public int getInvOffset() {
        return -33;
    }

}
