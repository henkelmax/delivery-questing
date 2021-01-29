package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.UUID;

public class MailboxTileEntity extends GroupTileEntity {

    private NonNullList<ItemStack> outbox;

    public MailboxTileEntity() {
        super(ModTileEntities.CARDBOARD_BOX);
        outbox = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    public IInventory getOutbox() {
        return new ItemListInventory(outbox, this::markDirty);
    }

    @Nullable
    public IInventory getInbox(ServerPlayerEntity player) {
        Progression progression = Main.getProgression(player);
        UUID g = getGroup();
        if (g == null) {
            return null;
        }
        Group group = progression.getGroup(g);
        if (group == null) {
            return null;
        }
        return new ItemListInventory(group.getMailboxInbox(), this::markDirty);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        outbox.clear();
        ItemUtils.readInventory(compound, "Outbox", outbox);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ItemUtils.saveInventory(compound, "Outbox", outbox);
        return super.write(compound);
    }

}
