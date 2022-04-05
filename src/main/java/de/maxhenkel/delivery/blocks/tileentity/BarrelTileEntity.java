package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.BarrelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class BarrelTileEntity extends BlockEntity implements IFluidHandler {

    private FluidTank tank;
    private Tier tier;

    private LazyOptional<FluidTank> tankCache;

    public BarrelTileEntity(Tier tier, BlockPos pos, BlockState state) {
        super(ModTileEntities.BARREL, pos, state);
        this.tier = tier;
        if (tier != null) {
            tank = new FluidTank(BarrelBlock.getMillibuckets(tier));
        }
        tankCache = LazyOptional.of(() -> tank);
    }

    public BarrelTileEntity(BlockPos pos, BlockState state) {
        this(null, pos, state);
    }

    public FluidTank getTank() {
        return tank;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        tier = ((BarrelBlock) getBlockState().getBlock()).getTier();
        tank = new FluidTank(BarrelBlock.getMillibuckets(tier));
        tank.readFromNBT(compound.getCompound("Fluid"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Fluid", tank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        tankCache.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!remove && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return tankCache.cast();
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
