package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.corelib.energy.UsableEnergyStorage;
import de.maxhenkel.corelib.inventory.ItemListInventory;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.entity.DroneEntity;
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
        public int get(int index) {
            if (index == 0) {
                return energy.getEnergyStored();
            }
            return 0;
        }

        public void set(int index, int value) {
            if (index == 0) {
                energy.setEnergy(value);
            }
        }

        public int size() {
            return 1;
        }
    };

    private NonNullList<ItemStack> inventory;
    private NonNullList<ItemStack> temporaryDroneInventory;
    private UsableEnergyStorage energy;
    @Nullable
    private UUID droneID;

    public DronePadTileEntity() {
        super(ModTileEntities.DRONE_PAD);
        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        temporaryDroneInventory = NonNullList.withSize(1, ItemStack.EMPTY);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0);
    }

    @Nullable
    private DroneEntity cachedDrone;

    @Override
    public void tick() {
        cachedDrone = getDrone();

        if (world.isRemote) {
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
        markDirty();

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
        List<DroneEntity> drone = world.getEntitiesWithinAABB(
                DroneEntity.class,
                new AxisAlignedBB(getPos().getX() - 16, -16, getPos().getZ() - 16, getPos().getX() + 16, world.getHeight() + 16, getPos().getZ() + 16),
                droneEntity -> droneEntity.getPadLocation().equals(getPos()) && (world.isRemote || droneEntity.getUniqueID().equals(droneID))
        );
        return drone.stream().findAny().orElse(null);
    }

    public DroneEntity spawnDrone() {
        Direction direction = getBlockState().get(HorizontalRotatableBlock.FACING);
        DroneEntity drone = new DroneEntity(world);
        drone.setPosition(getPos().getX() + 0.5D + direction.getXOffset() * 1D / 16D, getPos().getY() + 32, getPos().getZ() + 0.5D + direction.getZOffset() * 1D / 16D);
        drone.setPadLocation(getPos());
        drone.setEnergy(16000);
        drone.rotationYaw = direction.getHorizontalAngle();
        world.addEntity(drone);
        setDroneID(drone.getUniqueID());
        return drone;
    }

    public boolean isSkyFree() {
        BlockPos.Mutable p = new BlockPos.Mutable(pos.getX(), 0, pos.getZ());
        for (int y = pos.getY() + 1; y < world.getHeight(); y++) {
            p.setY(y);
            if (!world.isAirBlock(p)) {
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
        markDirty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (removed) {
            return super.getCapability(cap, side);
        }
        if (side == null || side.equals(Direction.DOWN) || side.equals(getBlockState().get(HorizontalRotatableBlock.FACING).getOpposite())) {
            if (cap == CapabilityEnergy.ENERGY) {
                return LazyOptional.of(() -> energy).cast();
            } else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return LazyOptional.of(this::getItemHandler).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    public IInventory getInventory() {
        return new ItemListInventory(inventory, this::markDirty);
    }

    public IInventory getTemporaryDroneInventory() {
        return new ItemListInventory(temporaryDroneInventory, () -> {
        });
    }

    private IItemHandler getItemHandler() {
        return new ItemStackHandler(inventory);
    }

    public IIntArray getFields() {
        return fields;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound.getCompound("Inventory"), inventory);
        energy = new UsableEnergyStorage(ENERGY_CAPACITY, ENERGY_CAPACITY, 0, compound.getInt("Energy"));
        if (compound.contains("DroneID")) {
            droneID = compound.getUniqueId("DroneID");
        } else {
            droneID = null;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Inventory", ItemStackHelper.saveAllItems(new CompoundNBT(), inventory, true));
        compound.putInt("Energy", energy.getEnergyStored());
        if (droneID != null) {
            compound.putUniqueId("DroneID", droneID);
        }
        return super.write(compound);
    }
}
