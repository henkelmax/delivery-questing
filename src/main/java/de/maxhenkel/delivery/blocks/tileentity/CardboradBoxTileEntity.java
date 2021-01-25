package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.delivery.blocks.CardboardBoxBlock;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class CardboradBoxTileEntity extends TileEntity {

    private NonNullList<ItemStack> inventory;
    @Nullable
    private CardboardBoxBlock.Tier tier;

    public CardboradBoxTileEntity(CardboardBoxBlock.Tier tier) {
        super(ModTileEntities.CARDBOARD_BOX);
        this.tier = tier;
        if (tier != null) {
            inventory = NonNullList.withSize(tier.getSlotCount(), ItemStack.EMPTY);
        }
    }

    public CardboradBoxTileEntity() {
        this(null);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        if (state != null && state.getBlock() instanceof CardboardBoxBlock) {
            tier = ((CardboardBoxBlock) state.getBlock()).getTier();
            inventory = NonNullList.withSize(tier.getSlotCount(), ItemStack.EMPTY);
        }
        ItemStackHelper.loadAllItems(compound, inventory);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ItemStackHelper.saveAllItems(compound, inventory, true);
        return super.write(compound);
    }

    public IInventory getInventory() {
        return new ItemListInventory(inventory, this::markDirty);
    }
}
