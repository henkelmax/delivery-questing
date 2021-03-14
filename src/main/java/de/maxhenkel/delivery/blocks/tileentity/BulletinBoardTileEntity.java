package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.delivery.blocks.BulletinBoardBlock;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.tileentity.ITickableTileEntity;

public class BulletinBoardTileEntity extends GroupTileEntity implements ITickableTileEntity {

    public BulletinBoardTileEntity() {
        super(ModTileEntities.BULLETIN_BOARD);
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            return;
        }
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
