package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.blockentity.ITickableBlockEntity;
import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.entity.DroneEntity;
import de.maxhenkel.delivery.items.UpgradeItem;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class DronePadTileEntity extends GroupTileEntity implements ITickableBlockEntity {

    public static final int ENERGY_CAPACITY = 16_000;

    private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return energy.getEnergyStored() + 1;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                energy.setEnergy(value - 1);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    private NonNullList<ItemStack> inventory;
    private NonNullList<ItemStack> upgradeInventory;
    private NonNullList<ItemStack> temporaryDroneInventory;
    private UsableEnergyStorage energy;
    @Nullable
    private UUID droneID;

    private LazyOptional<IItemHandler> itemHandlerCache;
    private LazyOptional<UsableEnergyStorage> energyCache;

    public DronePadTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntities.DRONE_PAD, pos, state);
        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        temporaryDroneInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0);

        itemHandlerCache = LazyOptional.of(this::createItemHandler);
        energyCache = LazyOptional.of(() -> energy);
    }

    @Nullable
    private DroneEntity cachedDrone;

    @Override
    public void tick() {
        cachedDrone = getDrone();

        if (level.isClientSide) {
            return;
        }

        if (cachedDrone != null) {
            if (cachedDrone.isOnPad()) {
                dronePadTick(cachedDrone);
                temporaryDroneInventory.set(0, cachedDrone.getPayload());
            } else {
                temporaryDroneInventory.set(0, ItemStack.EMPTY);
            }
        } else {
            if (!isSkyFree()) {
                temporaryDroneInventory.set(0, ItemStack.EMPTY);
            } else {
                cachedDrone = spawnDrone();
                temporaryDroneInventory.set(0, cachedDrone.getPayload());
            }
        }
    }

    public void dronePadTick(DroneEntity drone) {
        drone.setEnergy(drone.getEnergy() + energy.useEnergy(Math.min(2, ENERGY_CAPACITY - drone.getEnergy()), false));
        setChanged();

        Tier tier = getTier();
        int t = 0;
        if (tier != null) {
            t = tier.getTier();
        }
        if (drone.getTier() != t) {
            drone.setTier(t);
        }

        if (!inventory.get(0).isEmpty() && drone.getPayload().isEmpty()) {
            drone.setPayload(inventory.get(0).copy());
            inventory.set(0, ItemStack.EMPTY);
        }
    }

    @Nullable
    public DroneEntity getDroneCached() {
        return cachedDrone;
    }

    @Nullable
    public DroneEntity getCachedDroneOnPad() {
        DroneEntity droneCached = getDroneCached();
        if (droneCached != null && droneCached.isOnPad()) {
            return droneCached;
        }
        return null;
    }

    @Nullable
    public DroneEntity getDrone() {
        List<DroneEntity> drone = level.getEntitiesOfClass(
                DroneEntity.class,
                new AABB(getBlockPos().getX() - 16, level.getMinBuildHeight() - 16, getBlockPos().getZ() - 16, getBlockPos().getX() + 16, level.getMaxBuildHeight() + 256, getBlockPos().getZ() + 16),
                droneEntity -> droneEntity.getPadLocation().equals(getBlockPos()) && (level.isClientSide || droneEntity.getUUID().equals(droneID))
        );
        return drone.stream().findAny().orElse(null);
    }

    public DroneEntity spawnDrone() {
        Direction direction = getBlockState().getValue(HorizontalRotatableBlock.FACING);
        DroneEntity drone = new DroneEntity(level);
        drone.setPos(getBlockPos().getX() + 0.5D + direction.getStepX() * 1D / 16D, getBlockPos().getY() + 32, getBlockPos().getZ() + 0.5D + direction.getStepZ() * 1D / 16D);
        drone.setPadLocation(getBlockPos());
        drone.setEnergy(16000);
        drone.yRotO = direction.toYRot();
        level.addFreshEntity(drone);
        setDroneID(drone.getUUID());
        return drone;
    }

    public boolean isSkyFree() {
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(worldPosition.getX(), 0, worldPosition.getZ());
        for (int y = worldPosition.getY() + 1; y < level.getHeight(); y++) {
            p.setY(y);
            if (!level.isEmptyBlock(p)) {
                return false;
            }
        }
        return true;
    }

    public UsableEnergyStorage getEnergy() {
        return energy;
    }

    @Nullable
    public UUID getDroneID() {
        return droneID;
    }

    public void setDroneID(@Nullable UUID droneID) {
        this.droneID = droneID;
        setChanged();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        itemHandlerCache.invalidate();
        energyCache.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (remove) {
            return super.getCapability(cap, side);
        }
        if (side == null || side.equals(Direction.DOWN) || side.equals(getBlockState().getValue(HorizontalRotatableBlock.FACING).getOpposite())) {
            if (cap == CapabilityEnergy.ENERGY) {
                return energyCache.cast();
            } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return itemHandlerCache.cast();
            }
        }
        return super.getCapability(cap, side);
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

    public Container getTemporaryDroneInventory() {
        return new ItemListInventory(temporaryDroneInventory, () -> {
        });
    }

    private IItemHandler createItemHandler() {
        return new ItemStackHandler(inventory) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return super.isItemValid(slot, stack) && Group.getTaskContainer(stack) != null;
            }
        };
    }

    public ContainerData getFields() {
        return fields;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound.getCompound("Inventory"), inventory);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound.getCompound("UpgradeInventory"), upgradeInventory);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, compound.getInt("Energy"));
        if (compound.contains("DroneID")) {
            droneID = compound.getUUID("DroneID");
        } else {
            droneID = null;
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Inventory", ContainerHelper.saveAllItems(new CompoundTag(), inventory, true));
        compound.put("UpgradeInventory", ContainerHelper.saveAllItems(new CompoundTag(), upgradeInventory, true));
        compound.putInt("Energy", energy.getEnergyStored());
        if (droneID != null) {
            compound.putUUID("DroneID", droneID);
        }
    }
}
