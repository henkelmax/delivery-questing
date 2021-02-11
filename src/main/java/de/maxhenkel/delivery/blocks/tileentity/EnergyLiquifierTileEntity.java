package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.energy.EnergyUtils;
import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.corelib.fluid.FluidUtils;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.corelib.inventory.RestrictedItemStackHandler;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.fluid.ModFluids;
import de.maxhenkel.delivery.items.UpgradeItem;
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

import javax.annotation.Nullable;

public class EnergyLiquifierTileEntity extends TileEntity implements ITickableTileEntity, RestrictedItemStackHandler.ItemValidator {

    private static final int TANK_CAPACITY = 16_000;
    private static final int ENERGY_CAPACITY = 16_000;

    private final IIntArray fields = new IIntArray() {
        public int get(int index) {
            switch (index) {
                case 0:
                    return energy.getEnergyStored() + 1;
                case 1:
                    return tank.getFluidAmount() + 1;
                case 2:
                    return reversed ? 2 : 1;
            }
            return 0;
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                    energy.setEnergy(value - 1);
                    break;
                case 1:
                    tank.setFluid(new FluidStack(ModFluids.LIQUID_ENERGY, value - 1));
                    break;
                case 2:
                    reversed = value != 1;
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
    private NonNullList<ItemStack> upgradeInventory;
    private boolean reversed;

    public EnergyLiquifierTileEntity() {
        super(ModTileEntities.ENERGY_LIQUIFIER);
        tank = new FluidTank(TANK_CAPACITY, fluidStack -> fluidStack.getFluid() == ModFluids.LIQUID_ENERGY);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, ENERGY_CAPACITY);
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
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

        int rate = getRate();
        if (reversed) {
            if (energy.getEnergyStored() <= ENERGY_CAPACITY) {
                energy.receiveEnergy(tank.drain(new FluidStack(ModFluids.LIQUID_ENERGY, rate), IFluidHandler.FluidAction.EXECUTE).getAmount(), false);
                markDirty();
            }
        } else {
            if (tank.getFluidAmount() <= TANK_CAPACITY) {
                tank.fill(new FluidStack(ModFluids.LIQUID_ENERGY, energy.useEnergy(rate, false)), IFluidHandler.FluidAction.EXECUTE);
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

    public IInventory getUpgradeInventory() {
        return new ItemListInventory(upgradeInventory, this::markDirty);
    }

    @Nullable
    public Tier getTier() {
        ItemStack stack = upgradeInventory.get(0);
        if (stack.getItem() instanceof UpgradeItem) {
            UpgradeItem upgradeItem = (UpgradeItem) stack.getItem();
            return upgradeItem.getTier();
        }
        return null;
    }

    public int getRate() {
        Tier tier = getTier();
        if (tier == null) {
            return 1;
        }
        switch (tier) {
            case TIER_1:
                return 2;
            case TIER_2:
                return 3;
            case TIER_3:
                return 5;
            case TIER_4:
                return 8;
            case TIER_5:
                return 16;
            case TIER_6:
            default:
                return 64;
        }
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
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("UpgradeInventory"), upgradeInventory);
        reversed = compound.getBoolean("Reversed");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Fluid", tank.writeToNBT(new CompoundNBT()));
        compound.putInt("Energy", energy.getEnergyStored());
        compound.put("Inventory", ItemStackHelper.saveAllItems(new CompoundNBT(), inventory, true));
        compound.put("UpgradeInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), upgradeInventory, true));
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
