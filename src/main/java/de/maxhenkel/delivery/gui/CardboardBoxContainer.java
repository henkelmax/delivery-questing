package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.Tier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;

public abstract class CardboardBoxContainer extends ContainerBase {

    protected Tier tier;

    public CardboardBoxContainer(ContainerType type, int id, PlayerInventory playerInventory, IInventory inventory, Tier tier) {
        super(type, id, playerInventory, inventory);
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public int getInvOffset() {
        return -33;
    }

}
