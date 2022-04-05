package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.MailboxBlock;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class MailboxTileEntity extends GroupTileEntity implements IServerTickableBlockEntity {

    private NonNullList<ItemStack> outbox;

    public MailboxTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.MAILBOX, pos, state);
        outbox = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    public void tickServer() {
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

    public Container getOutbox() {
        return new ItemListInventory(outbox, this::setChanged);
    }

    @Nullable
    public Container getInbox(ServerPlayer player) {
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
    public void load(CompoundTag compound) {
        super.load(compound);
        outbox.clear();
        ItemUtils.readInventory(compound, "Outbox", outbox);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ItemUtils.saveInventory(compound, "Outbox", outbox);
    }
}
