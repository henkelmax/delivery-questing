package de.maxhenkel.delivery.entity;

import de.maxhenkel.delivery.ITiered;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.sounds.ModSounds;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class DroneEntity extends DroneEntitySoundBase {

    private static final EntityDataAccessor<ItemStack> PAYLOAD = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<BlockPos> PAD_LOCATION = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> ENERGY = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TIER = SynchedEntityData.defineId(DroneEntity.class, EntityDataSerializers.INT);

    public DroneEntity(Level worldIn) {
        super(ModEntities.DRONE, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        moveDrone();

        if (level.isClientSide) {
            return;
        }

        xo = 0F;
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
        BlockEntity tileEntity = level.getBlockEntity(getPadLocation());
        if (tileEntity instanceof DronePadTileEntity) {
            return (DronePadTileEntity) tileEntity;
        }
        return null;
    }

    public void moveDrone() {
        Vec3 oldMotion = getDeltaMovement();
        Vec3 newMotion = Vec3.ZERO;

        double oldYMotion = oldMotion.y;

        double speedPerc = Math.max(Math.min(position().subtract(getPadVector()).y, 1D), 0.05D);

        if (getEnergy() < DronePadTileEntity.ENERGY_CAPACITY && isOnPad()) {
            // Wait for full charge
            decreasePropellerSpeed();
        } else if (getEnergy() <= 0 && !isOnPad() && !verticalCollision) {
            newMotion = new Vec3(0D, Math.min(oldYMotion - 0.01D, -0.2D), 0D);
            decreasePropellerSpeed();
        } else if (getPayload().isEmpty()) {
            if (!isOnPad() && !verticalCollision) {
                newMotion = moveHorizontal().add(new Vec3(0D, speedPerc * -0.2D, 0D));
                setEnergy(getEnergy() - 1);
                increasePropellerSpeed(0.75F);
            } else {
                decreasePropellerSpeed();
            }
        } else {
            newMotion = moveHorizontal().add(new Vec3(0D, speedPerc * getRiseSpeed(), 0D));
            setEnergy(getEnergy() - 5);
            increasePropellerSpeed(1F);
        }

        setDeltaMovement(newMotion);
        if (newMotion.length() > 0D) {
            move(MoverType.SELF, newMotion);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        if (level.isClientSide) {
            playCrashSound();
        }
        return super.causeFallDamage(distance, damageMultiplier, source);
    }

    @OnlyIn(Dist.CLIENT)
    private void playCrashSound() {
        level.playSound(Minecraft.getInstance().player, getX(), getY(), getZ(), ModSounds.DRONE_CRASH, SoundSource.NEUTRAL, 1F, 1F);
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

    public Vec3 moveHorizontal() {
        Vec3 moveVec = getPadVector().multiply(1D, 0D, 1D).subtract(position().multiply(1D, 0D, 1D));
        if (moveVec.x >= 16D || moveVec.z >= 16D) {
            explode();
        }
        return new Vec3(Math.min(moveVec.x(), 0.1D), Math.min(moveVec.y(), 0.1D), Math.min(moveVec.z(), 0.1D));
    }

    public void explode() {
        if (isRemoved()) {
            return;
        }
        level.explode(this, getX(), getY(), getZ(), 1, Explosion.BlockInteraction.NONE);
        Containers.dropItemStack(level, getX(), getY(), getZ(), getPayload());
        setRemoved(RemovalReason.KILLED);
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

    public Vec3 getPadVector() {
        BlockPos padLocation = getPadLocation();
        DronePadTileEntity dronePad = getDronePad();
        if (dronePad != null) {
            Direction direction = dronePad.getBlockState().getValue(HorizontalRotatableBlock.FACING);
            return new Vec3(padLocation.getX() + 0.5D + direction.getStepX() * 1D / 16D, padLocation.getY() + 2D / 16D, padLocation.getZ() + 0.5D + direction.getStepZ() * 1D / 16D);
        }
        return new Vec3(padLocation.getX() + 0.5D, padLocation.getY() + 2D / 16D, padLocation.getZ() + 0.5D);
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
    protected void readAdditionalSaveData(CompoundTag compound) {
        setPayload(ItemStack.of(compound.getCompound("Payload")));
        CompoundTag padLocation = compound.getCompound("PadLocation");
        setPadLocation(new BlockPos(padLocation.getInt("X"), padLocation.getInt("Y"), padLocation.getInt("Z")));
        setEnergy(compound.getInt("Energy"));
        setTier(compound.getInt("Tier"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.put("Payload", entityData.get(PAYLOAD).save(new CompoundTag()));
        CompoundTag padLocation = new CompoundTag();
        BlockPos loc = entityData.get(PAD_LOCATION);
        padLocation.putInt("X", loc.getX());
        padLocation.putInt("Y", loc.getY());
        padLocation.putInt("Z", loc.getZ());
        compound.put("PadLocation", padLocation);
        compound.putInt("Energy", entityData.get(ENERGY));
        compound.putInt("Tier", entityData.get(TIER));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
