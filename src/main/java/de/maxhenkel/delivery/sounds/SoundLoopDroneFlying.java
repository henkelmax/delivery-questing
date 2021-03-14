package de.maxhenkel.delivery.sounds;

import de.maxhenkel.delivery.entity.DroneEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundLoopDroneFlying extends SoundLoopDrone {

    public SoundLoopDroneFlying(DroneEntity drone, SoundEvent event, SoundCategory category) {
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
