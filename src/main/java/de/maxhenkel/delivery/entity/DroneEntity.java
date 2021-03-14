package de.maxhenkel.delivery.entity;

import de.maxhenkel.delivery.ITiered;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.sounds.ModSounds;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.client.Minecraft;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class DroneEntity extends DroneEntitySoundBase {

    private static final DataParameter<ItemStack> PAYLOAD = EntityDataManager.defineId(DroneEntity.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<BlockPos> PAD_LOCATION = EntityDataManager.defineId(DroneEntity.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Integer> ENERGY = EntityDataManager.defineId(DroneEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> TIER = EntityDataManager.defineId(DroneEntity.class, DataSerializers.INT);

    public DroneEntity(World worldIn) {
        super(ModEntities.DRONE, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        moveDrone();

        if (level.isClientSide) {
            return;
        }

        xRot = 0F;
        xRotO = 0F;

        if (level.getGameTime() % 20 == 0) {
            onEverySecond();
        }

        if (!isOnPad()) {
            if (horizontalCollision) {
                explode();
            }
            if (verticalCollision && getDeltaMovement().y == 0D) {
                explode();
            }
        }

        if (isInWaterOrBubble() || isInLava()) {
            explode();
        }
    }

    public void onEverySecond() {
        DronePadTileEntity dronePad = getDronePad();
        if (dronePad == null) {
            explode();
            return;
        }

        if (dronePad.getDroneID() == null || !dronePad.getDroneID().equals(getUUID())) {
            explode();
            return;
        }

        if (getY() > dronePad.getBlockPos().getY() + level.getHeight()) {
            if (!getPayload().isEmpty()) {
                Group group = dronePad.getGroup();
                if (group == null) {
                    explode();
                } else {
                    group.handInTaskItems(NonNullList.of(ItemStack.EMPTY, getPayload().copy()));
                    setPayload(ItemStack.EMPTY);
                }
            }
        }
    }

    @Nullable
    public DronePadTileEntity getDronePad() {
        TileEntity tileEntity = level.getBlockEntity(getPadLocation());
        if (tileEntity instanceof DronePadTileEntity) {
            return (DronePadTileEntity) tileEntity;
        }
        return null;
    }

    public void moveDrone() {
        Vector3d oldMotion = getDeltaMovement();
        Vector3d newMotion = Vector3d.ZERO;

        double oldYMotion = oldMotion.y;

        double speedPerc = Math.max(Math.min(position().subtract(getPadVector()).y, 1D), 0.05D);

        if (getEnergy() < DronePadTileEntity.ENERGY_CAPACITY && isOnPad()) {
            // Wait for full charge
            decreasePropellerSpeed();
        } else if (getEnergy() <= 0 && !isOnPad() && !verticalCollision) {
            newMotion = new Vector3d(0D, Math.min(oldYMotion - 0.01D, -0.2D), 0D);
            decreasePropellerSpeed();
        } else if (getPayload().isEmpty()) {
            if (!isOnPad() && !verticalCollision) {
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

        setDeltaMovement(newMotion);
        if (newMotion.length() > 0D) {
            move(MoverType.SELF, newMotion);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        if (level.isClientSide) {
            playCrashSound();
        }
        return super.causeFallDamage(distance, damageMultiplier);
    }

    @OnlyIn(Dist.CLIENT)
    private void playCrashSound() {
        level.playSound(Minecraft.getInstance().player, getX(), getY(), getZ(), ModSounds.DRONE_CRASH, SoundCategory.NEUTRAL, 1F, 1F);
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
        double diff = position().y - padLocation.getY();
        return blockPosition().equals(padLocation) && diff > 1.8F / 16 && diff < 2.2F / 16;
    }

    public Vector3d moveHorizontal() {
        Vector3d moveVec = getPadVector().multiply(1D, 0D, 1D).subtract(position().multiply(1D, 0D, 1D));
        if (moveVec.x >= 16D || moveVec.z >= 16D) {
            explode();
        }
        return new Vector3d(Math.min(moveVec.x(), 0.1D), Math.min(moveVec.y(), 0.1D), Math.min(moveVec.z(), 0.1D));
    }

    public void explode() {
        if (removed) {
            return;
        }
        level.explode(this, getX(), getY(), getZ(), 1, Explosion.Mode.NONE);
        InventoryHelper.dropItemStack(level, getX(), getY(), getZ(), getPayload());
        remove();
    }

    @Override
    public boolean isLoaded() {
        return !getPayload().isEmpty();
    }

    public ItemStack getPayload() {
        return entityData.get(PAYLOAD);
    }

    public void setPayload(ItemStack stack) {
        entityData.set(PAYLOAD, stack);
    }

    public BlockPos getPadLocation() {
        return entityData.get(PAD_LOCATION);
    }

    public Vector3d getPadVector() {
        BlockPos padLocation = getPadLocation();
        DronePadTileEntity dronePad = getDronePad();
        if (dronePad != null) {
            Direction direction = dronePad.getBlockState().getValue(HorizontalRotatableBlock.FACING);
            return new Vector3d(padLocation.getX() + 0.5D + direction.getStepX() * 1D / 16D, padLocation.getY() + 2D / 16D, padLocation.getZ() + 0.5D + direction.getStepZ() * 1D / 16D);
        }
        return new Vector3d(padLocation.getX() + 0.5D, padLocation.getY() + 2D / 16D, padLocation.getZ() + 0.5D);
    }

    public void setPadLocation(BlockPos pos) {
        entityData.set(PAD_LOCATION, pos);
    }

    public int getEnergy() {
        return entityData.get(ENERGY);
    }

    public void setEnergy(int energy) {
        entityData.set(ENERGY, Math.max(0, energy));
    }

    public int getTier() {
        return entityData.get(TIER);
    }

    public void setTier(int tier) {
        entityData.set(TIER, Math.min(Math.max(0, tier), 6));
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(PAYLOAD, ItemStack.EMPTY);
        entityData.define(PAD_LOCATION, new BlockPos(0, -1, 0));
        entityData.define(ENERGY, 0);
        entityData.define(TIER, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        setPayload(ItemStack.of(compound.getCompound("Payload")));
        CompoundNBT padLocation = compound.getCompound("PadLocation");
        setPadLocation(new BlockPos(padLocation.getInt("X"), padLocation.getInt("Y"), padLocation.getInt("Z")));
        setEnergy(compound.getInt("Energy"));
        setTier(compound.getInt("Tier"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        compound.put("Payload", entityData.get(PAYLOAD).save(new CompoundNBT()));
        CompoundNBT padLocation = new CompoundNBT();
        BlockPos loc = entityData.get(PAD_LOCATION);
        padLocation.putInt("X", loc.getX());
        padLocation.putInt("Y", loc.getY());
        padLocation.putInt("Z", loc.getZ());
        compound.put("PadLocation", padLocation);
        compound.putInt("Energy", entityData.get(ENERGY));
        compound.putInt("Tier", entityData.get(TIER));
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
