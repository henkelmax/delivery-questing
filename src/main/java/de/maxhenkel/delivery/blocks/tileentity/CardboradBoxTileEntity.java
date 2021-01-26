package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.delivery.blocks.CardboardBoxBlock;
import de.maxhenkel.delivery.gui.NonRecursiveSlot;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class CardboradBoxTileEntity extends TileEntity {

    private NonNullList<ItemStack> inventory;
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
        tier = ((CardboardBoxBlock) state.getBlock()).getTier();
        inventory = NonNullList.withSize(tier.getSlotCount(), ItemStack.EMPTY);
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(this::getItemHandler).cast();
        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler getItemHandler() {
        return new ItemStackHandler(inventory) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return super.isItemValid(slot, stack) && NonRecursiveSlot.isNonRecursive(stack);
            }
        };
    }

}
