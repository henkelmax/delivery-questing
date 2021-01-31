package de.maxhenkel.delivery.sounds;

import de.maxhenkel.delivery.Main;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSounds {

    // https://freesound.org/people/chestnutjam/sounds/399443/
    public static SoundEvent MOUSE_CLICK = registerSound("mouse_click");
    // https://freesound.org/people/Zott820/sounds/209578/
    public static SoundEvent CASH_REGISTER = registerSound("cash_register");

    public static SoundEvent registerSound(String soundName) {
        SoundEvent event = new SoundEvent(new ResourceLocation(Main.MODID, soundName));
        event.setRegistryName(new ResourceLocation(Main.MODID, soundName));
        return event;
    }

}
