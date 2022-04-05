package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.IServerTickableBlockEntity;
import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.items.UpgradeItem;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.ITaskContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

public class PackagerTileEntity extends BlockEntity implements IServerTickableBlockEntity {

    private static final int TANK_CAPACITY = 16_000;
    private static final int ENERGY_CAPACITY = 16_000;

    private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return energy.getEnergyStored() + 1;
                case 1:
                    return tank.getFluidAmount() + 1;
            }
            return 0;
        }

        @Override
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

        @Override
        public int getCount() {
            return 2;
        }
    };

    private FluidTank tank;
    private UsableEnergyStorage energy;
    private NonNullList<ItemStack> inventory;
    private NonNullList<ItemStack> upgradeInventory;

    protected LazyOptional<FluidTank> tankCache;
    protected LazyOptional<UsableEnergyStorage> energyCache;
    protected LazyOptional<IItemHandler> itemCache;

    public PackagerTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.PACKAGER, pos, state);
        tank = new FluidTank(TANK_CAPACITY);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0);
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);

        tankCache = LazyOptional.of(() -> tank);
        energyCache = LazyOptional.of(() -> energy);
        itemCache = LazyOptional.of(this::createItemHandler);
    }

    @Override
    public void tickServer() {
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

        if (level.getGameTime() % 4 != 0) {
            return;
        }

        ItemStack toInsert = inventory.get(0);
        if (!toInsert.isEmpty() && taskContainer.canAcceptItems(taskContainerStack) && energy.getEnergyStored() >= getItemEnergyUsage()) {
            int count = getCount();
            ItemStack rest = taskContainer.add(taskContainerStack, toInsert, Math.min(count, toInsert.getCount()));
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

    public Container getInventory() {
        return new ItemListInventory(inventory, this::setChanged);
    }

    public Container getUpgradeInventory() {
        return new ItemListInventory(upgradeInventory, this::setChanged);
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
    public void load(CompoundTag compound) {
        super.load(compound);
        tank = new FluidTank(TANK_CAPACITY);
        tank.readFromNBT(compound.getCompound("Fluid"));
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, compound.getInt("Energy"));
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound.getCompound("Inventory"), inventory);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound.getCompound("UpgradeInventory"), upgradeInventory);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Fluid", tank.writeToNBT(new CompoundTag()));
        compound.putInt("Energy", energy.getEnergyStored());
        compound.put("Inventory", ContainerHelper.saveAllItems(new CompoundTag(), inventory, true));
        compound.put("UpgradeInventory", ContainerHelper.saveAllItems(new CompoundTag(), upgradeInventory, true));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        tankCache.invalidate();
        energyCache.invalidate();
        itemCache.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (remove) {
            return super.getCapability(cap, side);
        }

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return tankCache.cast();
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energyCache.cast();
        } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemCache.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandler createItemHandler() {
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

    public ContainerData getFields() {
        return fields;
    }

    public void syncContents(ServerPlayer player) {
        player.connection.send(getUpdatePacket());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag updateTag = super.getUpdateTag();
        saveAdditional(updateTag);
        return updateTag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
