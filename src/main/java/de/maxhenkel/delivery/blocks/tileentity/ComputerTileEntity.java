package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.delivery.blocks.ComputerBlock;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

public class ComputerTileEntity extends GroupTileEntity implements IServerTickableBlockEntity {

    private static final int ENERGY_CAPACITY = 16_000;

    private UsableEnergyStorage energy;

    private LazyOptional<UsableEnergyStorage> energyCache;

    public ComputerTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.COMPUTER, pos, state);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0);
        energyCache = LazyOptional.of(() -> energy);
    }

    public UsableEnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public void tickServer() {
        if (level.getGameTime() % 20 != 0) {
            return;
        }
        if (energy.getEnergyStored() <= 0) {
            if (getBlockState().getValue(ComputerBlock.ON)) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ComputerBlock.ON, false));
            }
        } else {
            if (!getBlockState().getValue(ComputerBlock.ON)) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ComputerBlock.ON, true));
            }
        }
    }

    public void containerTick() {
        energy.useEnergy(10, false);
        setChanged();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        energyCache.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (remove) {
            return super.getCapability(cap, side);
        }

        if (cap == CapabilityEnergy.ENERGY) {
            if (side == null || side.equals(Direction.DOWN) || side.equals(getBlockState().getValue(HorizontalRotatableBlock.FACING).getClockWise())) {
                return energyCache.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, compound.getInt("Energy"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("Energy", energy.getEnergyStored());
    }
}
