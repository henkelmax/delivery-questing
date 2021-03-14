package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.entity.DroneEntity;
import de.maxhenkel.delivery.items.UpgradeItem;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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

public class DronePadTileEntity extends GroupTileEntity implements ITickableTileEntity {

    public static final int ENERGY_CAPACITY = 16_000;

    private final IIntArray fields = new IIntArray() {
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

    public DronePadTileEntity() {
        super(ModTileEntities.DRONE_PAD);
        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        temporaryDroneInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0);
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
                new AxisAlignedBB(getBlockPos().getX() - 16, -16, getBlockPos().getZ() - 16, getBlockPos().getX() + 16, level.getHeight() + 256, getBlockPos().getZ() + 16),
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
        drone.yRot = direction.toYRot();
        level.addFreshEntity(drone);
        setDroneID(drone.getUUID());
        return drone;
    }

    public boolean isSkyFree() {
        BlockPos.Mutable p = new BlockPos.Mutable(worldPosition.getX(), 0, worldPosition.getZ());
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (remove) {
            return super.getCapability(cap, side);
        }
        if (side == null || side.equals(Direction.DOWN) || side.equals(getBlockState().getValue(HorizontalRotatableBlock.FACING).getOpposite())) {
            if (cap == CapabilityEnergy.ENERGY) {
                return LazyOptional.of(() -> energy).cast();
            } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return LazyOptional.of(this::getItemHandler).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    public IInventory getInventory() {
        return new ItemListInventory(inventory, this::setChanged);
    }

    public IInventory getUpgradeInventory() {
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

    public IInventory getTemporaryDroneInventory() {
        return new ItemListInventory(temporaryDroneInventory, () -> {
        });
    }

    private IItemHandler getItemHandler() {
        return new ItemStackHandler(inventory) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return super.isItemValid(slot, stack) && Group.getTaskContainer(stack) != null;
            }
        };
    }

    public IIntArray getFields() {
        return fields;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("Inventory"), inventory);
        upgradeInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("UpgradeInventory"), upgradeInventory);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, compound.getInt("Energy"));
        if (compound.contains("DroneID")) {
            droneID = compound.getUUID("DroneID");
        } else {
            droneID = null;
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.put("Inventory", ItemStackHelper.saveAllItems(new CompoundNBT(), inventory, true));
        compound.put("UpgradeInventory", ItemStackHelper.saveAllItems(new CompoundNBT(), upgradeInventory, true));
        compound.putInt("Energy", energy.getEnergyStored());
        if (droneID != null) {
            compound.putUUID("DroneID", droneID);
        }
        return super.save(compound);
    }
}
