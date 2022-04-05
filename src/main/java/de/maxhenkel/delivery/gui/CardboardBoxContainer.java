package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.tileentity.CardboradBoxTileEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public abstract class CardboardBoxContainer extends ContainerBase {

    protected CardboradBoxTileEntity cardboradBox;
    protected Tier tier;

    public CardboardBoxContainer(MenuType type, int id, Inventory playerInventory, CardboradBoxTileEntity cardboradBox, Tier tier) {
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
    public boolean stillValid(Player player) {
        if (cardboradBox.isRemoved()) {
            return false;
        }
        return super.stillValid(player);
    }
}
