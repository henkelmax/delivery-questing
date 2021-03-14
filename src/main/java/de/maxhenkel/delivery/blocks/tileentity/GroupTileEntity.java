package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.UUID;

public class GroupTileEntity extends TileEntity {

    @Nullable
    private UUID group;

    public GroupTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Nullable
    public UUID getGroupID() {
        return group;
    }

    @Nullable
    public Group getGroup() {
        if (level instanceof ServerWorld) {
            UUID groupID = getGroupID();
            if (groupID == null) {
                return null;
            }

            return Main.getProgression(((ServerWorld) level).getServer()).getGroup(groupID);
        }
        return null;
    }

    public void setGroup(@Nullable UUID group) {
        this.group = group;
        setChanged();
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);

        if (compound.contains("Group")) {
            group = compound.getUUID("Group");
        } else {
            group = null;
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (group != null) {
            compound.putUUID("Group", group);
        }
        return super.save(compound);
    }

}
