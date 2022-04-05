package de.maxhenkel.delivery.capability;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityEvents {

    public static final ResourceLocation PROGRESSION_CAPABILITY = new ResourceLocation(Main.MODID, "progression");

    @SubscribeEvent
    public void onCapabilityAttach(AttachCapabilitiesEvent<Level> event) {
        if (!event.getObject().dimension().equals(Level.OVERWORLD)) {
            return;
        }
        if (event.getObject().getCapability(Main.PROGRESSION_CAPABILITY).isPresent()) {
            return;
        }

        event.addCapability(PROGRESSION_CAPABILITY, new ProgressionCapabilityProvider(new Progression()));
    }

}
