package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.CardboardBoxBlock;
import de.maxhenkel.delivery.gui.NonRecursiveSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class CardboradBoxTileEntity extends BlockEntity {

    private NonNullList<ItemStack> inventory;
    private Tier tier;

    private LazyOptional<ItemStackHandler> itemHandlerCache;

    public CardboradBoxTileEntity(Tier tier, BlockPos pos, BlockState state) {
        super(ModTileEntities.CARDBOARD_BOX, pos, state);
        this.tier = tier;
        if (tier != null) {
            inventory = NonNullList.withSize(CardboardBoxBlock.getSlots(tier), ItemStack.EMPTY);
        }
        itemHandlerCache = LazyOptional.of(this::createItemHandler);
    }

    public CardboradBoxTileEntity(BlockPos pos, BlockState state) {
        this(null, pos, state);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        tier = ((CardboardBoxBlock) getBlockState().getBlock()).getTier();
        inventory = NonNullList.withSize(CardboardBoxBlock.getSlots(tier), ItemStack.EMPTY);
        ItemUtils.readInventory(compound, "Items", inventory);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ItemUtils.saveInventory(compound, "Items", inventory);
    }

    public Container getInventory() {
        return new ItemListInventory(inventory, this::setChanged);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        itemHandlerCache.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandlerCache.cast();
        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler createItemHandler() {
        return new ItemStackHandler(inventory) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return super.isItemValid(slot, stack) && NonRecursiveSlot.isNonRecursive(stack);
            }
        };
    }

}
