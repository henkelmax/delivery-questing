package de.maxhenkel.delivery.advancements;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModTriggers {

    public static final LevelTrigger LEVEL_TRIGGER = CriteriaTriggers.register(new LevelTrigger());
    public static final ComputerAgeTrigger COMPUTER_AGE_TRIGGER = CriteriaTriggers.register(new ComputerAgeTrigger());
    public static final AcceptComputerContractTrigger ACCEPT_COMPUTER_CONTRACT_TRIGGER = CriteriaTriggers.register(new AcceptComputerContractTrigger());

    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.getPlayer();

        try {
            Group group = Main.getProgression(player).getPlayerGroup(player.getUUID());
            LEVEL_TRIGGER.trigger(player, (int) group.getLevel());
        } catch (Exception e) {
        }

    }

}
