package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.delivery.blocks.ComputerBlock;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

public class ComputerTileEntity extends GroupTileEntity implements ITickableTileEntity {

    private static final int ENERGY_CAPACITY = 16_000;

    private UsableEnergyStorage energy;

    public ComputerTileEntity() {
        super(ModTileEntities.COMPUTER);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0);
    }

    public UsableEnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            return;
        }
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
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (remove) {
            return super.getCapability(cap, side);
        }

        if (cap == CapabilityEnergy.ENERGY) {
            if (side == null || side.equals(Direction.DOWN) || side.equals(getBlockState().getValue(HorizontalRotatableBlock.FACING).getClockWise())) {
                return LazyOptional.of(() -> energy).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, compound.getInt("Energy"));
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("Energy", energy.getEnergyStored());
        return super.save(compound);
    }
}
