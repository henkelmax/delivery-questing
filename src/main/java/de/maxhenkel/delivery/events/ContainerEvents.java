package de.maxhenkel.delivery.events;

import de.maxhenkel.delivery.gui.computer.ComputerContainer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ContainerEvents {

    @SubscribeEvent
    public void onContainerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer) {
            if (event.player.containerMenu instanceof ComputerContainer) {
                ComputerContainer container = (ComputerContainer) event.player.containerMenu;
                container.tick();
            }
        }
    }

}
