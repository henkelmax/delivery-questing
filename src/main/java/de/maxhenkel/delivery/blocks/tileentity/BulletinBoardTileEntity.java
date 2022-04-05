package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.delivery.blocks.BulletinBoardBlock;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BulletinBoardTileEntity extends GroupTileEntity implements IServerTickableBlockEntity {

    public BulletinBoardTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.BULLETIN_BOARD, pos, state);
    }

    @Override
    public void tickServer() {
        if (level.getGameTime() % 20 == 0) {
            Group group = getGroup();
            if (group == null) {
                if (getBlockState().getValue(BulletinBoardBlock.CONTRACTS) != 0) {
                    level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BulletinBoardBlock.CONTRACTS, 0));
                }
                return;
            }
            int size = group.getActiveTasks().getTasks().size();
            if (getBlockState().getValue(BulletinBoardBlock.CONTRACTS) != size) {
                level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(BulletinBoardBlock.CONTRACTS, Math.min(size, 3)));
            }
        }
    }
}
