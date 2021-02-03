package de.maxhenkel.delivery.entity;

import de.maxhenkel.delivery.ITiered;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.MoverType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class DroneEntity extends DroneEntitySoundBase {

    private static final DataParameter<ItemStack> PAYLOAD = EntityDataManager.createKey(DroneEntity.class, DataSerializers.ITEMSTACK);
    private static final DataParameter<BlockPos> PAD_LOCATION = EntityDataManager.createKey(DroneEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Integer> ENERGY = EntityDataManager.createKey(DroneEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIER = EntityDataManager.createKey(DroneEntity.class, DataSerializers.VARINT);

    public DroneEntity(World worldIn) {
        super(ModEntities.DRONE, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        moveDrone();

        if (world.isRemote) {
            return;
        }

        rotationPitch = 0F;
        prevRotationPitch = 0F;

        if (world.getGameTime() % 20 == 0) {
            onEverySecond();
        }

        if (!isOnPad()) {
            if (collidedHorizontally) {
                explode();
            }
            if (collidedVertically && getMotion().y == 0D) {
                explode();
            }
        }

        if (isInWaterOrBubbleColumn()) {
            explode();
        }
    }

    public void onEverySecond() {
        DronePadTileEntity dronePad = getDronePad();
        if (dronePad == null) {
            explode();
            return;
        }

        if (dronePad.getDroneID() == null || !dronePad.getDroneID().equals(getUniqueID())) {
            explode();
            return;
        }

        if (getPosY() > dronePad.getPos().getY() + world.getHeight()) {
            if (!getPayload().isEmpty()) {
                Group group = dronePad.getGroup();
                if (group == null) {
                    explode();
                } else {
                    group.handInTaskItems(NonNullList.from(ItemStack.EMPTY, getPayload().copy()));
                    setPayload(ItemStack.EMPTY);
                }
            }
        }
    }

    @Nullable
    public DronePadTileEntity getDronePad() {
        TileEntity tileEntity = world.getTileEntity(getPadLocation());
        if (tileEntity instanceof DronePadTileEntity) {
            return (DronePadTileEntity) tileEntity;
        }
        return null;
    }

    public void moveDrone() {
        Vector3d oldMotion = getMotion();
        Vector3d newMotion = Vector3d.ZERO;

        double oldYMotion = oldMotion.y;

        double speedPerc = Math.max(Math.min(getPositionVec().subtract(getPadVector()).y, 1D), 0.05D);

        if (getEnergy() < DronePadTileEntity.ENERGY_CAPACITY && isOnPad()) {
            // Wait for full charge
            decreasePropellerSpeed();
        } else if (getEnergy() <= 0 && !isOnPad()) {
            newMotion = new Vector3d(0D, Math.min(oldYMotion - 0.01D, -0.2D), 0D);
            decreasePropellerSpeed();
        } else if (getPayload().isEmpty()) {
            if (!isOnPad() && !collidedVertically) {
                newMotion = moveHorizontal().add(new Vector3d(0D, speedPerc * -0.2D, 0D));
                setEnergy(getEnergy() - 1);
                increasePropellerSpeed(0.75F);
            } else {
                decreasePropellerSpeed();
            }
        } else {
            newMotion = moveHorizontal().add(new Vector3d(0D, speedPerc * getRiseSpeed(), 0D));
            setEnergy(getEnergy() - 5);
            increasePropellerSpeed(1F);
        }

        setMotion(newMotion);
        if (newMotion.length() > 0D) {
            move(MoverType.SELF, newMotion);
        }
    }

    public double getRiseSpeed() {
        double tier = getTier();
        return 0.1D * (tier + 1D) / (double) getPayloadTier();
    }

    public int getPayloadTier() {
        Item payload = getPayload().getItem();

        if (payload instanceof ITiered) {
            return ((ITiered) payload).getTier().getTier();
        }
        if (payload instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) payload;
            if (blockItem.getBlock() instanceof ITiered) {
                return ((ITiered) blockItem.getBlock()).getTier().getTier();
            }
        }
        return 1;
    }

    public boolean isOnPad() {
        BlockPos padLocation = getPadLocation();
        double diff = getPositionVec().y - padLocation.getY();
        return getPosition().equals(padLocation) && diff > 1.8F / 16 && diff < 2.2F / 16;
    }

    public Vector3d moveHorizontal() {
        Vector3d moveVec = getPadVector().mul(1D, 0D, 1D).subtract(getPositionVec().mul(1D, 0D, 1D));
        if (moveVec.x >= 16D || moveVec.z >= 16D) {
            explode();
        }
        return new Vector3d(Math.min(moveVec.getX(), 0.1D), Math.min(moveVec.getY(), 0.1D), Math.min(moveVec.getZ(), 0.1D));
    }

    public void explode() {
        if (removed) {
            return;
        }
        world.createExplosion(this, getPosX(), getPosY(), getPosZ(), 1, Explosion.Mode.NONE);
        InventoryHelper.spawnItemStack(world, getPosX(), getPosY(), getPosZ(), getPayload());
        remove();
    }

    @Override
    public boolean isLoaded() {
        return !getPayload().isEmpty();
    }

    public ItemStack getPayload() {
        return dataManager.get(PAYLOAD);
    }

    public void setPayload(ItemStack stack) {
        dataManager.set(PAYLOAD, stack);
    }

    public BlockPos getPadLocation() {
        return dataManager.get(PAD_LOCATION);
    }

    public Vector3d getPadVector() {
        BlockPos padLocation = getPadLocation();
        DronePadTileEntity dronePad = getDronePad();
        if (dronePad != null) {
            Direction direction = dronePad.getBlockState().get(HorizontalRotatableBlock.FACING);
            return new Vector3d(padLocation.getX() + 0.5D + direction.getXOffset() * 1D / 16D, padLocation.getY() + 2D / 16D, padLocation.getZ() + 0.5D + direction.getZOffset() * 1D / 16D);
        }
        return new Vector3d(padLocation.getX() + 0.5D, padLocation.getY() + 2D / 16D, padLocation.getZ() + 0.5D);
    }

    public void setPadLocation(BlockPos pos) {
        dataManager.set(PAD_LOCATION, pos);
    }

    public int getEnergy() {
        return dataManager.get(ENERGY);
    }

    public void setEnergy(int energy) {
        dataManager.set(ENERGY, Math.max(0, energy));
    }

    public int getTier() {
        return dataManager.get(TIER);
    }

    public void setTier(int tier) {
        dataManager.set(TIER, Math.min(Math.max(0, tier), 6));
    }

    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    protected void registerData() {
        dataManager.register(PAYLOAD, ItemStack.EMPTY);
        dataManager.register(PAD_LOCATION, new BlockPos(0, -1, 0));
        dataManager.register(ENERGY, 0);
        dataManager.register(TIER, 0);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        setPayload(ItemStack.read(compound.getCompound("Payload")));
        CompoundNBT padLocation = compound.getCompound("PadLocation");
        setPadLocation(new BlockPos(padLocation.getInt("X"), padLocation.getInt("Y"), padLocation.getInt("Z")));
        setEnergy(compound.getInt("Energy"));
        setTier(compound.getInt("Tier"));
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.put("Payload", dataManager.get(PAYLOAD).write(new CompoundNBT()));
        CompoundNBT padLocation = new CompoundNBT();
        BlockPos loc = dataManager.get(PAD_LOCATION);
        padLocation.putInt("X", loc.getX());
        padLocation.putInt("Y", loc.getY());
        padLocation.putInt("Z", loc.getZ());
        compound.put("PadLocation", padLocation);
        compound.putInt("Energy", dataManager.get(ENERGY));
        compound.putInt("Tier", dataManager.get(TIER));
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
