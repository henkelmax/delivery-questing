package de.maxhenkel.delivery.entity;

import de.maxhenkel.delivery.sounds.ModSounds;
import de.maxhenkel.delivery.sounds.SoundLoopDroneFlying;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class DroneEntitySoundBase extends DroneEntityPropellerBase {

    @OnlyIn(Dist.CLIENT)
    private SoundLoopDroneFlying droneSound;

    public DroneEntitySoundBase(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            return;
        }
        if (Math.abs(getDeltaMovement().y) > 0D && ((DroneEntity) this).getEnergy() > 0) {
            checkHighLoop();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkHighLoop() {
        if (!isSoundPlaying(droneSound)) {
            droneSound = new SoundLoopDroneFlying((DroneEntity) this, ModSounds.DRONE, SoundSource.NEUTRAL);
            ModSounds.playSoundLoop(droneSound, level);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSoundPlaying(SoundInstance sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }

}
