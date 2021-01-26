package de.maxhenkel.delivery.capability;

import de.maxhenkel.delivery.Main;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityEvents {

    public static final ResourceLocation TASKS_CAPABILITY = new ResourceLocation(Main.MODID, "tasks");

    @SubscribeEvent
    public void onCapabilityAttach(AttachCapabilitiesEvent<World> event) {
        if (!event.getObject().getDimensionKey().equals(Dimension.OVERWORLD)) {
            return;
        }
        if (event.getObject().getCapability(Main.TASKS_CAPABILITY).isPresent()) {
            return;
        }

        event.addCapability(TASKS_CAPABILITY, new TasksCapabilityProvider(new Tasks()));
    }

}
