package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class BarrelTileEntity extends TileEntity implements IFluidHandler {

    private FluidTank tank;
    private Tier tier;

    public BarrelTileEntity(Tier tier) {
        super(ModTileEntities.BARREL);
        this.tier = tier;
        if (tier != null) {
            tank = new FluidTank(BarrelBlock.getMillibuckets(tier));
        }
    }

    public BarrelTileEntity() {
        this(null);
    }

    public FluidTank getTank() {
        return tank;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        tier = ((BarrelBlock) state.getBlock()).getTier();
        tank = new FluidTank(BarrelBlock.getMillibuckets(tier));
        tank.readFromNBT(compound.getCompound("Fluid"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Fluid", tank.writeToNBT(new CompoundNBT()));
        return super.write(compound);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!removed && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> tank).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public int getTanks() {
        return tank.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int t) {
        return tank.getFluidInTank(t);
    }

    @Override
    public int getTankCapacity(int t) {
        return tank.getTankCapacity(t);
    }

    @Override
    public boolean isFluidValid(int t, FluidStack stack) {
        return tank.isFluidValid(t, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return tank.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return tank.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return tank.drain(maxDrain, action);
    }
}
