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
        if (world.isRemote) {
            return;
        }
        if (world.getGameTime() % 20 == 0) {
            Group group = getGroup();
            if (group == null) {
                if (getBlockState().get(BulletinBoardBlock.CONTRACTS) != 0) {
                    world.setBlockState(getPos(), getBlockState().with(BulletinBoardBlock.CONTRACTS, 0));
                }
                return;
            }
            int size = group.getActiveTasks().getTasks().size();
            if (getBlockState().get(BulletinBoardBlock.CONTRACTS) != size) {
                world.setBlockState(getPos(), getBlockState().with(BulletinBoardBlock.CONTRACTS, Math.min(size, 3)));
            }
        }
    }
}
