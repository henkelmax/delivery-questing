package de.maxhenkel.delivery.events;

import de.maxhenkel.delivery.integration.jei.NoDisplayTextComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TooltipEvents {

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent.Pre event) {
        if (event.getComponents().stream().anyMatch(text -> text instanceof NoDisplayTextComponent)) {
            event.setCanceled(true);
        }
    }

}
