package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.UUID;

public class GroupTileEntity extends BlockEntity {

    @Nullable
    private UUID group;

    public GroupTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    @Nullable
    public UUID getGroupID() {
        return group;
    }

    @Nullable
    public Group getGroup() {
        if (level instanceof ServerLevel) {
            UUID groupID = getGroupID();
            if (groupID == null) {
                return null;
            }

            return Main.getProgression(((ServerLevel) level).getServer()).getGroup(groupID);
        }
        return null;
    }

    public void setGroup(@Nullable UUID group) {
        this.group = group;
        setChanged();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        if (compound.contains("Group")) {
            group = compound.getUUID("Group");
        } else {
            group = null;
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (group != null) {
            compound.putUUID("Group", group);
        }
    }

}
