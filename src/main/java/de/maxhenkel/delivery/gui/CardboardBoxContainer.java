package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.tileentity.CardboradBoxTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;

public abstract class CardboardBoxContainer extends ContainerBase {

    protected CardboradBoxTileEntity cardboradBox;
    protected Tier tier;

    public CardboardBoxContainer(ContainerType type, int id, PlayerInventory playerInventory, CardboradBoxTileEntity cardboradBox, Tier tier) {
        super(type, id, playerInventory, cardboradBox.getInventory());
        this.cardboradBox = cardboradBox;
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public int getInvOffset() {
        return -33;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        if (cardboradBox.isRemoved()) {
            return false;
        }
        return super.canInteractWith(player);
    }
}
