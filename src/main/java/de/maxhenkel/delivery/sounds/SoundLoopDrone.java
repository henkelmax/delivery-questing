package de.maxhenkel.delivery.sounds;

import de.maxhenkel.delivery.entity.DroneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class SoundLoopDrone extends AbstractTickableSoundInstance {

    protected DroneEntity drone;

    public SoundLoopDrone(DroneEntity drone, SoundEvent event, SoundSource category) {
        super(event, category);
        this.drone = drone;
        this.looping = true;
        this.delay = 0;
        this.volume = 1F;
        this.pitch = 1F;
        this.relative = false;
        this.attenuation = Attenuation.LINEAR;
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

        LocalPlayer player = Minecraft.getInstance().player;
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
