package de.maxhenkel.delivery.capability;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityEvents {

    public static final ResourceLocation PROGRESSION_CAPABILITY = new ResourceLocation(Main.MODID, "progression");

    @SubscribeEvent
    public void onCapabilityAttach(AttachCapabilitiesEvent<World> event) {
        if (!event.getObject().dimension().equals(Dimension.OVERWORLD)) {
            return;
        }
        if (event.getObject().getCapability(Main.PROGRESSION_CAPABILITY).isPresent()) {
            return;
        }

        event.addCapability(PROGRESSION_CAPABILITY, new ProgressionCapabilityProvider(new Progression()));
    }

}
