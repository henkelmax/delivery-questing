package de.maxhenkel.delivery.blocks.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.UUID;

public class GroupTileEntity extends TileEntity {

    @Nullable
    private UUID group;

    public GroupTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Nullable
    public UUID getGroup() {
        return group;
    }

    public void setGroup(@Nullable UUID group) {
        this.group = group;
        markDirty();
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);

        if (compound.contains("Group")) {
            group = compound.getUniqueId("Group");
        } else {
            group = null;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if (group != null) {
            compound.putUniqueId("Group", group);
        }
        return super.write(compound);
    }

}
