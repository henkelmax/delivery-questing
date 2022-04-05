package de.maxhenkel.delivery.sounds;

import de.maxhenkel.delivery.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModSounds {

    // https://freesound.org/people/chestnutjam/sounds/399443/
    public static SoundEvent MOUSE_CLICK = registerSound("mouse_click");
    // https://freesound.org/people/Zott820/sounds/209578/
    public static SoundEvent CASH_REGISTER = registerSound("cash_register");
    // https://freesound.org/people/euromir/sounds/365428/
    public static SoundEvent DRONE = registerSound("drone");
    // https://freesound.org/people/qubodup/sounds/182856/
    public static SoundEvent DRONE_CRASH = registerSound("drone_crash");

    public static SoundEvent registerSound(String soundName) {
        SoundEvent event = new SoundEvent(new ResourceLocation(Main.MODID, soundName));
        event.setRegistryName(new ResourceLocation(Main.MODID, soundName));
        return event;
    }

    @OnlyIn(Dist.CLIENT)
    public static void playSoundLoop(AbstractTickableSoundInstance loop, Level world) {
        if (world.isClientSide) {
            Minecraft.getInstance().getSoundManager().play(loop);
        }
    }

}
