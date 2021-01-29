package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.MailboxBlock;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;
import java.util.UUID;

public class MailboxTileEntity extends GroupTileEntity implements ITickableTileEntity {

    private NonNullList<ItemStack> outbox;

    public MailboxTileEntity() {
        super(ModTileEntities.MAILBOX);
        outbox = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        if (world.getDayTime() % 24000 == 20) {
            Group group = getGroup();
            if (group != null) {
                group.handInTaskItems(clearMailbox());
            }
        }

        if (world.getGameTime() % 20 == 0) {
            boolean hasMail = !outbox.stream().allMatch(ItemStack::isEmpty);
            if (hasMail != getBlockState().get(MailboxBlock.NEW_MAIL)) {
                world.setBlockState(pos, world.getBlockState(pos).with(MailboxBlock.NEW_MAIL, hasMail), 3);
            }
        }
    }

    public NonNullList<ItemStack> clearMailbox() {
        NonNullList<ItemStack> items = NonNullList.create();
        for (ItemStack stack : outbox) {
            if (!stack.isEmpty()) {
                items.add(stack.copy());
            }
        }
        outbox.clear();
        markDirty();
        return items;
    }

    public IInventory getOutbox() {
        return new ItemListInventory(outbox, this::markDirty);
    }

    @Nullable
    public IInventory getInbox(ServerPlayerEntity player) {
        Progression progression = Main.getProgression(player);
        UUID g = getGroupID();
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
