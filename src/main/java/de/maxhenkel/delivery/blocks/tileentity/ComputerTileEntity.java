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
        if (world.isRemote) {
            return;
        }
        if (world.getGameTime() % 20 != 0) {
            return;
        }
        if (energy.getEnergyStored() <= 0) {
            if (getBlockState().get(ComputerBlock.ON)) {
                world.setBlockState(pos, getBlockState().with(ComputerBlock.ON, false));
            }
        } else {
            if (!getBlockState().get(ComputerBlock.ON)) {
                world.setBlockState(pos, getBlockState().with(ComputerBlock.ON, true));
            }
        }
    }

    public void containerTick() {
        energy.useEnergy(10, false);
        markDirty();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (removed) {
            return super.getCapability(cap, side);
        }

        if (cap == CapabilityEnergy.ENERGY) {
            if (side == null || side.equals(Direction.DOWN) || side.equals(getBlockState().get(HorizontalRotatableBlock.FACING).rotateY())) {
                return LazyOptional.of(() -> energy).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, compound.getInt("Energy"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("Energy", energy.getEnergyStored());
        return super.write(compound);
    }
}
