package de.maxhenkel.delivery.entity;

import de.maxhenkel.delivery.sounds.ModSounds;
import de.maxhenkel.delivery.sounds.SoundLoopDroneFlying;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class DroneEntitySoundBase extends DroneEntityPropellerBase {

    @OnlyIn(Dist.CLIENT)
    private SoundLoopDroneFlying droneSound;

    public DroneEntitySoundBase(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isRemote) {
            return;
        }
        if (Math.abs(getMotion().y) > 0D && ((DroneEntity) this).getEnergy() > 0) {
            checkHighLoop();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void checkHighLoop() {
        if (!isSoundPlaying(droneSound)) {
            droneSound = new SoundLoopDroneFlying((DroneEntity) this, ModSounds.DRONE, SoundCategory.NEUTRAL);
            ModSounds.playSoundLoop(droneSound, world);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isSoundPlaying(ISound sound) {
        if (sound == null) {
            return false;
        }
        return Minecraft.getInstance().getSoundHandler().isPlaying(sound);
    }

}
