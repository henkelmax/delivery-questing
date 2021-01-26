package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.inventory.ItemListInventory;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

public class MailboxTileEntity extends TileEntity {

    private NonNullList<ItemStack> outbox;

    public MailboxTileEntity() {
        super(ModTileEntities.CARDBOARD_BOX);
        outbox = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        ItemStackHelper.loadAllItems(compound.getCompound("Outbox"), outbox);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Outbox", ItemStackHelper.saveAllItems(new CompoundNBT(), outbox, true));
        return super.write(compound);
    }

    public IInventory getOutbox() {
        return new ItemListInventory(outbox, this::markDirty);
    }
}
