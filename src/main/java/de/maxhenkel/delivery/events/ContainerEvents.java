package de.maxhenkel.delivery.events;

import de.maxhenkel.delivery.gui.computer.ComputerContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ContainerEvents {

    @SubscribeEvent
    public void onContainerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayerEntity) {
            if (event.player.openContainer instanceof ComputerContainer) {
                ComputerContainer container = (ComputerContainer) event.player.openContainer;
                container.tick();
            }
        }
    }

}
