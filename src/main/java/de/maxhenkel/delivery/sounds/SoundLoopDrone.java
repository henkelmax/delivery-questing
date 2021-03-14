package de.maxhenkel.delivery.sounds;

import de.maxhenkel.delivery.entity.DroneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public abstract class SoundLoopDrone extends TickableSound {

    protected DroneEntity drone;

    public SoundLoopDrone(DroneEntity drone, SoundEvent event, SoundCategory category) {
        super(event, category);
        this.drone = drone;
        this.looping = true;
        this.delay = 0;
        this.volume = 1F;
        this.pitch = 1F;
        this.priority = true;
        this.relative = false;
        this.attenuation = AttenuationType.LINEAR;
        this.updatePos();
    }

    public void updatePos() {
        this.x = (float) drone.getX();
        this.y = (float) drone.getY();
        this.z = (float) drone.getZ();
    }

    @Override
    public void tick() {
        if (isStopped()) {
            return;
        }

        if (!drone.isAlive()) {
            stop();
            return;
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive()) {
            stop();
            return;
        }

        if (shouldStopSound()) {
            stop();
            return;
        }

        updatePos();
    }

    public abstract boolean shouldStopSound();

}
