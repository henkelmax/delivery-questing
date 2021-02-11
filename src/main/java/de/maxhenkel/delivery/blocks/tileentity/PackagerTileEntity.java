package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.fluid.ModFluids;
import de.maxhenkel.delivery.items.UpgradeItem;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.ITaskContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PackagerTileEntity extends TileEntity implements ITickableTileEntity {

    private static final int TANK_CAPACITY = 16_000;
    private static final int ENERGY_CAPACITY = 16_000;

    private final IIntArray fields = new IIntArray() {
        public int get(int index) {
            switch (index) {
                case 0:
                    return energy.getEnergyStored() + 1;
                case 1:
                    return tank.getFluidAmount() + 1;
            }
            return 0;
        }

        public void set(int index, int value) {
            switch (index) {
                case 0:
                    energy.setEnergy(value - 1);
                    break;
                case 1:
                    tank.setFluid(new FluidStack(tank.getFluid().getFluid(), value - 1));
                    break;
            }
        }

        public int size() {
            return 2;
        }
    };

    private FluidTank tank;
    private UsableEnergyStorage energy;
    private NonNullList<ItemStack> inventory;
    private NonNullList<ItemStack> upgradeInventory;

    public PackagerTileEntity() {
        super(ModTileEntities.PACKAGER);
        tank = new FluidTank(TANK_CAPACITY, fluidStack -> fluidStack.getFluid() == ModFluids.LIQUID_ENERGY);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0);
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        if (energy.getEnergyStored() <= 0) {
            return;
        }

        ItemStack taskContainerStack = inventory.get(1);
        ITaskContainer taskContainer = Group.getTaskContainer(taskContainerStack);

        if (taskContainer == null) {
            return;
        }

        if (tank.getFluidAmount() > 0 && taskContainer.canAcceptFluids(taskContainerStack) && energy.getEnergyStored() >= getFluidEnergyUsage()) {
            int count = getFluidCount();
            int added = taskContainer.add(taskContainerStack, tank, count);
            if (added > 0) {
                energy.useEnergy(getFluidEnergyUsage(), false);
            }
        }

        if (world.getGameTime() % 4 != 0) {
            return;
        }

        ItemStack toInsert = inventory.get(0);
        if (!toInsert.isEmpty() && taskContainer.canAcceptItems(taskContainerStack) && energy.getEnergyStored() >= getItemEnergyUsage()) {
            int count = getCount();
            ItemStack rest = taskContainer.add(taskContainerStack, toInsert, count);
            if (rest.getCount() < toInsert.getCount()) {
                energy.useEnergy(getItemEnergyUsage(), false);
            }
            inventory.set(0, rest);
        }
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

    public int getFluidEnergyUsage() {
        return 250;
    }

    public int getItemEnergyUsage() {
        return 1000;
    }

    public int getCount() {
        Tier tier = getTier();
        if (tier == null) {
            return 1;
        }
        switch (tier) {
            case TIER_1:
                return 2;
            case TIER_2:
                return 4;
            case TIER_3:
                return 8;
            case TIER_4:
                return 16;
            case TIER_5:
                return 32;
            case TIER_6:
            default:
                return 64;
        }
    }

    public int getFluidCount() {
        Tier tier = getTier();
        if (tier == null) {
            return 2;
        }
        switch (tier) {
            case TIER_1:
                return 8;
            case TIER_2:
                return 20;
            case TIER_3:
                return 50;
            case TIER_4:
                return 100;
            case TIER_5:
                return 250;
            case TIER_6:
            default:
                return 1000;
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        tank = new FluidTank(TANK_CAPACITY);
        tank.readFromNBT(compound.getCompound("Fluid"));
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, compound.getInt("Energy"));
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("Inventory"), inventory);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("UpgradeInventory"), upgradeInventory);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Fluid", tank.writeToNBT(new CompoundNBT()));
        compound.putInt("Energy", energy.getEnergyStored());
        compound.put("Inventory", ItemStackHelper.saveAllItems(new CompoundNBT(), inventory, true));
        compound.put("UpgradeInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), upgradeInventory, true));
        return super.write(compound);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (removed) {
            return super.getCapability(cap, side);
        }

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> tank).cast();
        } else if (cap == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> energy).cast();
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(this::getItemHandler).cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandler getItemHandler() {
        return new ItemStackHandler(inventory) {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == 1) {
                    ITaskContainer taskContainer = Group.getTaskContainer(inventory.get(1));
                    if (taskContainer != null) {
                        if (!taskContainer.isFull(inventory.get(1))) {
                            return ItemStack.EMPTY;
                        }
                    }
                    return super.extractItem(slot, amount, simulate);
                } else {
                    return ItemStack.EMPTY;
                }
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (Group.getTaskContainer(stack) != null) {
                    if (slot == 0) {
                        return stack;
                    } else {
                        return super.insertItem(slot, stack, simulate);
                    }
                } else if (slot == 0) {
                    return super.insertItem(slot, stack, simulate);
                } else {
                    return stack;
                }
            }
        };
    }

    public IIntArray getFields() {
        return fields;
    }

    public void syncContents(ServerPlayerEntity player) {
        player.connection.sendPacket(getUpdatePacket());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(getBlockState(), pkt.getNbtCompound());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

}
