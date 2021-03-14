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
        if (level.isClientSide) {
            return;
        }

        if (level.getDayTime() % 24000 == 20) {
            Group group = getGroup();
            if (group != null) {
                group.handInTaskItems(clearMailbox());
            }
        }

        if (level.getGameTime() % 20 == 0) {
            boolean hasMail = !outbox.stream().allMatch(ItemStack::isEmpty);
            if (hasMail != getBlockState().getValue(MailboxBlock.NEW_MAIL)) {
                level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(MailboxBlock.NEW_MAIL, hasMail), 3);
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
        setChanged();
        return items;
    }

    public IInventory getOutbox() {
        return new ItemListInventory(outbox, this::setChanged);
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
        return new ItemListInventory(group.getMailboxInbox(), this::setChanged);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        outbox.clear();
        ItemUtils.readInventory(compound, "Outbox", outbox);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        ItemUtils.saveInventory(compound, "Outbox", outbox);
        return super.save(compound);
    }

}
