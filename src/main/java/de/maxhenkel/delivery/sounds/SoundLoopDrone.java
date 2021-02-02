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
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1F;
        this.pitch = 1F;
        this.priority = true;
        this.global = false;
        this.attenuationType = AttenuationType.LINEAR;
        this.updatePos();
    }

    public void updatePos() {
        this.x = (float) drone.getPosX();
        this.y = (float) drone.getPosY();
        this.z = (float) drone.getPosZ();
    }

    @Override
    public void tick() {
        if (isDonePlaying()) {
            return;
        }

        if (!drone.isAlive()) {
            finishPlaying();
            return;
        }

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive()) {
            finishPlaying();
            return;
        }

        if (shouldStopSound()) {
            finishPlaying();
            return;
        }

        updatePos();
    }

    public abstract boolean shouldStopSound();

}
