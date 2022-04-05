package de.maxhenkel.delivery.sounds;

import de.maxhenkel.delivery.entity.DroneEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class SoundLoopDroneFlying extends SoundLoopDrone {

    public SoundLoopDroneFlying(DroneEntity drone, SoundEvent event, SoundSource category) {
        super(drone, event, category);
    }

    @Override
    public void tick() {
        pitch = drone.getEnginePitch();
        super.tick();
    }

    @Override
    public boolean shouldStopSound() {
        return drone.getDeltaMovement().length() <= 0D || drone.getEnergy() <= 0;
    }
}
