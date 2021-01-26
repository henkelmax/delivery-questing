package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.energy.EnergyUtils;
import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.corelib.fluid.FluidUtils;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.inventory.RestrictedItemStackHandler;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.fluid.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class EnergyLiquifierTileEntity extends TileEntity implements ITickableTileEntity, RestrictedItemStackHandler.ItemValidator {

    private static final int TANK_CAPACITY = 16_000;
    private static final int ENERGY_CAPACITY = 16_000;
    private static final int RATE = 1;

    private final IIntArray fields = new IIntArray() {
        public int get(int index) {
            switch (index) {
                case 0:
                    return energy.getEnergyStored();
                case 1:
                    return tank.getFluidAmount();
                case 2:
                    return reversed ? 1 : 0;
            }
            return 0;
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                    energy.setEnergy(value);
                    break;
                case 1:
                    tank.setFluid(new FluidStack(ModFluids.LIQUID_ENERGY, value));
                    break;
                case 2:
                    reversed = value != 0;
                    break;
            }
        }

        public int size() {
            return 3;
        }
    };

    private FluidTank tank;
    private UsableEnergyStorage energy;
    private NonNullList<ItemStack> inventory;
    private boolean reversed;

    public EnergyLiquifierTileEntity() {
        super(ModTileEntities.ENERGY_LIQUIFIER);
        tank = new FluidTank(TANK_CAPACITY, fluidStack -> fluidStack.getFluid() == ModFluids.LIQUID_ENERGY);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY);
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        inventory.get(0).getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
            if (reversed) {
                EnergyUtils.pushEnergy(energy, energyStorage, 1000);
            } else {
                EnergyUtils.pushEnergy(energyStorage, energy, 1000);
            }
            markDirty();
        });

        if (reversed) {
            if (energy.getEnergyStored() + RATE <= ENERGY_CAPACITY) {
                energy.receiveEnergy(tank.drain(new FluidStack(ModFluids.LIQUID_ENERGY, RATE), IFluidHandler.FluidAction.EXECUTE).getAmount(), false);
                markDirty();
            }
        } else {
            if (tank.getFluidAmount() + RATE <= TANK_CAPACITY) {
                tank.fill(new FluidStack(ModFluids.LIQUID_ENERGY, energy.useEnergy(RATE, false)), IFluidHandler.FluidAction.EXECUTE);
                markDirty();
            }
        }

        inventory.get(1).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fluidHandlerItem -> {
            if (reversed) {
                FluidUtils.tryFluidTransfer(tank, fluidHandlerItem, 1000, true);
                inventory.set(1, fluidHandlerItem.getContainer());
            } else {
                FluidUtils.tryFluidTransfer(fluidHandlerItem, tank, 1000, true);
                inventory.set(1, fluidHandlerItem.getContainer());
            }
            markDirty();
        });
    }

    public FluidTank getTank() {
        return tank;
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public IInventory getInventory() {
        return new ItemListInventory(inventory, this::markDirty);
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
        markDirty();
    }

    public boolean isReversed() {
        return reversed;
    }

    public void reverse() {
        setReversed(!reversed);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        tank = new FluidTank(TANK_CAPACITY, fluidStack -> fluidStack.getFluid() == ModFluids.LIQUID_ENERGY);
        tank.readFromNBT(compound.getCompound("Fluid"));
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY, compound.getInt("Energy"));
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("Inventory"), inventory);
        reversed = compound.getBoolean("Reversed");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Fluid", tank.writeToNBT(new CompoundNBT()));
        compound.putInt("Energy", energy.getEnergyStored());
        compound.put("Inventory", ItemStackHelper.saveAllItems(new CompoundNBT(), inventory, true));
        compound.putBoolean("Reversed", reversed);
        return super.write(compound);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (removed) {
            return super.getCapability(cap, side);
        }

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (side == null || side.equals(getBlockState().get(HorizontalRotatableBlock.FACING))) {
                return LazyOptional.of(() -> tank).cast();
            }
        } else if (cap == CapabilityEnergy.ENERGY) {
            if (side == null || side.equals(getBlockState().get(HorizontalRotatableBlock.FACING).getOpposite())) {
                return LazyOptional.of(() -> energy).cast();
            }
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(this::getItemHandler).cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandler getItemHandler() {
        return new RestrictedItemStackHandler(inventory, this) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    public IIntArray getFields() {
        return fields;
    }

    @Override
    public boolean isValid(int i, ItemStack itemStack) {
        if (i == 0) {
            return itemStack.getCapability(CapabilityEnergy.ENERGY).isPresent();
        } else if (i == 1) {
            return itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        }
        return false;
    }
}
